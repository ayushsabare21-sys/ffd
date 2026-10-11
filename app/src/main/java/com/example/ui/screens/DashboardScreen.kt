package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseCategory
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AddRentDialog
import com.example.ui.components.AddSalaryDialog
import com.example.ui.components.DailySpendBarChart
import com.example.ui.components.ExpenseCard
import com.example.ui.components.QuickAddBar
import com.example.ui.components.StatCard
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.BlueCompany
import com.example.ui.theme.EmeraldContainerDark
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GoldSecondaryLight
import com.example.ui.theme.GreenGain
import com.example.ui.theme.PurpleRent
import com.example.ui.theme.RedExpense
import com.example.ui.viewmodel.PaisaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: PaisaViewModel,
    onNavigateToDaily: (Long) -> Unit,
    onNavigateToMilestones: () -> Unit,
    onNavigateToBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val todayExpenses by viewModel.todayExpenses.collectAsStateWithLifecycle()
    val todayTotalSpend by viewModel.todayTotalSpend.collectAsStateWithLifecycle()
    val monthlyTotalSpend by viewModel.monthlyTotalSpend.collectAsStateWithLifecycle()
    val salaryCount by viewModel.salaryCount.collectAsStateWithLifecycle()
    val totalSalary by viewModel.totalSalary.collectAsStateWithLifecycle()
    val rentPaidCount by viewModel.rentPaidCount.collectAsStateWithLifecycle()
    val totalRentPaid by viewModel.totalRentPaid.collectAsStateWithLifecycle()
    val pendingClaims by viewModel.pendingReimbursementAmount.collectAsStateWithLifecycle()

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddSalaryDialog by remember { mutableStateOf(false) }
    var showAddRentDialog by remember { mutableStateOf(false) }

    val dailyProgress = (todayTotalSpend / settings.dailySpendLimit).toFloat().coerceIn(0f, 1f)
    val dailyStatusColor = when {
        todayTotalSpend <= settings.dailySpendLimit * 0.8 -> GreenGain
        todayTotalSpend <= settings.dailySpendLimit -> AmberAlert
        else -> RedExpense
    }

    val currentMonthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddExpenseDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Spend") },
                text = { Text("Add Spend", fontWeight = FontWeight.Bold) },
                containerColor = EmeraldPrimaryLight,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_spend")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EmeraldPrimaryLight, Color(0xFF043828))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PaisaWise",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Internship Stipend • $currentMonthLabel",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "₹${settings.monthlyStipend.toInt()} /mo",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldSecondaryLight
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Financial Health Snapshot
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SnapshotChip(
                                title = "Spent This Month",
                                amount = "₹${monthlyTotalSpend.toInt()}",
                                subtitle = "of ₹${settings.monthlyStipend.toInt()}",
                                modifier = Modifier.weight(1f)
                            )
                            SnapshotChip(
                                title = "Projected Savings",
                                amount = "₹${settings.projectedMonthlySavings.toInt()}",
                                subtitle = "Target ~₹5,000+",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Quick Add Presets Bar
            item {
                Column {
                    Text(
                        text = "⚡ Quick Log Daily Spends",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    QuickAddBar(
                        morningFoodAmount = settings.morningFoodBudget.toInt(),
                        nightFoodAmount = settings.nightFoodBudget.toInt(),
                        onAddMorning = { viewModel.quickAddMorningFood() },
                        onAddNight = { viewModel.quickAddNightFood() },
                        onAddChai = { viewModel.quickAddChai() },
                        onAddPorter = {
                            viewModel.quickAddPorterCompanySpend(
                                amount = 150.0,
                                description = "Office Porter delivery"
                            )
                        },
                        onAddCustom = { showAddExpenseDialog = true }
                    )
                }
            }

            // Milestone Counters: Salary & Rent
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "🏆 Key Internship Milestones",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Salary Received",
                            value = "$salaryCount Times",
                            subtitle = "Total: ₹${totalSalary.toInt()}",
                            icon = Icons.Default.AccountBalance,
                            iconTint = EmeraldPrimaryLight,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_salary_milestone")
                        )
                        StatCard(
                            title = "Rent Paid to Owner",
                            value = "$rentPaidCount Times",
                            subtitle = "Share: ₹${totalRentPaid.toInt()}",
                            icon = Icons.Default.Home,
                            iconTint = PurpleRent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_rent_milestone")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { showAddSalaryDialog = true },
                            modifier = Modifier.weight(1f).testTag("btn_log_salary")
                        ) {
                            Text("+ Log Salary", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { showAddRentDialog = true },
                            modifier = Modifier.weight(1f).testTag("btn_log_rent")
                        ) {
                            Text("+ Pay Rent", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Today's Spending Alert & Budget Limit Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("today_spend_alert_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(dailyStatusColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (todayTotalSpend > settings.dailySpendLimit) Icons.Default.Warning else Icons.Default.CheckCircle,
                                        contentDescription = "Budget status",
                                        tint = dailyStatusColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Today's Daily Spending",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (todayTotalSpend <= settings.dailySpendLimit)
                                            "On track! Remaining: ₹${(settings.dailySpendLimit - todayTotalSpend).coerceAtLeast(0.0).toInt()}"
                                        else "⚠️ Over daily target by ₹${(todayTotalSpend - settings.dailySpendLimit).toInt()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = dailyStatusColor
                                    )
                                }
                            }
                            Text(
                                text = "₹${todayTotalSpend.toInt()} / ₹${settings.dailySpendLimit.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = dailyStatusColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { dailyProgress },
                            color = dailyStatusColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Daily Food Plan: ₹${settings.morningFoodBudget.toInt()} AM + ₹${settings.nightFoodBudget.toInt()} PM = ₹${settings.dailyFoodEstimate.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (pendingClaims > 0) {
                                Text(
                                    text = "💼 ₹${pendingClaims.toInt()} Porter Pending",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BlueCompany
                                )
                            }
                        }
                    }
                }
            }

            // 7-Day Spend Trend Bar Chart
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DailySpendBarChart(
                        expenses = allExpenses,
                        dailyLimit = settings.dailySpendLimit,
                        onDaySelected = { dateMillis ->
                            viewModel.setSelectedDate(dateMillis)
                            onNavigateToDaily(dateMillis)
                        }
                    )
                }
            }

            // Recent Expenses Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Spends",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedButton(
                        onClick = { onNavigateToDaily(System.currentTimeMillis()) }
                    ) {
                        Text("View Daily Log", fontSize = 12.sp)
                    }
                }
            }

            if (allExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No spends logged yet. Use the quick buttons above to track morning food!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(allExpenses.take(5)) { expense ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ExpenseCard(
                            expense = expense,
                            onDelete = { viewModel.deleteExpense(expense.id) },
                            onToggleReimbursed = { viewModel.toggleReimbursementStatus(expense) }
                        )
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { title, amount, category, isReimbursable, note ->
                viewModel.addExpense(
                    title = title,
                    amount = amount,
                    category = category,
                    isReimbursable = isReimbursable,
                    note = note
                )
                showAddExpenseDialog = false
            }
        )
    }

    if (showAddSalaryDialog) {
        AddSalaryDialog(
            defaultStipend = settings.monthlyStipend,
            onDismiss = { showAddSalaryDialog = false },
            onConfirm = { amount, source, monthLabel, note ->
                viewModel.addSalaryCredit(
                    amount = amount,
                    source = source,
                    monthLabel = monthLabel,
                    note = note
                )
                showAddSalaryDialog = false
            }
        )
    }

    if (showAddRentDialog) {
        AddRentDialog(
            defaultTotalRent = settings.roomRentTotal,
            defaultMyShare = settings.myRentShare,
            defaultRoommateShare = settings.roomRentTotal - settings.myRentShare,
            onDismiss = { showAddRentDialog = false },
            onConfirm = { total, myShare, rmShare, ownerName, mode, note ->
                viewModel.addRentPayment(
                    totalRent = total,
                    myShare = myShare,
                    roommateShare = rmShare,
                    ownerName = ownerName,
                    paymentMode = mode,
                    receiptNote = note
                )
                showAddRentDialog = false
            }
        )
    }
}

@Composable
private fun SnapshotChip(
    title: String,
    amount: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
