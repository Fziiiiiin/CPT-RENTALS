package com.example.model

enum class UserRole(val label: String) {
    ADMIN("System Admin"),
    WAREHOUSE("Warehouse Incharge")
}

data class UserSession(
    val username: String,
    val name: String,
    val role: UserRole
)

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val totalQty: Int,
    val inWarehouse: Int,
    val onSite: Int,
    val damaged: Int = 0,
    val unitPrice: Double
) {
    val isBalanced: Boolean
        get() = totalQty == (inWarehouse + onSite + damaged)
}

data class BillingItem(
    val itemId: String,
    val name: String,
    val qty: Int,
    val price: Double
) {
    val total: Double
        get() = qty * price
}

data class DamagePenaltyItem(
    val id: String,
    val name: String,
    val qty: Int,
    val penaltyPrice: Double
) {
    val total: Double
        get() = qty * penaltyPrice
}

data class Invoice(
    val id: String,
    val invoiceNumber: String,
    val clientName: String,
    val clientPhone: String,
    val items: List<BillingItem>,
    val damagedItems: List<DamagePenaltyItem> = emptyList(),
    val subtotal: Double,
    val damageSubtotal: Double,
    val advancePaid: Double,
    val previousDueCollected: Double = 0.0,
    val balanceDue: Double,
    val status: String = "Active",
    val date: String
) {
    val grandTotal: Double
        get() = subtotal + damageSubtotal
}

data class LedgerTransaction(
    val id: String,
    val type: String, // "EXPENSE" or "PURCHASE"
    val category: String,
    val amount: Double,
    val notes: String,
    val date: String
)

enum class AppTab(val title: String) {
    DASHBOARD("Home"),
    BILLING("Billing"),
    INVENTORY("Stock"),
    CLOSING("Closing")
}

data class UiNotification(
    val title: String,
    val message: String,
    val isError: Boolean = false
)
