package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavTab
import com.example.ui.components.GarageBottomNav
import com.example.ui.components.GarageTopBar
import com.example.ui.components.InvoiceReceiptDialog
import com.example.ui.components.PinLockScreen
import com.example.ui.screens.BillScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StockScreen

@Composable
fun GarageApp(
    viewModel: GarageViewModel = viewModel()
) {
    val isLocked by viewModel.isLocked.collectAsState()
    val isPinSetup by viewModel.isPinSetup.collectAsState()
    val enteredPin by viewModel.enteredPin.collectAsState()
    val pinError by viewModel.pinError.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val invoices by viewModel.invoices.collectAsState()

    val custName by viewModel.customerName.collectAsState()
    val custPhone by viewModel.customerPhone.collectAsState()
    val bikeModel by viewModel.bikeModel.collectAsState()
    val selectedServiceIds by viewModel.selectedServiceIds.collectAsState()
    val manualLabour by viewModel.manualLabour.collectAsState()
    val billDiscount by viewModel.billDiscount.collectAsState()
    val billedParts by viewModel.billedParts.collectAsState()

    val lastSavedInvoice by viewModel.lastSavedInvoice.collectAsState()
    val timeFilter by viewModel.timeFilter.collectAsState()

    val lowStockCount = inventory.count { it.stockQty < 3 }

    BackHandler(enabled = !isLocked && currentTab != NavTab.HOME) {
        viewModel.switchTab(NavTab.HOME)
    }

    if (isLocked) {
        PinLockScreen(
            enteredPin = enteredPin,
            pinError = pinError,
            isPinSetup = isPinSetup,
            language = language,
            onDigit = { viewModel.onPinDigit(it) },
            onBackspace = { viewModel.onPinBackspace() },
            onClear = { viewModel.onPinClear() },
            onLanguageSelected = { viewModel.setLanguage(it) }
        )
    } else {
        Scaffold(
            topBar = {
                GarageTopBar(
                    onLockClicked = { viewModel.lockApp() }
                )
            },
            bottomBar = {
                GarageBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.switchTab(it) },
                    lowStockCount = lowStockCount,
                    language = language
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            invoices = invoices,
                            expenses = expenses,
                            inventory = inventory,
                            language = language,
                            onNewBillClick = { viewModel.switchTab(NavTab.BILL) },
                            onAddStockClick = { viewModel.switchTab(NavTab.STOCK) },
                            onAddExpenseClick = { viewModel.switchTab(NavTab.REPORTS) },
                            onViewAllInvoicesClick = { viewModel.switchTab(NavTab.REPORTS) },
                            onInvoiceClick = { invoice ->
                                viewModel.lastSavedInvoice.value = invoice
                            }
                        )
                    }
                    NavTab.BILL -> {
                        BillScreen(
                            customerName = custName,
                            onCustomerNameChange = { viewModel.customerName.value = it },
                            customerPhone = custPhone,
                            onCustomerPhoneChange = { viewModel.customerPhone.value = it },
                            bikeModel = bikeModel,
                            onBikeModelChange = { viewModel.bikeModel.value = it },
                            selectedServiceIds = selectedServiceIds,
                            onToggleService = { viewModel.toggleService(it) },
                            manualLabour = manualLabour,
                            onManualLabourChange = { viewModel.manualLabour.value = it },
                            billDiscount = billDiscount,
                            onBillDiscountChange = { viewModel.billDiscount.value = it },
                            inventory = inventory,
                            billedParts = billedParts,
                            onAddPartToBill = { id, qty -> viewModel.addPartToBill(id, qty) },
                            onRemoveBilledPart = { viewModel.removePartFromBill(it) },
                            onSaveInvoice = { viewModel.saveInvoice() },
                            language = language
                        )
                    }
                    NavTab.STOCK -> {
                        StockScreen(
                            inventory = inventory,
                            language = language,
                            onQuickAddStock = { viewModel.quickAddStock(it) },
                            onSavePart = { id, name, brand, buy, sell, qty ->
                                viewModel.addOrUpdatePart(id, name, brand, buy, sell, qty)
                            },
                            onDeletePart = { viewModel.deletePart(it) }
                        )
                    }
                    NavTab.REPORTS -> {
                        ReportsScreen(
                            invoices = invoices,
                            expenses = expenses,
                            timeFilter = timeFilter,
                            onFilterSelected = { viewModel.timeFilter.value = it },
                            language = language,
                            onAddExpense = { cat, amt, notes, date ->
                                viewModel.addExpense(cat, amt, notes, date)
                            },
                            onDeleteExpense = { viewModel.deleteExpense(it) }
                        )
                    }
                    NavTab.SETTINGS -> {
                        SettingsScreen(
                            currentLanguage = language,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            onLockAppClick = { viewModel.lockApp() },
                            onResetDemoData = { viewModel.resetDemoData() }
                        )
                    }
                }

                // Invoice Saved & WhatsApp Share Dialog
                lastSavedInvoice?.let { inv ->
                    InvoiceReceiptDialog(
                        invoice = inv,
                        language = language,
                        onDismiss = { viewModel.lastSavedInvoice.value = null }
                    )
                }
            }
        }
    }
}
