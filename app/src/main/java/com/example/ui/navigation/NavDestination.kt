package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    DAILY_SPEND("Daily Spend", Icons.Default.ReceiptLong, "nav_daily_spend"),
    RENT_MILESTONES("Rent & Salary", Icons.Default.AccountBalance, "nav_rent_milestones"),
    BUDGET_ALERTS("Budget & Alerts", Icons.Default.Tune, "nav_budget_alerts"),
    AI_ADVISOR("AI Advisor", Icons.Default.AutoAwesome, "nav_ai_advisor")
}
