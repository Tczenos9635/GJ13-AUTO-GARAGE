package com.example.model

enum class Language(val code: String, val label: String) {
    EN("en", "ENG"),
    HI("hi", "हिन्दी"),
    GU("gu", "ગુજરાતી")
}

enum class NavTab {
    HOME, BILL, STOCK, REPORTS
}

enum class TimeFilter {
    DAILY, WEEKLY, MONTHLY, ALL
}

data class InventoryItem(
    val id: String,
    val name: String,
    val brand: String,
    val purchasePrice: Double,
    val retailPrice: Double,
    val stockQty: Int
)

data class ServiceItem(
    val id: String,
    val key: String,
    val price: Double
)

data class BilledPart(
    val id: String,
    val name: String,
    val brand: String,
    val unitRetail: Double,
    val unitPurchase: Double,
    val qty: Int,
    val total: Double
)

data class Invoice(
    val id: String,
    val customer: String,
    val phone: String,
    val bike: String,
    val servicesSelected: List<ServiceItem> = emptyList(),
    val partsUsed: List<BilledPart> = emptyList(),
    val totalPartsAmount: Double = 0.0,
    val totalPartsCost: Double = 0.0,
    val totalLabour: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val estimatedProfit: Double = 0.0,
    val date: String = "",
    val time: String = ""
)

data class Expense(
    val id: String,
    val category: String,
    val amount: Double,
    val date: String,
    val notes: String = ""
)

data class GarageSettings(
    val pin: String = "1234",
    val pinSet: Boolean = true,
    val selectedLanguage: String = "gu",
    val garageName: String = "GJ13 AUTO GARAGE",
    val garagePhone: String = "9876543210"
)
