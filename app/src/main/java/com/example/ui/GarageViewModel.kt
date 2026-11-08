package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.GarageRepository
import com.example.model.BilledPart
import com.example.model.Expense
import com.example.model.GarageSettings
import com.example.model.InventoryItem
import com.example.model.Invoice
import com.example.model.Language
import com.example.model.NavTab
import com.example.model.TimeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GarageViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = GarageRepository(application.applicationContext)

    private val _settings = MutableStateFlow(repo.loadSettings())
    val settings: StateFlow<GarageSettings> = _settings.asStateFlow()

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _isPinSetup = MutableStateFlow(false)
    val isPinSetup: StateFlow<Boolean> = _isPinSetup.asStateFlow()

    private val _enteredPin = MutableStateFlow("")
    val enteredPin: StateFlow<String> = _enteredPin.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    private var tempNewPin: String? = null

    private val _currentLanguage = MutableStateFlow(
        Language.values().firstOrNull { it.code == _settings.value.selectedLanguage } ?: Language.GU
    )
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _inventory = MutableStateFlow(repo.loadInventory())
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _expenses = MutableStateFlow(repo.loadExpenses())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _invoices = MutableStateFlow(repo.loadInvoices())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    // Billing Form State
    val customerName = MutableStateFlow("")
    val customerPhone = MutableStateFlow("")
    val bikeModel = MutableStateFlow("")
    val selectedServiceIds = MutableStateFlow<Set<String>>(emptySet())
    val manualLabour = MutableStateFlow(0.0)
    val billDiscount = MutableStateFlow(0.0)
    val billedParts = MutableStateFlow<List<BilledPart>>(emptyList())

    // WhatsApp Invoice Dialog state
    val lastSavedInvoice = MutableStateFlow<Invoice?>(null)

    // Reports Time Filter
    val timeFilter = MutableStateFlow(TimeFilter.DAILY)

    init {
        if (!_settings.value.pinSet || _settings.value.pin.isEmpty()) {
            _isPinSetup.value = true
        }
    }

    fun onPinDigit(digit: String) {
        if (_enteredPin.value.length < 4) {
            val updated = _enteredPin.value + digit
            _enteredPin.value = updated
            _pinError.value = null
            if (updated.length == 4) {
                verifyPin(updated)
            }
        }
    }

    fun onPinBackspace() {
        if (_enteredPin.value.isNotEmpty()) {
            _enteredPin.value = _enteredPin.value.dropLast(1)
            _pinError.value = null
        }
    }

    fun onPinClear() {
        _enteredPin.value = ""
        _pinError.value = null
    }

    private fun verifyPin(pin: String) {
        if (_isPinSetup.value) {
            if (tempNewPin == null) {
                tempNewPin = pin
                _enteredPin.value = ""
                _pinError.value = "Confirm your 4-digit PIN"
            } else {
                if (tempNewPin == pin) {
                    val newSettings = _settings.value.copy(pin = pin, pinSet = true)
                    _settings.value = newSettings
                    repo.saveSettings(newSettings)
                    _isPinSetup.value = false
                    _isLocked.value = false
                    _enteredPin.value = ""
                    tempNewPin = null
                } else {
                    _pinError.value = "PINs did not match. Try again."
                    _enteredPin.value = ""
                    tempNewPin = null
                }
            }
        } else {
            if (pin == _settings.value.pin) {
                _isLocked.value = false
                _enteredPin.value = ""
                _pinError.value = null
            } else {
                _pinError.value = "Incorrect PIN. Try again."
                _enteredPin.value = ""
            }
        }
    }

    fun lockApp() {
        _enteredPin.value = ""
        _pinError.value = null
        _isLocked.value = true
    }

    fun setLanguage(language: Language) {
        _currentLanguage.value = language
        val newSettings = _settings.value.copy(selectedLanguage = language.code)
        _settings.value = newSettings
        repo.saveSettings(newSettings)
    }

    fun switchTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun toggleService(serviceId: String) {
        val current = selectedServiceIds.value.toMutableSet()
        if (current.contains(serviceId)) {
            current.remove(serviceId)
        } else {
            current.add(serviceId)
        }
        selectedServiceIds.value = current
    }

    fun addPartToBill(partId: String, qty: Int): Boolean {
        if (qty <= 0) return false
        val part = _inventory.value.find { it.id == partId } ?: return false

        val existing = billedParts.value.find { it.id == partId }
        val currentBilledQty = existing?.qty ?: 0
        if (currentBilledQty + qty > part.stockQty) return false

        val currentList = billedParts.value.toMutableList()
        if (existing != null) {
            val idx = currentList.indexOf(existing)
            val newQty = existing.qty + qty
            currentList[idx] = existing.copy(
                qty = newQty,
                total = newQty * existing.unitRetail
            )
        } else {
            currentList.add(
                BilledPart(
                    id = part.id,
                    name = part.name,
                    brand = part.brand,
                    unitRetail = part.retailPrice,
                    unitPurchase = part.purchasePrice,
                    qty = qty,
                    total = qty * part.retailPrice
                )
            )
        }
        billedParts.value = currentList
        return true
    }

    fun removePartFromBill(partId: String) {
        billedParts.value = billedParts.value.filter { it.id != partId }
    }

    fun saveInvoice(): Invoice? {
        val name = customerName.value.trim()
        val phone = customerPhone.value.trim()
        val bike = bikeModel.value.trim()

        if (name.isEmpty() || phone.length < 10 || bike.isEmpty()) {
            return null
        }

        // Selected services
        val chosenServices = GarageStrings.DEFAULT_SERVICES.filter {
            selectedServiceIds.value.contains(it.id)
        }
        val servicesTotal = chosenServices.sumOf { it.price }
        val partsTotal = billedParts.value.sumOf { it.total }
        val partsCost = billedParts.value.sumOf { it.unitPurchase * it.qty }
        val labour = manualLabour.value
        val discount = billDiscount.value
        val grandTotal = maxOf(0.0, servicesTotal + partsTotal + labour - discount)

        // Deduct inventory
        val currentInv = _inventory.value.toMutableList()
        billedParts.value.forEach { bp ->
            val idx = currentInv.indexOfFirst { it.id == bp.id }
            if (idx != -1) {
                val item = currentInv[idx]
                currentInv[idx] = item.copy(stockQty = maxOf(0, item.stockQty - bp.qty))
            }
        }
        _inventory.value = currentInv
        repo.saveInventory(currentInv)

        val now = Date()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now)
        val invId = "INV-${_invoices.value.size + 1001}"

        val invoice = Invoice(
            id = invId,
            customer = name,
            phone = phone,
            bike = bike,
            servicesSelected = chosenServices,
            partsUsed = billedParts.value,
            totalPartsAmount = partsTotal,
            totalPartsCost = partsCost,
            totalLabour = servicesTotal + labour,
            discount = discount,
            totalAmount = grandTotal,
            estimatedProfit = grandTotal - partsCost,
            date = dateStr,
            time = timeStr
        )

        val updatedInvoices = _invoices.value + invoice
        _invoices.value = updatedInvoices
        repo.saveInvoices(updatedInvoices)

        lastSavedInvoice.value = invoice
        resetBillForm()
        return invoice
    }

    fun resetBillForm() {
        customerName.value = ""
        customerPhone.value = ""
        bikeModel.value = ""
        selectedServiceIds.value = emptySet()
        manualLabour.value = 0.0
        billDiscount.value = 0.0
        billedParts.value = emptyList()
    }

    fun quickAddStock(partId: String) {
        val list = _inventory.value.toMutableList()
        val idx = list.indexOfFirst { it.id == partId }
        if (idx != -1) {
            val item = list[idx]
            list[idx] = item.copy(stockQty = item.stockQty + 1)
            _inventory.value = list
            repo.saveInventory(list)
        }
    }

    fun addOrUpdatePart(
        id: String?,
        name: String,
        brand: String,
        purchase: Double,
        retail: Double,
        qty: Int
    ) {
        val list = _inventory.value.toMutableList()
        if (id != null) {
            val idx = list.indexOfFirst { it.id == id }
            if (idx != -1) {
                list[idx] = list[idx].copy(
                    name = name,
                    brand = brand,
                    purchasePrice = purchase,
                    retailPrice = retail,
                    stockQty = qty
                )
            }
        } else {
            val existing = list.find {
                it.name.equals(name, ignoreCase = true) && it.brand.equals(brand, ignoreCase = true)
            }
            if (existing != null) {
                val idx = list.indexOf(existing)
                list[idx] = existing.copy(
                    purchasePrice = purchase,
                    retailPrice = retail,
                    stockQty = existing.stockQty + qty
                )
            } else {
                list.add(
                    InventoryItem(
                        id = "p_${System.currentTimeMillis()}",
                        name = name,
                        brand = brand,
                        purchasePrice = purchase,
                        retailPrice = retail,
                        stockQty = qty
                    )
                )
            }
        }
        _inventory.value = list
        repo.saveInventory(list)
    }

    fun deletePart(partId: String) {
        val list = _inventory.value.filter { it.id != partId }
        _inventory.value = list
        repo.saveInventory(list)
    }

    fun addExpense(category: String, amount: Double, notes: String, date: String) {
        val newExp = Expense(
            id = "e_${System.currentTimeMillis()}",
            category = category,
            amount = amount,
            date = date,
            notes = notes
        )
        val updated = _expenses.value + newExp
        _expenses.value = updated
        repo.saveExpenses(updated)
    }

    fun deleteExpense(expId: String) {
        val updated = _expenses.value.filter { it.id != expId }
        _expenses.value = updated
        repo.saveExpenses(updated)
    }

    fun resetDemoData() {
        repo.resetToDemoData()
        _inventory.value = GarageRepository.DEFAULT_INVENTORY
        _expenses.value = repo.loadExpenses()
        _invoices.value = repo.loadInvoices()
        _settings.value = repo.loadSettings()
    }
}
