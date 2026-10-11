package com.example

import com.example.data.model.BudgetSettings
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun budgetCalculations_internshipSetup_areAccurate() {
    val settings = BudgetSettings(
      monthlyStipend = 15000.0,
      roomRentTotal = 4000.0,
      roommateCount = 2,
      morningFoodBudget = 70.0,
      nightFoodBudget = 35.0,
      mobileRechargeBudget = 400.0,
      lightBillTotal = 1500.0,
      lightBillSplit = true,
      transportBudget = 1000.0,
      healthBudget = 500.0,
      dailySpendLimit = 250.0
    )

    // Rent split in 2 people = 2000 INR
    assertEquals(2000.0, settings.myRentShare, 0.01)

    // Daily food = 70 + 35 = 105 INR
    assertEquals(105.0, settings.dailyFoodEstimate, 0.01)

    // Monthly food = 105 * 30 = 3150 INR
    assertEquals(3150.0, settings.monthlyFoodEstimate, 0.01)

    // Light bill split in 2 = 750 INR
    assertEquals(750.0, settings.myLightBillShare, 0.01)

    // Fixed & routine needs: 2000 + 750 + 400 + 3150 + 1000 + 500 = 7800 INR
    assertEquals(7800.0, settings.estimatedTotalFixedExpenses, 0.01)

    // Projected savings from 15,000 stipend: 15000 - 7800 = 7200 INR
    assertEquals(7200.0, settings.projectedMonthlySavings, 0.01)
    assertTrue("Should save at least 5000 INR", settings.projectedMonthlySavings >= 5000.0)
  }
}

