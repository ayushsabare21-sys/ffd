package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String, // from ExpenseCategory.name
    val dateMillis: Long,
    val isReimbursable: Boolean = false, // Porter, work errand, courier
    val isReimbursed: Boolean = false,
    val isPresetFood: Boolean = false, // ₹70 or ₹35 quick log
    val note: String = ""
)
