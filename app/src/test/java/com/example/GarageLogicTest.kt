package com.example

import com.example.model.BilledPart
import com.example.model.InventoryItem
import org.junit.Assert.assertEquals
import org.junit.Test

class GarageLogicTest {

    @Test
    fun testNetProfitFormula() {
        val totalSales = 1000.0
        val partsCost = 400.0
        val expenses = 150.0

        // Accurate Formula: Net Profit = Total Sales - Parts Purchase Cost - Expenses
        val netProfit = totalSales - partsCost - expenses
        assertEquals(450.0, netProfit, 0.001)
    }

    @Test
    fun testStockDeduction() {
        val item = InventoryItem("p1", "Castrol Activ 4T", "Castrol", 280.0, 380.0, 10)
        val billedPart = BilledPart("p1", item.name, item.brand, item.retailPrice, item.purchasePrice, 3, 3 * item.retailPrice)

        val updatedStock = maxOf(0, item.stockQty - billedPart.qty)
        assertEquals(7, updatedStock)
    }
}
