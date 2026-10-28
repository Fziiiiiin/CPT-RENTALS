package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CptRepository
import com.example.model.BillingItem
import com.example.model.DamagePenaltyItem
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var repository: CptRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        repository = CptRepository(context)
    }

    @Test
    fun readAppNameString() {
        val appName = context.getString(R.string.app_name)
        assertEquals("CPT rentals", appName)
    }

    @Test
    fun loginAdminAndWarehouse() {
        val adminResult = repository.login("admin", "admin123")
        assertTrue(adminResult.isSuccess)
        assertEquals(UserRole.ADMIN, adminResult.getOrNull()?.role)

        val warehouseResult = repository.login("warehouse", "ware123")
        assertTrue(warehouseResult.isSuccess)
        assertEquals(UserRole.WAREHOUSE, warehouseResult.getOrNull()?.role)

        val failedResult = repository.login("admin", "wrongpassword")
        assertTrue(failedResult.isFailure)
    }

    @Test
    fun stockMovementOnInvoiceCreation() {
        // Initial inventory has item "1" with inWarehouse = 6, onSite = 3
        val initialItem = repository.inventory.value.first { it.id == "1" }
        val initialWarehouseStock = initialItem.inWarehouse
        val initialOnSiteStock = initialItem.onSite

        val invoiceResult = repository.createInvoice(
            clientName = "Test Client",
            clientPhone = "+919876543210",
            items = listOf(BillingItem("1", initialItem.name, 2, initialItem.unitPrice)),
            damagedItems = emptyList(),
            advancePaid = 5000.0,
            prevDueCollected = 0.0
        )

        assertTrue(invoiceResult.isSuccess)
        val invoice = invoiceResult.getOrNull()
        assertNotNull(invoice)
        assertEquals(30000.0, invoice?.subtotal ?: 0.0, 0.1)
        assertEquals(25000.0, invoice?.balanceDue ?: 0.0, 0.1)

        val updatedItem = repository.inventory.value.first { it.id == "1" }
        assertEquals(initialWarehouseStock - 2, updatedItem.inWarehouse)
        assertEquals(initialOnSiteStock + 2, updatedItem.onSite)
    }

    @Test
    fun ledgerExpenseOutflow() {
        val initialCount = repository.transactions.value.size
        val txResult = repository.addTransaction(
            type = "EXPENSE",
            category = "Transport Fuel",
            amount = 1500.0,
            notes = "Fuel for truck"
        )

        assertTrue(txResult.isSuccess)
        assertEquals(initialCount + 1, repository.transactions.value.size)
        assertEquals(1500.0, repository.transactions.value.first().amount, 0.1)
    }
}
