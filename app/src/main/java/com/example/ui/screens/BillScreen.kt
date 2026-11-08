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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.BilledPart
import com.example.model.InventoryItem
import com.example.model.Invoice
import com.example.model.Language
import com.example.ui.GarageStrings
import com.example.ui.theme.GarageGreen
import com.example.ui.theme.GarageOrange
import com.example.ui.theme.GarageRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillScreen(
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    customerPhone: String,
    onCustomerPhoneChange: (String) -> Unit,
    bikeModel: String,
    onBikeModelChange: (String) -> Unit,
    selectedServiceIds: Set<String>,
    onToggleService: (String) -> Unit,
    manualLabour: Double,
    onManualLabourChange: (Double) -> Unit,
    billDiscount: Double,
    onBillDiscountChange: (Double) -> Unit,
    inventory: List<InventoryItem>,
    billedParts: List<BilledPart>,
    onAddPartToBill: (String, Int) -> Boolean,
    onRemoveBilledPart: (String) -> Unit,
    onSaveInvoice: () -> Unit,
    language: Language
) {
    var selectedPartId by remember { mutableStateOf("") }
    var partQtyText by remember { mutableStateOf("1") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val chosenServices = GarageStrings.DEFAULT_SERVICES.filter { selectedServiceIds.contains(it.id) }
    val servicesTotal = chosenServices.sumOf { it.price }
    val partsTotal = billedParts.sumOf { it.total }
    val grandTotal = maxOf(0.0, servicesTotal + partsTotal + manualLabour - billDiscount)

    val selectedInventoryPart = inventory.find { it.id == selectedPartId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Customer Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = GarageStrings.get("billingTitle", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = onCustomerNameChange,
                        label = { Text(GarageStrings.get("customerName", language)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { if (it.length <= 10) onCustomerPhoneChange(it) },
                            label = { Text(GarageStrings.get("phoneNumber", language)) },
                            placeholder = { Text("9876543210") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = bikeModel,
                            onValueChange = onBikeModelChange,
                            label = { Text(GarageStrings.get("bikeModel", language)) },
                            placeholder = { Text("Hero Splendor+") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Service Selection Checkboxes
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GarageStrings.get("servicesTitle", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "${selectedServiceIds.size} selected",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GarageGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GarageStrings.DEFAULT_SERVICES.forEach { srv ->
                        val isChecked = selectedServiceIds.contains(srv.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleService(srv.id) }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onToggleService(srv.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = NavyPrimary)
                                )
                                Text(
                                    text = GarageStrings.get(srv.key, language),
                                    fontSize = 13.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = TextDark
                                )
                            }
                            Text(
                                text = "₹${srv.price.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = SlateBackground)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GarageStrings.get("additionalLabour", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        OutlinedTextField(
                            value = if (manualLabour == 0.0) "" else manualLabour.toInt().toString(),
                            onValueChange = { onManualLabourChange(it.toDoubleOrNull() ?: 0.0) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.size(width = 100.dp, height = 50.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Spare Parts from Inventory Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = GarageStrings.get("partsTitle", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dropdown for Part Selection
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedInventoryPart?.let { "${it.name} (Stock: ${it.stockQty})" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text(GarageStrings.get("choosePart", language)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            inventory.forEach { part ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${part.name} (${part.brand}) - ₹${part.retailPrice.toInt()} [Stock: ${part.stockQty}]",
                                            fontSize = 12.sp
                                        )
                                    },
                                    onClick = {
                                        selectedPartId = part.id
                                        dropdownExpanded = false
                                    },
                                    enabled = part.stockQty > 0
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = partQtyText,
                            onValueChange = { partQtyText = it },
                            label = { Text(GarageStrings.get("qty", language)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                val qty = partQtyText.toIntOrNull() ?: 1
                                if (selectedPartId.isNotEmpty()) {
                                    val added = onAddPartToBill(selectedPartId, qty)
                                    if (added) {
                                        selectedPartId = ""
                                        partQtyText = "1"
                                        errorMessage = null
                                    } else {
                                        errorMessage = "Requested quantity exceeds available stock!"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(GarageStrings.get("addPartBtn", language), fontSize = 12.sp)
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = GarageRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Billed Parts Line Items
                    if (billedParts.isEmpty()) {
                        Text(
                            text = GarageStrings.get("noPartsYet", language),
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        billedParts.forEach { part ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SlateBackground, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = part.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text(text = "${part.qty} × ₹${part.unitRetail.toInt()}", fontSize = 10.sp, color = TextMuted)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "₹${part.total.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    IconButton(onClick = { onRemoveBilledPart(part.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = GarageRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // Bill Summary & SAVE & STOCK MINUS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = GarageStrings.get("billSummary", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(GarageStrings.get("servicesTotal", language), fontSize = 12.sp, color = TextMuted)
                        Text("₹${servicesTotal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(GarageStrings.get("partsTotal", language), fontSize = 12.sp, color = TextMuted)
                        Text("₹${partsTotal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(GarageStrings.get("labourCharge", language), fontSize = 12.sp, color = TextMuted)
                        Text("₹${manualLabour.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(GarageStrings.get("discount", language), fontSize = 12.sp, color = TextMuted)
                        OutlinedTextField(
                            value = if (billDiscount == 0.0) "" else billDiscount.toInt().toString(),
                            onValueChange = { onBillDiscountChange(it.toDoubleOrNull() ?: 0.0) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.size(width = 90.dp, height = 48.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SlateBackground)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = GarageStrings.get("grandTotal", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                        Text(
                            text = "₹${grandTotal.toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = GarageGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Button: SAVE & STOCK MINUS
                    Button(
                        onClick = onSaveInvoice,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GarageGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = GarageStrings.get("btnSaveAndMinus", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
