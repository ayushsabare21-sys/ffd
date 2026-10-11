package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddRentDialog(
    defaultTotalRent: Double = 4000.0,
    defaultMyShare: Double = 2000.0,
    defaultRoommateShare: Double = 2000.0,
    onDismiss: () -> Unit,
    onConfirm: (total: Double, myShare: Double, roommateShare: Double, ownerName: String, mode: String, note: String) -> Unit
) {
    var totalRentText by remember { mutableStateOf(defaultTotalRent.toInt().toString()) }
    var myShareText by remember { mutableStateOf(defaultMyShare.toInt().toString()) }
    var ownerName by remember { mutableStateOf("Room Owner") }
    var paymentMode by remember { mutableStateOf("UPI (GPay/PhonePe)") }
    var receiptNote by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Pay Room Rent to Owner", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = totalRentText,
                    onValueChange = {
                        totalRentText = it
                        val total = it.toDoubleOrNull() ?: 0.0
                        myShareText = (total / 2.0).toInt().toString()
                        isError = false
                    },
                    label = { Text("Total Room Rent (₹)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("total_rent_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = myShareText,
                    onValueChange = {
                        myShareText = it
                        isError = false
                    },
                    label = { Text("My Share Paid (₹)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("my_rent_share_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    val tot = totalRentText.toDoubleOrNull() ?: 0.0
                    val my = myShareText.toDoubleOrNull() ?: 0.0
                    val rm = (tot - my).coerceAtLeast(0.0)
                    Text(
                        text = "Split summary: ₹${tot.toInt()} total = ₹${my.toInt()} (You) + ₹${rm.toInt()} (Roommate)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner / Landlord Name") },
                    placeholder = { Text("e.g. Sharma Ji, Building Owner") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = paymentMode,
                    onValueChange = { paymentMode = it },
                    label = { Text("Payment Mode") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = receiptNote,
                    onValueChange = { receiptNote = it },
                    label = { Text("Receipt Note / UPI Ref ID") },
                    placeholder = { Text("e.g. UPI Ref #987654321") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalRentText.toDoubleOrNull()
                    val my = myShareText.toDoubleOrNull()
                    if (total == null || my == null || total <= 0 || my <= 0) {
                        isError = true
                        return@Button
                    }
                    val roommateShare = (total - my).coerceAtLeast(0.0)
                    onConfirm(total, my, roommateShare, ownerName, paymentMode, receiptNote)
                },
                modifier = Modifier.testTag("confirm_rent_payment_button")
            ) {
                Text("Log Rent Payment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
