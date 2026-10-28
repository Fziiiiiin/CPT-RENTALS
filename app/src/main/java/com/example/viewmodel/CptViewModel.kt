package com.example.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CptRepository
import com.example.model.AppTab
import com.example.model.BillingItem
import com.example.model.DamagePenaltyItem
import com.example.model.InventoryItem
import com.example.model.Invoice
import com.example.model.LedgerTransaction
import com.example.model.UiNotification
import com.example.model.UserRole
import com.example.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

data class BillingRowDraft(
    val draftId: String = UUID.randomUUID().toString(),
    val itemId: String = "",
    val name: String = "",
    val qty: String = "1",
    val price: Double = 0.0
)

data class DamageRowDraft(
    val draftId: String = UUID.randomUUID().toString(),
    val name: String = "",
    val qty: String = "1",
    val penaltyPrice: String = "250"
)

class CptViewModel(private val repository: CptRepository) : ViewModel() {

    val currentUser: StateFlow<UserSession?> = repository.currentUser
    val inventory: StateFlow<List<InventoryItem>> = repository.inventory
    val invoices: StateFlow<List<Invoice>> = repository.invoices
    val transactions: StateFlow<List<LedgerTransaction>> = repository.transactions

    private val _activeTab = MutableStateFlow(AppTab.DASHBOARD)
    val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    // Inventory Search & Categories
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _collapsedCategories = MutableStateFlow<Set<String>>(emptySet())
    val collapsedCategories: StateFlow<Set<String>> = _collapsedCategories.asStateFlow()

    // Add Stock Dialog Form
    private val _showAddStockDialog = MutableStateFlow(false)
    val showAddStockDialog: StateFlow<Boolean> = _showAddStockDialog.asStateFlow()

    val newStockName = MutableStateFlow("")
    val newStockCategory = MutableStateFlow("Tents & Structures")
    val newStockTotalQty = MutableStateFlow("1")
    val newStockUnitPrice = MutableStateFlow("1000")

    // Billing Form
    val billingClientName = MutableStateFlow("")
    val billingClientPhone = MutableStateFlow("")
    private val _billingItems = MutableStateFlow<List<BillingRowDraft>>(listOf(BillingRowDraft()))
    val billingItems: StateFlow<List<BillingRowDraft>> = _billingItems.asStateFlow()

    private val _damageItems = MutableStateFlow<List<DamageRowDraft>>(emptyList())
    val damageItems: StateFlow<List<DamageRowDraft>> = _damageItems.asStateFlow()

    val billingAdvancePaid = MutableStateFlow("")
    val billingPrevDueCollected = MutableStateFlow("")

    // Expense Form
    val expenseType = MutableStateFlow("EXPENSE")
    val expenseCategory = MutableStateFlow("Transport Fuel")
    val expenseAmount = MutableStateFlow("")
    val expenseNotes = MutableStateFlow("")

    // Calculations
    val billingSubtotal: StateFlow<Double> = _billingItems.map { rows ->
        rows.sumOf { (it.qty.toIntOrNull() ?: 0) * it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val damageSubtotal: StateFlow<Double> = _damageItems.map { rows ->
        rows.sumOf { (it.qty.toIntOrNull() ?: 0) * (it.penaltyPrice.toDoubleOrNull() ?: 0.0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val grandTotal: StateFlow<Double> = combine(billingSubtotal, damageSubtotal) { r, d ->
        r + d
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balanceDue: StateFlow<Double> = combine(grandTotal, billingAdvancePaid) { total, advStr ->
        total - (advStr.toDoubleOrNull() ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Financial calculations
    val todayCashCollected: StateFlow<Double> = invoices.map { invs ->
        val today = repository.getTodayDateStr()
        invs.filter { it.date == today }.sumOf { it.advancePaid + it.previousDueCollected }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayOutflow: StateFlow<Double> = transactions.map { txs ->
        val today = repository.getTodayDateStr()
        txs.filter { it.date == today }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netCashInHand: StateFlow<Double> = combine(todayCashCollected, todayOutflow) { inFlow, outFlow ->
        repository.openingFloat + inFlow - outFlow
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.openingFloat)

    val totalWarehouseStockUnits: StateFlow<Int> = inventory.map { items ->
        items.sumOf { it.inWarehouse }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val stockMismatches: StateFlow<List<InventoryItem>> = inventory.map { items ->
        items.filterNot { it.isBalanced }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleCategoryCollapse(category: String) {
        val current = _collapsedCategories.value
        _collapsedCategories.value = if (current.contains(category)) {
            current - category
        } else {
            current + category
        }
    }

    fun showNotification(title: String, message: String, isError: Boolean = false) {
        _notification.value = UiNotification(title, message, isError)
    }

    fun dismissNotification() {
        _notification.value = null
    }

    // Auth actions
    fun login(username: String, password: String): Boolean {
        val result = repository.login(username, password)
        return result.fold(
            onSuccess = {
                true
            },
            onFailure = { error ->
                showNotification("Login Failed", error.message ?: "Invalid credentials", isError = true)
                false
            }
        )
    }

    fun quickLogin(role: UserRole) {
        if (role == UserRole.ADMIN) {
            login("admin", "admin123")
        } else {
            login("warehouse", "ware123")
        }
    }

    fun logout() {
        repository.logout()
        _activeTab.value = AppTab.DASHBOARD
    }

    // Master Inventory Actions
    fun openAddStockDialog() {
        val role = currentUser.value?.role
        if (role == UserRole.WAREHOUSE) {
            showNotification("Access Denied", "Warehouse Incharge is not permitted to add master inventory items.", isError = true)
            return
        }
        newStockName.value = ""
        newStockCategory.value = "Tents & Structures"
        newStockTotalQty.value = "1"
        newStockUnitPrice.value = "1000"
        _showAddStockDialog.value = true
    }

    fun closeAddStockDialog() {
        _showAddStockDialog.value = false
    }

    fun submitNewStock() {
        val role = currentUser.value?.role ?: return
        val name = newStockName.value.trim()
        val cat = newStockCategory.value
        val qty = newStockTotalQty.value.toIntOrNull() ?: 1
        val price = newStockUnitPrice.value.toDoubleOrNull() ?: 0.0

        if (name.isEmpty()) {
            showNotification("Error", "Please enter an item name.", isError = true)
            return
        }

        val result = repository.addInventoryItem(name, cat, qty, price, role)
        result.fold(
            onSuccess = {
                _showAddStockDialog.value = false
                showNotification("Success", "New stock item successfully added to inventory.")
            },
            onFailure = { error ->
                showNotification("Error", error.message ?: "Could not add stock", isError = true)
            }
        )
    }

    fun updateItemUnitPrice(itemId: String, newPrice: Double) {
        val role = currentUser.value?.role ?: return
        val result = repository.updateUnitPrice(itemId, newPrice, role)
        result.onFailure { error ->
            showNotification("Restricted Action", error.message ?: "Failed to update price", isError = true)
        }
    }

    fun deleteItem(itemId: String) {
        val role = currentUser.value?.role ?: return
        val result = repository.deleteInventoryItem(itemId, role)
        result.fold(
            onSuccess = {
                showNotification("Success", "Item deleted from inventory.")
            },
            onFailure = { error ->
                showNotification("Restricted Action", error.message ?: "Cannot delete item", isError = true)
            }
        )
    }

    // Billing Form Actions
    fun addBillingRow() {
        _billingItems.value = _billingItems.value + BillingRowDraft()
    }

    fun removeBillingRow(draftId: String) {
        if (_billingItems.value.size > 1) {
            _billingItems.value = _billingItems.value.filterNot { it.draftId == draftId }
        }
    }

    fun updateBillingRowItem(draftId: String, selectedItemId: String) {
        val invItem = inventory.value.find { it.id == selectedItemId }
        _billingItems.value = _billingItems.value.map { row ->
            if (row.draftId == draftId) {
                row.copy(
                    itemId = selectedItemId,
                    name = invItem?.name ?: "",
                    price = invItem?.unitPrice ?: 0.0
                )
            } else row
        }
    }

    fun updateBillingRowQty(draftId: String, qtyStr: String) {
        _billingItems.value = _billingItems.value.map { row ->
            if (row.draftId == draftId) row.copy(qty = qtyStr) else row
        }
    }

    fun addDamageRow() {
        _damageItems.value = _damageItems.value + DamageRowDraft()
    }

    fun removeDamageRow(draftId: String) {
        _damageItems.value = _damageItems.value.filterNot { it.draftId == draftId }
    }

    fun updateDamageRow(draftId: String, name: String? = null, qty: String? = null, penalty: String? = null) {
        _damageItems.value = _damageItems.value.map { row ->
            if (row.draftId == draftId) {
                row.copy(
                    name = name ?: row.name,
                    qty = qty ?: row.qty,
                    penaltyPrice = penalty ?: row.penaltyPrice
                )
            } else row
        }
    }

    fun createBill(context: Context) {
        val clientName = billingClientName.value.trim()
        val clientPhone = billingClientPhone.value.trim()

        if (clientName.isEmpty() || clientPhone.isEmpty()) {
            showNotification("Error", "Please fill in client details and WhatsApp number.", isError = true)
            return
        }

        val items = _billingItems.value.mapNotNull { row ->
            if (row.itemId.isNotBlank()) {
                BillingItem(
                    itemId = row.itemId,
                    name = row.name,
                    qty = row.qty.toIntOrNull() ?: 1,
                    price = row.price
                )
            } else null
        }

        if (items.isEmpty()) {
            showNotification("Error", "Please select at least one rental item.", isError = true)
            return
        }

        val damages = _damageItems.value.mapNotNull { row ->
            if (row.name.isNotBlank()) {
                DamagePenaltyItem(
                    id = row.draftId,
                    name = row.name,
                    qty = row.qty.toIntOrNull() ?: 1,
                    penaltyPrice = row.penaltyPrice.toDoubleOrNull() ?: 0.0
                )
            } else null
        }

        val advance = billingAdvancePaid.value.toDoubleOrNull() ?: 0.0
        val prevDue = billingPrevDueCollected.value.toDoubleOrNull() ?: 0.0

        val result = repository.createInvoice(clientName, clientPhone, items, damages, advance, prevDue)
        result.fold(
            onSuccess = { newInvoice ->
                // Share to WhatsApp
                shareInvoiceViaWhatsApp(context, newInvoice)

                // Reset Form
                billingClientName.value = ""
                billingClientPhone.value = ""
                _billingItems.value = listOf(BillingRowDraft())
                _damageItems.value = emptyList()
                billingAdvancePaid.value = ""
                billingPrevDueCollected.value = ""

                showNotification("Success", "Bill generated successfully! Warehouse stock updated & WhatsApp share triggered.")
                _activeTab.value = AppTab.DASHBOARD
            },
            onFailure = { error ->
                showNotification("Error", error.message ?: "Failed to generate bill", isError = true)
            }
        )
    }

    fun deleteInvoice(invoice: Invoice) {
        val role = currentUser.value?.role ?: return
        val result = repository.deleteInvoice(invoice, role)
        result.fold(
            onSuccess = {
                showNotification("Invoice Deleted", "Invoice #${invoice.invoiceNumber} successfully removed.")
            },
            onFailure = { error ->
                showNotification("Restricted Action", error.message ?: "Cannot delete invoice", isError = true)
            }
        )
    }

    // Ledger Actions
    fun addExpense() {
        val amount = expenseAmount.value.toDoubleOrNull() ?: 0.0
        val category = expenseCategory.value
        val type = expenseType.value
        val notes = expenseNotes.value.trim()

        if (amount <= 0) {
            showNotification("Error", "Please enter a valid amount.", isError = true)
            return
        }

        val result = repository.addTransaction(type, category, amount, notes)
        result.fold(
            onSuccess = {
                expenseAmount.value = ""
                expenseNotes.value = ""
                showNotification("Success", "Daily transaction recorded successfully.")
            },
            onFailure = { error ->
                showNotification("Error", error.message ?: "Failed to log transaction", isError = true)
            }
        )
    }

    // WhatsApp Message Formatting & Intents
    fun buildInvoiceWhatsAppMessage(invoice: Invoice): String {
        val sb = StringBuilder()
        sb.append("*CPT EVENTS & INFRASTRUCTURE*\n")
        sb.append("----------------------------------\n")
        sb.append("*Invoice:* #${invoice.invoiceNumber}\n")
        sb.append("*Client:* ${invoice.clientName}\n")
        sb.append("*Date:* ${invoice.date}\n\n")
        sb.append("*Rental Items:*")
        for (item in invoice.items) {
            sb.append("\n- ${item.name} (x${item.qty}): ₹${"%.0f".format(item.total)}")
        }

        if (invoice.damagedItems.isNotEmpty()) {
            sb.append("\n\n*Damaged / Penalty Items:*")
            for (dam in invoice.damagedItems) {
                sb.append("\n- ${dam.name} (x${dam.qty}): ₹${"%.0f".format(dam.total)}")
            }
        }

        sb.append("\n----------------------------------\n")
        sb.append("*Rental Subtotal:* ₹${"%.0f".format(invoice.subtotal)}\n")
        sb.append("*Damage Penalties:* ₹${"%.0f".format(invoice.damageSubtotal)}\n")
        sb.append("*Grand Total:* ₹${"%.0f".format(invoice.grandTotal)}\n")
        sb.append("*Advance Paid:* ₹${"%.0f".format(invoice.advancePaid)}\n")
        sb.append("*Previous Due Collected:* ₹${"%.0f".format(invoice.previousDueCollected)}\n")
        sb.append("*Balance Due:* ₹${"%.0f".format(invoice.balanceDue)}\n\n")
        sb.append("Thank you for choosing CPT Events! 🙏")
        return sb.toString()
    }

    fun shareInvoiceViaWhatsApp(context: Context, invoice: Invoice) {
        val message = buildInvoiceWhatsAppMessage(invoice)
        val cleanPhone = invoice.clientPhone.replace(Regex("[^0-9]"), "")
        val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general text share chooser
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(sendIntent, "Share Invoice via WhatsApp / SMS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }

    fun buildClosingSummaryText(): String {
        val today = repository.getTodayDateStr()
        val todayInvs = invoices.value.filter { it.date == today }
        val advances = todayInvs.sumOf { it.advancePaid }
        val dues = todayInvs.sumOf { it.previousDueCollected }
        val cashIn = advances + dues

        val todayTxs = transactions.value.filter { it.date == today }
        val expenses = todayTxs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val purchases = todayTxs.filter { it.type == "PURCHASE" }.sumOf { it.amount }
        val netCash = repository.openingFloat + cashIn - (expenses + purchases)
        val mismatches = stockMismatches.value

        return """
*CPT EVENTS & INFRASTRUCTURE*
*DAILY CLOSING SHEET - $today*
----------------------------------
💰 *CASH FLOW SUMMARY*
- Opening Float: ₹${"%.0f".format(repository.openingFloat)}
- Cash Collected Today (Advances): ₹${"%.0f".format(advances)}
- Previous Dues Collected: ₹${"%.0f".format(dues)}
- *Total Cash In:* ₹${"%.0f".format(cashIn)}
- Total Expenses: ₹${"%.0f".format(expenses)}
- Total Purchases: ₹${"%.0f".format(purchases)}
- *Net Cash in Hand:* ₹${"%.0f".format(netCash)}

📦 *STOCK RECONCILIATION*
- Total Tracked Items: ${inventory.value.size} Items
- Status: ${if (mismatches.isEmpty()) "All Stock Balanced ✅" else "Discrepancy Detected ⚠️ (${mismatches.size} items)"}

----------------------------------
Report generated via CPT CRM.
        """.trimIndent()
    }

    fun copyClosingSheetToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val text = buildClosingSummaryText()
        if (clipboard != null) {
            val clip = ClipData.newPlainText("CPT Closing Sheet", text)
            clipboard.setPrimaryClip(clip)
            showNotification("Copied", "Daily closing sheet copied to clipboard successfully!")
        } else {
            showNotification("Error", "Clipboard is unavailable.", isError = true)
        }
    }

    fun shareClosingSheet(context: Context) {
        val text = buildClosingSummaryText()
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(sendIntent, "Share Daily Closing Sheet").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}

class CptViewModelFactory(private val repository: CptRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CptViewModel::class.java)) {
            return CptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
