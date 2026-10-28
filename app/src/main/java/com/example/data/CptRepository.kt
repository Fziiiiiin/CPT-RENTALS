package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BillingItem
import com.example.model.DamagePenaltyItem
import com.example.model.InventoryItem
import com.example.model.Invoice
import com.example.model.LedgerTransaction
import com.example.model.UserRole
import com.example.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CptRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("cpt_crm_storage", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    private val _inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _transactions = MutableStateFlow<List<LedgerTransaction>>(emptyList())
    val transactions: StateFlow<List<LedgerTransaction>> = _transactions.asStateFlow()

    val openingFloat: Double = 5000.0

    init {
        loadData()
    }

    private fun loadData() {
        // Load User Session
        val savedUser = prefs.getString("user_role", null)
        if (savedUser != null) {
            val role = if (savedUser == "ADMIN") UserRole.ADMIN else UserRole.WAREHOUSE
            val name = prefs.getString("user_name", role.label) ?: role.label
            val username = prefs.getString("user_username", savedUser.lowercase()) ?: savedUser.lowercase()
            _currentUser.value = UserSession(username, name, role)
        }

        // Load Inventory
        val invRaw = prefs.getString("inventory_data", null)
        if (invRaw != null) {
            _inventory.value = parseInventoryJson(invRaw)
        } else {
            _inventory.value = getDefaultInventory()
            saveInventory()
        }

        // Load Invoices
        val invcRaw = prefs.getString("invoices_data", null)
        if (invcRaw != null) {
            _invoices.value = parseInvoicesJson(invcRaw)
        } else {
            _invoices.value = getDefaultInvoices()
            saveInvoices()
        }

        // Load Transactions
        val txRaw = prefs.getString("transactions_data", null)
        if (txRaw != null) {
            _transactions.value = parseTransactionsJson(txRaw)
        } else {
            _transactions.value = getDefaultTransactions()
            saveTransactions()
        }
    }

    fun login(usernameInput: String, passwordInput: String): Result<UserSession> {
        val u = usernameInput.trim().lowercase()
        val p = passwordInput.trim()

        return if (u == "admin" && p == "admin123") {
            val session = UserSession(username = "admin", name = "System Admin", role = UserRole.ADMIN)
            _currentUser.value = session
            saveUserSession(session)
            Result.success(session)
        } else if (u == "warehouse" && p == "ware123") {
            val session = UserSession(username = "warehouse", name = "Warehouse Incharge", role = UserRole.WAREHOUSE)
            _currentUser.value = session
            saveUserSession(session)
            Result.success(session)
        } else {
            Result.failure(IllegalArgumentException("Invalid credentials. Use admin / admin123 or warehouse / ware123"))
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove("user_role").remove("user_name").remove("user_username").apply()
    }

    private fun saveUserSession(session: UserSession) {
        prefs.edit()
            .putString("user_role", session.role.name)
            .putString("user_name", session.name)
            .putString("user_username", session.username)
            .apply()
    }

    // Inventory operations
    fun addInventoryItem(name: String, category: String, totalQty: Int, unitPrice: Double, userRole: UserRole): Result<InventoryItem> {
        if (userRole == UserRole.WAREHOUSE) {
            return Result.failure(IllegalAccessException("Warehouse Incharge is not permitted to add master inventory items."))
        }
        val cleanName = name.trim()
        if (cleanName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Item name cannot be empty."))
        }

        val newItem = InventoryItem(
            id = "item_${System.currentTimeMillis()}",
            name = cleanName,
            category = category,
            totalQty = totalQty,
            inWarehouse = totalQty,
            onSite = 0,
            damaged = 0,
            unitPrice = unitPrice
        )
        val updated = _inventory.value + newItem
        _inventory.value = updated
        saveInventory()
        return Result.success(newItem)
    }

    fun updateUnitPrice(itemId: String, newPrice: Double, userRole: UserRole): Result<Unit> {
        if (userRole == UserRole.WAREHOUSE) {
            return Result.failure(IllegalAccessException("Warehouse Incharge cannot modify fixed rental item prices."))
        }
        val updated = _inventory.value.map { item ->
            if (item.id == itemId) item.copy(unitPrice = newPrice) else item
        }
        _inventory.value = updated
        saveInventory()
        return Result.success(Unit)
    }

    fun deleteInventoryItem(itemId: String, userRole: UserRole): Result<Unit> {
        if (userRole == UserRole.WAREHOUSE) {
            return Result.failure(IllegalAccessException("Warehouse Incharge cannot delete master inventory items."))
        }
        val updated = _inventory.value.filterNot { it.id == itemId }
        _inventory.value = updated
        saveInventory()
        return Result.success(Unit)
    }

    // Billing & Stock Deduction
    fun createInvoice(
        clientName: String,
        clientPhone: String,
        items: List<BillingItem>,
        damagedItems: List<DamagePenaltyItem>,
        advancePaid: Double,
        prevDueCollected: Double
    ): Result<Invoice> {
        if (clientName.isBlank() || clientPhone.isBlank()) {
            return Result.failure(IllegalArgumentException("Please fill in client name and phone number."))
        }
        if (items.isEmpty() || items.all { it.itemId.isBlank() }) {
            return Result.failure(IllegalArgumentException("Please select at least one rental item."))
        }

        val validItems = items.filter { it.itemId.isNotBlank() && it.qty > 0 }
        if (validItems.isEmpty()) {
            return Result.failure(IllegalArgumentException("Item quantities must be at least 1."))
        }

        // Verify stock in warehouse
        val currentInvMap = _inventory.value.associateBy { it.id }
        for (bi in validItems) {
            val invItem = currentInvMap[bi.itemId]
            if (invItem == null || invItem.inWarehouse < bi.qty) {
                val available = invItem?.inWarehouse ?: 0
                val name = invItem?.name ?: bi.name
                return Result.failure(IllegalStateException("Insufficient stock in warehouse for '$name'. Available: $available"))
            }
        }

        // Deduct from inWarehouse and add to onSite
        val updatedInventory = _inventory.value.map { item ->
            val matched = validItems.find { it.itemId == item.id }
            if (matched != null) {
                item.copy(
                    inWarehouse = item.inWarehouse - matched.qty,
                    onSite = item.onSite + matched.qty
                )
            } else {
                item
            }
        }
        _inventory.value = updatedInventory
        saveInventory()

        val subtotal = validItems.sumOf { it.total }
        val damageSubtotal = damagedItems.sumOf { it.total }
        val grandTotal = subtotal + damageSubtotal
        val balanceDue = grandTotal - advancePaid

        val count = _invoices.value.size + 1
        val year = SimpleDateFormat("yyyy", Locale.US).format(Date())
        val invoiceNumber = String.format(Locale.US, "CPT-%s-%03d", year, count)

        val newInvoice = Invoice(
            id = "inv_${System.currentTimeMillis()}",
            invoiceNumber = invoiceNumber,
            clientName = clientName.trim(),
            clientPhone = clientPhone.trim(),
            items = validItems,
            damagedItems = damagedItems.filter { it.name.isNotBlank() },
            subtotal = subtotal,
            damageSubtotal = damageSubtotal,
            advancePaid = advancePaid,
            previousDueCollected = prevDueCollected,
            balanceDue = balanceDue,
            status = "Active",
            date = getTodayDateStr()
        )

        _invoices.value = listOf(newInvoice) + _invoices.value
        saveInvoices()
        return Result.success(newInvoice)
    }

    fun deleteInvoice(invoice: Invoice, userRole: UserRole): Result<Unit> {
        if (userRole == UserRole.WAREHOUSE && (invoice.advancePaid > 0 || invoice.previousDueCollected > 0)) {
            return Result.failure(IllegalAccessException("Warehouse Incharge cannot delete bills with financial payments attached."))
        }
        _invoices.value = _invoices.value.filterNot { it.id == invoice.id }
        saveInvoices()
        return Result.success(Unit)
    }

    // Ledger Transactions
    fun addTransaction(type: String, category: String, amount: Double, notes: String): Result<LedgerTransaction> {
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero."))
        }
        val tx = LedgerTransaction(
            id = "tx_${System.currentTimeMillis()}",
            type = type,
            category = category,
            amount = amount,
            notes = notes.ifBlank { "Daily entry" },
            date = getTodayDateStr()
        )
        _transactions.value = listOf(tx) + _transactions.value
        saveTransactions()
        return Result.success(tx)
    }

    fun deleteTransaction(transactionId: String) {
        _transactions.value = _transactions.value.filterNot { it.id == transactionId }
        saveTransactions()
    }

    fun getTodayDateStr(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // Serialization & Storage Helpers
    private fun saveInventory() {
        val array = JSONArray()
        for (item in _inventory.value) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("category", item.category)
                put("totalQty", item.totalQty)
                put("inWarehouse", item.inWarehouse)
                put("onSite", item.onSite)
                put("damaged", item.damaged)
                put("unitPrice", item.unitPrice)
            }
            array.put(obj)
        }
        prefs.edit().putString("inventory_data", array.toString()).apply()
    }

    private fun parseInventoryJson(json: String): List<InventoryItem> {
        val list = mutableListOf<InventoryItem>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                InventoryItem(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    totalQty = obj.getInt("totalQty"),
                    inWarehouse = obj.getInt("inWarehouse"),
                    onSite = obj.getInt("onSite"),
                    damaged = obj.optInt("damaged", 0),
                    unitPrice = obj.getDouble("unitPrice")
                )
            )
        }
        return list
    }

    private fun saveInvoices() {
        val array = JSONArray()
        for (inv in _invoices.value) {
            val obj = JSONObject().apply {
                put("id", inv.id)
                put("invoiceNumber", inv.invoiceNumber)
                put("clientName", inv.clientName)
                put("clientPhone", inv.clientPhone)
                put("subtotal", inv.subtotal)
                put("damageSubtotal", inv.damageSubtotal)
                put("advancePaid", inv.advancePaid)
                put("previousDueCollected", inv.previousDueCollected)
                put("balanceDue", inv.balanceDue)
                put("status", inv.status)
                put("date", inv.date)

                val itemsArr = JSONArray()
                for (it in inv.items) {
                    itemsArr.put(JSONObject().apply {
                        put("itemId", it.itemId)
                        put("name", it.name)
                        put("qty", it.qty)
                        put("price", it.price)
                    })
                }
                put("items", itemsArr)

                val damArr = JSONArray()
                for (d in inv.damagedItems) {
                    damArr.put(JSONObject().apply {
                        put("id", d.id)
                        put("name", d.name)
                        put("qty", d.qty)
                        put("penaltyPrice", d.penaltyPrice)
                    })
                }
                put("damagedItems", damArr)
            }
            array.put(obj)
        }
        prefs.edit().putString("invoices_data", array.toString()).apply()
    }

    private fun parseInvoicesJson(json: String): List<Invoice> {
        val list = mutableListOf<Invoice>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val itemsArr = obj.getJSONArray("items")
            val itemsList = mutableListOf<BillingItem>()
            for (j in 0 until itemsArr.length()) {
                val itObj = itemsArr.getJSONObject(j)
                itemsList.add(
                    BillingItem(
                        itemId = itObj.getString("itemId"),
                        name = itObj.getString("name"),
                        qty = itObj.getInt("qty"),
                        price = itObj.getDouble("price")
                    )
                )
            }

            val damArr = obj.optJSONArray("damagedItems")
            val damList = mutableListOf<DamagePenaltyItem>()
            if (damArr != null) {
                for (j in 0 until damArr.length()) {
                    val dObj = damArr.getJSONObject(j)
                    damList.add(
                        DamagePenaltyItem(
                            id = dObj.optString("id", "d_$j"),
                            name = dObj.getString("name"),
                            qty = dObj.getInt("qty"),
                            penaltyPrice = dObj.getDouble("penaltyPrice")
                        )
                    )
                }
            }

            list.add(
                Invoice(
                    id = obj.getString("id"),
                    invoiceNumber = obj.getString("invoiceNumber"),
                    clientName = obj.getString("clientName"),
                    clientPhone = obj.getString("clientPhone"),
                    items = itemsList,
                    damagedItems = damList,
                    subtotal = obj.getDouble("subtotal"),
                    damageSubtotal = obj.optDouble("damageSubtotal", 0.0),
                    advancePaid = obj.getDouble("advancePaid"),
                    previousDueCollected = obj.optDouble("previousDueCollected", 0.0),
                    balanceDue = obj.getDouble("balanceDue"),
                    status = obj.optString("status", "Active"),
                    date = obj.getString("date")
                )
            )
        }
        return list
    }

    private fun saveTransactions() {
        val array = JSONArray()
        for (tx in _transactions.value) {
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("type", tx.type)
                put("category", tx.category)
                put("amount", tx.amount)
                put("notes", tx.notes)
                put("date", tx.date)
            }
            array.put(obj)
        }
        prefs.edit().putString("transactions_data", array.toString()).apply()
    }

    private fun parseTransactionsJson(json: String): List<LedgerTransaction> {
        val list = mutableListOf<LedgerTransaction>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                LedgerTransaction(
                    id = obj.getString("id"),
                    type = obj.getString("type"),
                    category = obj.getString("category"),
                    amount = obj.getDouble("amount"),
                    notes = obj.getString("notes"),
                    date = obj.getString("date")
                )
            )
        }
        return list
    }

    private fun getDefaultInventory(): List<InventoryItem> = listOf(
        InventoryItem("1", "Transparent German Tent (20x30)", "Tents & Structures", 10, 6, 3, 1, 15000.0),
        InventoryItem("2", "White Canopy Tent (10x10)", "Tents & Structures", 25, 20, 4, 1, 2500.0),
        InventoryItem("3", "Round Banquet Tables", "Furniture", 50, 34, 15, 1, 500.0),
        InventoryItem("4", "Chiavari Chairs (Gold)", "Furniture", 300, 215, 80, 5, 100.0),
        InventoryItem("5", "Heavy-Duty Generator (62KVA)", "Power & Cooling", 4, 2, 2, 0, 8000.0),
        InventoryItem("6", "Halogen Floodlights (500W)", "Lighting", 40, 28, 10, 2, 350.0)
    )

    private fun getDefaultInvoices(): List<Invoice> = listOf(
        Invoice(
            id = "inv-101",
            invoiceNumber = "CPT-2026-001",
            clientName = "Rahul Sharma",
            clientPhone = "+919846012345",
            items = listOf(
                BillingItem("1", "Transparent German Tent (20x30)", 3, 15000.0)
            ),
            damagedItems = listOf(
                DamagePenaltyItem("d1", "Chiavari Chairs (Gold)", 2, 500.0)
            ),
            subtotal = 45000.0,
            damageSubtotal = 1000.0,
            advancePaid = 15000.0,
            previousDueCollected = 0.0,
            balanceDue = 31000.0,
            status = "Active",
            date = getTodayDateStr()
        )
    )

    private fun getDefaultTransactions(): List<LedgerTransaction> = listOf(
        LedgerTransaction("t1", "EXPENSE", "Transport Fuel", 2400.0, "Diesel for delivery truck 1", getTodayDateStr()),
        LedgerTransaction("t2", "PURCHASE", "Maintenance", 1200.0, "Tent rope and stake replacements", getTodayDateStr())
    )
}
