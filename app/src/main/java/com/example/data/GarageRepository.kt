package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BilledPart
import com.example.model.Expense
import com.example.model.GarageSettings
import com.example.model.InventoryItem
import com.example.model.Invoice
import com.example.model.ServiceItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GarageRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gj13_garage_prefs", Context.MODE_PRIVATE)

    companion object {
        val DEFAULT_INVENTORY = listOf(
            InventoryItem("p1", "Castrol Activ 4T 20W-40 (1L)", "Castrol", 280.0, 380.0, 18),
            InventoryItem("p2", "Motul 3000 4T Plus 10W-30 (900ml)", "Motul", 270.0, 360.0, 12),
            InventoryItem("p3", "Bosch M4 Spark Plug", "Bosch", 70.0, 120.0, 24),
            InventoryItem("p4", "Hero Splendor Chain Sprocket Kit", "Hero Genuine", 580.0, 750.0, 5),
            InventoryItem("p5", "Brake Shoe Set Front/Rear", "Endurance", 110.0, 170.0, 14),
            InventoryItem("p6", "Clutch Cable Splendor / HF", "Minda", 55.0, 90.0, 8),
            InventoryItem("p7", "Accelerator Cable", "Hero", 50.0, 85.0, 2),
            InventoryItem("p8", "LED Headlight Bulb 35W Super Bright", "Philips", 140.0, 220.0, 6),
            InventoryItem("p9", "Air Filter Foam", "Minda", 40.0, 70.0, 1),
            InventoryItem("p10", "Tubeless Tyre Valve (TR412)", "SpeedPro", 20.0, 50.0, 0)
        )
    }

    fun loadSettings(): GarageSettings {
        val pin = prefs.getString("settings_pin", "1234") ?: "1234"
        val pinSet = prefs.getBoolean("settings_pin_set", true)
        val lang = prefs.getString("settings_lang", "gu") ?: "gu"
        return GarageSettings(pin = pin, pinSet = pinSet, selectedLanguage = lang)
    }

    fun saveSettings(settings: GarageSettings) {
        prefs.edit()
            .putString("settings_pin", settings.pin)
            .putBoolean("settings_pin_set", settings.pinSet)
            .putString("settings_lang", settings.selectedLanguage)
            .apply()
    }

    fun loadInventory(): List<InventoryItem> {
        val jsonStr = prefs.getString("inventory_json", null) ?: return DEFAULT_INVENTORY
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<InventoryItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    InventoryItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        brand = obj.getString("brand"),
                        purchasePrice = obj.getDouble("purchasePrice"),
                        retailPrice = obj.getDouble("retailPrice"),
                        stockQty = obj.getInt("stockQty")
                    )
                )
            }
            if (list.isEmpty()) DEFAULT_INVENTORY else list
        } catch (_: Exception) {
            DEFAULT_INVENTORY
        }
    }

    fun saveInventory(list: List<InventoryItem>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("brand", item.brand)
            obj.put("purchasePrice", item.purchasePrice)
            obj.put("retailPrice", item.retailPrice)
            obj.put("stockQty", item.stockQty)
            array.put(obj)
        }
        prefs.edit().putString("inventory_json", array.toString()).apply()
    }

    fun loadExpenses(): List<Expense> {
        val jsonStr = prefs.getString("expenses_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Expense>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Expense(
                        id = obj.getString("id"),
                        category = obj.getString("category"),
                        amount = obj.getDouble("amount"),
                        date = obj.getString("date"),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveExpenses(list: List<Expense>) {
        val array = JSONArray()
        list.forEach { exp ->
            val obj = JSONObject()
            obj.put("id", exp.id)
            obj.put("category", exp.category)
            obj.put("amount", exp.amount)
            obj.put("date", exp.date)
            obj.put("notes", exp.notes)
            array.put(obj)
        }
        prefs.edit().putString("expenses_json", array.toString()).apply()
    }

    fun loadInvoices(): List<Invoice> {
        val jsonStr = prefs.getString("invoices_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Invoice>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val servicesArray = obj.optJSONArray("services") ?: JSONArray()
                val services = mutableListOf<ServiceItem>()
                for (j in 0 until servicesArray.length()) {
                    val sObj = servicesArray.getJSONObject(j)
                    services.add(ServiceItem(sObj.getString("id"), sObj.getString("key"), sObj.getDouble("price")))
                }

                val partsArray = obj.optJSONArray("parts") ?: JSONArray()
                val parts = mutableListOf<BilledPart>()
                for (j in 0 until partsArray.length()) {
                    val pObj = partsArray.getJSONObject(j)
                    parts.add(
                        BilledPart(
                            id = pObj.getString("id"),
                            name = pObj.getString("name"),
                            brand = pObj.getString("brand"),
                            unitRetail = pObj.getDouble("unitRetail"),
                            unitPurchase = pObj.getDouble("unitPurchase"),
                            qty = pObj.getInt("qty"),
                            total = pObj.getDouble("total")
                        )
                    )
                }

                list.add(
                    Invoice(
                        id = obj.getString("id"),
                        customer = obj.getString("customer"),
                        phone = obj.getString("phone"),
                        bike = obj.getString("bike"),
                        servicesSelected = services,
                        partsUsed = parts,
                        totalPartsAmount = obj.getDouble("totalPartsAmount"),
                        totalPartsCost = obj.getDouble("totalPartsCost"),
                        totalLabour = obj.getDouble("totalLabour"),
                        discount = obj.getDouble("discount"),
                        totalAmount = obj.getDouble("totalAmount"),
                        estimatedProfit = obj.getDouble("estimatedProfit"),
                        date = obj.getString("date"),
                        time = obj.optString("time", "")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveInvoices(list: List<Invoice>) {
        val array = JSONArray()
        list.forEach { inv ->
            val obj = JSONObject()
            obj.put("id", inv.id)
            obj.put("customer", inv.customer)
            obj.put("phone", inv.phone)
            obj.put("bike", inv.bike)
            obj.put("totalPartsAmount", inv.totalPartsAmount)
            obj.put("totalPartsCost", inv.totalPartsCost)
            obj.put("totalLabour", inv.totalLabour)
            obj.put("discount", inv.discount)
            obj.put("totalAmount", inv.totalAmount)
            obj.put("estimatedProfit", inv.estimatedProfit)
            obj.put("date", inv.date)
            obj.put("time", inv.time)

            val sArray = JSONArray()
            inv.servicesSelected.forEach { s ->
                val sObj = JSONObject()
                sObj.put("id", s.id)
                sObj.put("key", s.key)
                sObj.put("price", s.price)
                sArray.put(sObj)
            }
            obj.put("services", sArray)

            val pArray = JSONArray()
            inv.partsUsed.forEach { p ->
                val pObj = JSONObject()
                pObj.put("id", p.id)
                pObj.put("name", p.name)
                pObj.put("brand", p.brand)
                pObj.put("unitRetail", p.unitRetail)
                pObj.put("unitPurchase", p.unitPurchase)
                pObj.put("qty", p.qty)
                pObj.put("total", p.total)
                pArray.put(pObj)
            }
            obj.put("parts", pArray)

            array.put(obj)
        }
        prefs.edit().putString("invoices_json", array.toString()).apply()
    }

    fun resetAllData() {
        saveInvoices(emptyList())
        saveExpenses(emptyList())
        saveInventory(DEFAULT_INVENTORY)
    }

    fun resetToDemoData() {
        resetAllData()
    }
}
