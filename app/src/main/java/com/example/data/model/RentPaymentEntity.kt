package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rent_payments")
data class RentPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val totalRent: Double = 4000.0,
    val myShare: Double = 2000.0,
    val roommateShare: Double = 2000.0,
    val dateMillis: Long,
    val monthLabel: String = "",
    val ownerName: String = "Room Owner / Landlord",
    val paymentMode: String = "UPI (GPay / PhonePe)",
    val isConfirmedPaid: Boolean = true,
    val receiptNote: String = ""
)
