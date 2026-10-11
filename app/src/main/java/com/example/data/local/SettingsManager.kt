package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BudgetSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("paisawise_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<BudgetSettings> = _settings.asStateFlow()

    private fun loadSettings(): BudgetSettings {
        return BudgetSettings(
            monthlyStipend = prefs.getFloat("monthly_stipend", 15000f).toDouble(),
            roomRentTotal = prefs.getFloat("room_rent_total", 4000f).toDouble(),
            roommateCount = prefs.getInt("roommate_count", 2),
            morningFoodBudget = prefs.getFloat("morning_food", 70f).toDouble(),
            nightFoodBudget = prefs.getFloat("night_food", 35f).toDouble(),
            mobileRechargeBudget = prefs.getFloat("mobile_recharge", 400f).toDouble(),
            lightBillTotal = prefs.getFloat("light_bill_total", 1500f).toDouble(),
            lightBillSplit = prefs.getBoolean("light_bill_split", true),
            transportBudget = prefs.getFloat("transport_budget", 1000f).toDouble(),
            healthBudget = prefs.getFloat("health_budget", 500f).toDouble(),
            dailySpendLimit = prefs.getFloat("daily_spend_limit", 250f).toDouble(),
            reminderEnabled = prefs.getBoolean("reminder_enabled", true),
            reminderHour = prefs.getInt("reminder_hour", 21),
            reminderMinute = prefs.getInt("reminder_minute", 0)
        )
    }

    fun updateSettings(newSettings: BudgetSettings) {
        prefs.edit().apply {
            putFloat("monthly_stipend", newSettings.monthlyStipend.toFloat())
            putFloat("room_rent_total", newSettings.roomRentTotal.toFloat())
            putInt("roommate_count", newSettings.roommateCount)
            putFloat("morning_food", newSettings.morningFoodBudget.toFloat())
            putFloat("night_food", newSettings.nightFoodBudget.toFloat())
            putFloat("mobile_recharge", newSettings.mobileRechargeBudget.toFloat())
            putFloat("light_bill_total", newSettings.lightBillTotal.toFloat())
            putBoolean("light_bill_split", newSettings.lightBillSplit)
            putFloat("transport_budget", newSettings.transportBudget.toFloat())
            putFloat("health_budget", newSettings.healthBudget.toFloat())
            putFloat("daily_spend_limit", newSettings.dailySpendLimit.toFloat())
            putBoolean("reminder_enabled", newSettings.reminderEnabled)
            putInt("reminder_hour", newSettings.reminderHour)
            putInt("reminder_minute", newSettings.reminderMinute)
            apply()
        }
        _settings.value = newSettings
    }
}
