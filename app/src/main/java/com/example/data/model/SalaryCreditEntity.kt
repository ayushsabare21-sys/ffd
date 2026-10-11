package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salary_credits")
data class SalaryCreditEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double = 15000.0,
    val dateMillis: Long,
    val companyOrSource: String = "Internship Stipend",
    val monthLabel: String = "",
    val note: String = ""
)
