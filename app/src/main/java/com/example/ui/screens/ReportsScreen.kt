package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Expense
import com.example.model.Invoice
import com.example.model.Language
import com.example.model.TimeFilter
import com.example.ui.GarageStrings
import com.example.ui.theme.GarageGreen
import com.example.ui.theme.GarageOrange
import com.example.ui.theme.GarageRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    invoices: List<Invoice>,
    expenses: List<Expense>,
    timeFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    language: Language,
    onAddExpense: (String, Double, String, String) -> Unit,
    onDeleteExpense: (String) -> Unit,
    onResetDemoData: () -> Unit
) {
    var showExpenseDialog by remember { mutableStateOf(false) }

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val filteredInvoices = when (timeFilter) {
        TimeFilter.DAILY -> invoices.filter { it.date == todayStr }
        TimeFilter.WEEKLY -> {
            val weekAgo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)
            )
            invoices.filter { it.date >= weekAgo && it.date <= todayStr }
        }
        TimeFilter.MONTHLY -> {
            val curMonth = todayStr.substring(0, 7)
            invoices.filter { it.date.startsWith(curMonth) }
        }
        TimeFilter.ALL -> invoices
    }

    val filteredExpenses = when (timeFilter) {
        TimeFilter.DAILY -> expenses.filter { it.date == todayStr }
        TimeFilter.WEEKLY -> {
            val weekAgo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)
            )
            expenses.filter { it.date >= weekAgo && it.date <= todayStr }
        }
        TimeFilter.MONTHLY -> {
            val curMonth = todayStr.substring(0, 7)
            expenses.filter { it.date.startsWith(curMonth) }
        }
        TimeFilter.ALL -> expenses
    }

    val totalSales = filteredInvoices.sumOf { it.totalAmount }
    val totalPartsCost = filteredInvoices.sumOf { it.totalPartsCost }
    val totalLabourIncome = filteredInvoices.sumOf { it.totalLabour }
    val totalExpenses = filteredExpenses.sumOf { it.amount }
    val netProfit = totalSales - totalPartsCost - totalExpenses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Time Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TimeFilter.values().forEach { filter ->
                    val isSelected = filter == timeFilter
                    val labelKey = when (filter) {
                        TimeFilter.DAILY -> "daily"
                        TimeFilter.WEEKLY -> "weekly"
                        TimeFilter.MONTHLY -> "monthly"
                        TimeFilter.ALL -> "allTime"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NavyPrimary else Color.Transparent)
                            .clickable { onFilterSelected(filter) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = GarageStrings.get(labelKey, language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextMuted
                        )
                    }
                }
            }
        }

        // Net Profit Calculation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GarageStrings.get("netProfitTitle", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (netProfit >= 0) GarageGreen.copy(alpha = 0.15f) else GarageRed.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (netProfit >= 0) "PROFITABLE" else "LOSS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (netProfit >= 0) GarageGreen else GarageRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "₹${netProfit.toInt()}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (netProfit >= 0) GarageGreen else GarageRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = GarageStrings.get("formulaNote", language),
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier
                            .background(SlateBackground, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    )
                }
            }
        }

        // 4 Financial Stat Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Sales", fontSize = 11.sp, color = TextMuted)
                            Text("₹${totalSales.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GarageGreen)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Expenses", fontSize = 11.sp, color = TextMuted)
                            Text("₹${totalExpenses.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GarageOrange)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Parts Cost", fontSize = 11.sp, color = TextMuted)
                            Text("₹${totalPartsCost.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Labour Income", fontSize = 11.sp, color = TextMuted)
                            Text("₹${totalLabourIncome.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                    }
                }
            }
        }

        // Garage Expenses Header & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = GarageStrings.get("expenseHistory", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Button(
                    onClick = { showExpenseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(GarageStrings.get("btnAddExpense", language), fontSize = 11.sp)
                }
            }
        }

        // Expense List
        if (filteredExpenses.isEmpty()) {
            item {
                Text(
                    text = "No expenses recorded in this period",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(filteredExpenses) { exp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = exp.category, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(text = "${exp.date} • ${exp.notes}", fontSize = 11.sp, color = TextMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "₹${exp.amount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GarageOrange)
                            IconButton(onClick = { onDeleteExpense(exp.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GarageRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Settings & Reset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = GarageStrings.get("dataManagement", language), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "Backup and restore data or reset demo configuration", fontSize = 11.sp, color = TextMuted)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onResetDemoData,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(GarageStrings.get("resetDemo", language), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Add Expense Dialog
    if (showExpenseDialog) {
        val categories = listOf("Shop Rent", "Fuel Cost", "Personal Kharch", "Home Kharch", "Raw Material", "Tea & Breakfast", "Light Bill / Utility", "Other")
        var selectedCat by remember { mutableStateOf(categories[0]) }
        var expAmount by remember { mutableStateOf("") }
        var expNotes by remember { mutableStateOf("") }
        var dropdownExp by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text(GarageStrings.get("btnAddExpense", language), fontWeight = FontWeight.Bold, color = GarageOrange) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(expanded = dropdownExp, onExpandedChange = { dropdownExp = !dropdownExp }) {
                        OutlinedTextField(
                            value = selectedCat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExp) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(expanded = dropdownExp, onDismissRequest = { dropdownExp = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = { selectedCat = cat; dropdownExp = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = expAmount,
                        onValueChange = { expAmount = it },
                        label = { Text("Amount (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = expNotes,
                        onValueChange = { expNotes = it },
                        label = { Text("Description / Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = expAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onAddExpense(selectedCat, amt, expNotes.trim(), todayStr)
                            showExpenseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GarageOrange)
                ) {
                    Text(GarageStrings.get("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpenseDialog = false }) {
                    Text(GarageStrings.get("cancel", language))
                }
            }
        )
    }
}
