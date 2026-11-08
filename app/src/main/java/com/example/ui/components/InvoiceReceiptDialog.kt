package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Invoice
import com.example.model.Language
import com.example.ui.GarageStrings
import com.example.ui.theme.GarageGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.net.URLEncoder

@Composable
fun InvoiceReceiptDialog(
    invoice: Invoice,
    language: Language,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GarageGreen, modifier = Modifier.size(24.dp))
                Text("Invoice Saved!", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateBackground, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = invoice.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                    Text(text = "${invoice.date} ${invoice.time}", fontSize = 11.sp, color = TextMuted)
                }

                Text(text = "Customer: ${invoice.customer} (${invoice.phone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text(text = "Bike: ${invoice.bike}", fontSize = 12.sp, color = TextMuted)

                HorizontalDivider(color = Color.LightGray)

                invoice.servicesSelected.forEach { srv ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "• ${srv.key}", fontSize = 11.sp, color = TextDark)
                        Text(text = "₹${srv.price.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                invoice.partsUsed.forEach { p ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "• ${p.name} (${p.qty}x)", fontSize = 11.sp, color = TextDark)
                        Text(text = "₹${p.total.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (invoice.discount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Discount", fontSize = 11.sp, color = TextMuted)
                        Text(text = "-₹${invoice.discount.toInt()}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color.LightGray)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "TOTAL AMOUNT:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "₹${invoice.totalAmount.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GarageGreen)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    shareInvoiceWhatsApp(context, invoice)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text(GarageStrings.get("sendWhatsApp", language), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}

fun shareInvoiceWhatsApp(context: Context, invoice: Invoice) {
    val builder = StringBuilder()
    builder.append("*GJ13 AUTO GARAGE* 🔧🏍️\n")
    builder.append("📍 Main Road, Surendranagar\n")
    builder.append("--------------------------------\n")
    builder.append("*Invoice:* ${invoice.id}\n")
    builder.append("*Date:* ${invoice.date} ${invoice.time}\n")
    builder.append("*Customer:* ${invoice.customer}\n")
    builder.append("*Bike Model:* ${invoice.bike}\n\n")

    if (invoice.servicesSelected.isNotEmpty()) {
        builder.append("*Services:*\n")
        invoice.servicesSelected.forEach { s ->
            builder.append("• ${s.key}: ₹${s.price.toInt()}\n")
        }
        builder.append("\n")
    }

    if (invoice.partsUsed.isNotEmpty()) {
        builder.append("*Parts Used:*\n")
        invoice.partsUsed.forEach { p ->
            builder.append("• ${p.name} (${p.qty}x): ₹${p.total.toInt()}\n")
        }
        builder.append("\n")
    }

    if (invoice.discount > 0) {
        builder.append("*Discount:* ₹${invoice.discount.toInt()}\n")
    }

    builder.append("*FINAL BILL AMOUNT:* ₹${invoice.totalAmount.toInt()}\n")
    builder.append("--------------------------------\n")
    builder.append("Thank you for choosing *GJ13 AUTO GARAGE*! 🛵\n")
    builder.append("Call: +91 98765 43210. Drive Safely!")

    var phone = invoice.phone.filter { it.isDigit() }
    if (phone.length == 10) phone = "91$phone"

    val encoded = URLEncoder.encode(builder.toString(), "UTF-8")
    val url = "https://wa.me/$phone?text=$encoded"

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (_: Exception) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, builder.toString())
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Invoice"))
    }
}
