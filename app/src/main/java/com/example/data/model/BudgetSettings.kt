package com.example.data.model

data class BudgetSettings(
    val monthlyStipend: Double = 15000.0,
    val roomRentTotal: Double = 4000.0,
    val roommateCount: Int = 2,
    val morningFoodBudget: Double = 70.0,
    val nightFoodBudget: Double = 35.0,
    val mobileRechargeBudget: Double = 400.0,
    val lightBillTotal: Double = 1500.0,
    val lightBillSplit: Boolean = true,
    val transportBudget: Double = 1000.0,
    val healthBudget: Double = 500.0,
    val dailySpendLimit: Double = 250.0,
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0
) {
    val myRentShare: Double
        get() = if (roommateCount > 0) roomRentTotal / roommateCount else roomRentTotal

    val myLightBillShare: Double
        get() = if (lightBillSplit && roommateCount > 0) lightBillTotal / roommateCount else lightBillTotal

    val dailyFoodEstimate: Double
        get() = morningFoodBudget + nightFoodBudget

    val monthlyFoodEstimate: Double
        get() = dailyFoodEstimate * 30.0

    val estimatedTotalFixedExpenses: Double
        get() = myRentShare + myLightBillShare + mobileRechargeBudget + monthlyFoodEstimate + transportBudget + healthBudget

    val projectedMonthlySavings: Double
        get() = (monthlyStipend - estimatedTotalFixedExpenses).coerceAtLeast(0.0)
}
