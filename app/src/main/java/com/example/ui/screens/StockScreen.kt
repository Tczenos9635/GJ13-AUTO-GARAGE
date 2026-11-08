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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.model.InventoryItem
import com.example.model.Language
import com.example.ui.GarageStrings
import com.example.ui.theme.GarageGreen
import com.example.ui.theme.GarageOrange
import com.example.ui.theme.GarageRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun StockScreen(
    inventory: List<InventoryItem>,
    language: Language,
    onQuickAddStock: (String) -> Unit,
    onSavePart: (String?, String, String, Double, Double, Int) -> Unit,
    onDeletePart: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterLowStockOnly by remember { mutableStateOf(false) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingPart by remember { mutableStateOf<InventoryItem?>(null) }

    val totalStockVal = inventory.sumOf { it.purchasePrice * it.stockQty }
    val totalRetailVal = inventory.sumOf { it.retailPrice * it.stockQty }
    val lowStockCount = inventory.count { it.stockQty < 3 && it.stockQty > 0 }
    val outStockCount = inventory.count { it.stockQty <= 0 }

    val filteredList = inventory.filter {
        val matchesQuery = it.name.contains(searchQuery, ignoreCase = true) ||
                it.brand.contains(searchQuery, ignoreCase = true)
        if (filterLowStockOnly) matchesQuery && it.stockQty < 3 else matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Stock Stats Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = GarageStrings.get("totalStockVal", language), fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "₹${totalStockVal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text(text = "Retail: ₹${totalRetailVal.toInt()}", fontSize = 10.sp, color = TextMuted)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = GarageStrings.get("totalParts", language), fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "${inventory.size} items", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = "$lowStockCount Low • $outStockCount Out", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GarageRed)
                    }
                }
            }
        }

        // Search & Filter & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search part or brand...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedButton(
                    onClick = { filterLowStockOnly = !filterLowStockOnly },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (filterLowStockOnly) GarageRed else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = if (filterLowStockOnly) Color.White else TextDark,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Button(
                    onClick = {
                        editingPart = null
                        showAddEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Inventory Items List
        items(filteredList) { item ->
            val isOutOfStock = item.stockQty <= 0
            val isLowStock = item.stockQty < 3 && !isOutOfStock

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            if (isOutOfStock) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.LightGray, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("OUT", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                                }
                            } else if (isLowStock) {
                                Box(
                                    modifier = Modifier
                                        .background(GarageRed.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("LOW (${item.stockQty})", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = GarageRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.brand} • Buy: ₹${item.purchasePrice.toInt()} | Sell: ₹${item.retailPrice.toInt()}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Stock: ${item.stockQty} pcs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.stockQty < 3) GarageRed else GarageGreen
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Quick +1 Stock
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GarageGreen.copy(alpha = 0.15f))
                                .clickable { onQuickAddStock(item.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+1", tint = GarageGreen, modifier = Modifier.size(16.dp))
                        }

                        // Edit
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyPrimary.copy(alpha = 0.1f))
                                .clickable {
                                    editingPart = item
                                    showAddEditDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyPrimary, modifier = Modifier.size(16.dp))
                        }

                        // Delete
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GarageRed.copy(alpha = 0.1f))
                                .clickable { onDeletePart(item.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GarageRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Part Dialog
    if (showAddEditDialog) {
        var partName by remember { mutableStateOf(editingPart?.name ?: "") }
        var partBrand by remember { mutableStateOf(editingPart?.brand ?: "") }
        var partPurchase by remember { mutableStateOf(editingPart?.purchasePrice?.toInt()?.toString() ?: "") }
        var partRetail by remember { mutableStateOf(editingPart?.retailPrice?.toInt()?.toString() ?: "") }
        var partQty by remember { mutableStateOf(editingPart?.stockQty?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = {
                Text(
                    text = if (editingPart != null) "Edit Spare Part" else GarageStrings.get("addNewPart", language),
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = partName,
                        onValueChange = { partName = it },
                        label = { Text("Part Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = partBrand,
                        onValueChange = { partBrand = it },
                        label = { Text("Brand Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = partPurchase,
                            onValueChange = { partPurchase = it },
                            label = { Text("Buy (₹) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = partRetail,
                            onValueChange = { partRetail = it },
                            label = { Text("Sell (₹) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = partQty,
                        onValueChange = { partQty = it },
                        label = { Text("Stock Quantity *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pBuy = partPurchase.toDoubleOrNull() ?: 0.0
                        val pSell = partRetail.toDoubleOrNull() ?: 0.0
                        val pQ = partQty.toIntOrNull() ?: 0
                        if (partName.isNotBlank() && partBrand.isNotBlank()) {
                            onSavePart(editingPart?.id, partName.trim(), partBrand.trim(), pBuy, pSell, pQ)
                            showAddEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(GarageStrings.get("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) {
                    Text(GarageStrings.get("cancel", language))
                }
            }
        )
    }
}
