package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseCategory
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.ExpenseCard
import com.example.ui.components.QuickAddBar
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GreenGain
import com.example.ui.theme.RedExpense
import com.example.ui.viewmodel.PaisaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyExpenseScreen(
    viewModel: PaisaViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val selectedDateMillis by viewModel.selectedDateMillis.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<ExpenseCategory?>(null) }

    // Day calculation
    val (startOfDay, endOfDay, dayTitle, isToday) = remember(selectedDateMillis) {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        val todayCal = Calendar.getInstance()

        val isCurrentDay = cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

        val start = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val end = start + 24 * 60 * 60 * 1000L

        val title = if (isCurrentDay) "Today, " + SimpleDateFormat("dd MMMM", Locale.getDefault()).format(Date(start))
        else SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault()).format(Date(start))

        Quadruple(start, end, title, isCurrentDay)
    }

    val dayExpenses = remember(allExpenses, startOfDay, endOfDay) {
        allExpenses.filter { it.dateMillis in startOfDay until endOfDay }
    }

    val filteredExpenses = remember(dayExpenses, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) dayExpenses
        else dayExpenses.filter { it.category.equals(selectedCategoryFilter?.name, ignoreCase = true) }
    }

    val dayTotal = remember(dayExpenses) {
        dayExpenses.filter { !it.isReimbursable || it.isReimbursed }.sumOf { it.amount }
    }

    val foodTotal = remember(dayExpenses) {
        dayExpenses.filter { it.category == ExpenseCategory.FOOD.name }.sumOf { it.amount }
    }

    val porterTotal = remember(dayExpenses) {
        dayExpenses.filter { it.category == ExpenseCategory.WORK_PORTER.name }.sumOf { it.amount }
    }

    val isOverBudget = dayTotal > settings.dailySpendLimit

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Daily Spending Log", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    if (!isToday) {
                        IconButton(
                            onClick = { viewModel.setSelectedDate(System.currentTimeMillis()) },
                            modifier = Modifier.testTag("btn_jump_today")
                        ) {
                            Icon(Icons.Default.Today, contentDescription = "Go to Today")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimaryLight,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_daily_spend")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Spend")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Switcher Header
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.setSelectedDate(selectedDateMillis - 24 * 60 * 60 * 1000L)
                            },
                            modifier = Modifier.testTag("btn_prev_day")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isToday) "Current Active Day" else "Historical Record",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.setSelectedDate(selectedDateMillis + 24 * 60 * 60 * 1000L)
                            },
                            modifier = Modifier.testTag("btn_next_day")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
                        }
                    }
                }
            }

            // Day Summary Box
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOverBudget) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Spent on this Day",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${dayTotal.toInt()}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isOverBudget) RedExpense else EmeraldPrimaryLight
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Daily Limit: ₹${settings.dailySpendLimit.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val statusText = if (isOverBudget) "Over by ₹${(dayTotal - settings.dailySpendLimit).toInt()}"
                                else "Under by ₹${(settings.dailySpendLimit - dayTotal).toInt()}"
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isOverBudget) RedExpense else GreenGain
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🍛 Food: ₹${foodTotal.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (porterTotal > 0) {
                                Text(
                                    text = "🚚 Porter/Work: ₹${porterTotal.toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Text(
                                text = "📝 ${dayExpenses.size} items",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Quick Add bar if today
            if (isToday) {
                item {
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
                        onAddCustom = { showAddDialog = true }
                    )
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All (${dayExpenses.size})") }
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == ExpenseCategory.FOOD,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == ExpenseCategory.FOOD) null else ExpenseCategory.FOOD
                        },
                        label = { Text("Food") }
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == ExpenseCategory.WORK_PORTER,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == ExpenseCategory.WORK_PORTER) null else ExpenseCategory.WORK_PORTER
                        },
                        label = { Text("Porter / Work") }
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == ExpenseCategory.TRANSPORT,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == ExpenseCategory.TRANSPORT) null else ExpenseCategory.TRANSPORT
                        },
                        label = { Text("Transport") }
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == ExpenseCategory.LIGHT_BILL || selectedCategoryFilter == ExpenseCategory.ROOM_RENT,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == ExpenseCategory.LIGHT_BILL) null else ExpenseCategory.LIGHT_BILL
                        },
                        label = { Text("Bills & Rent") }
                    )
                }
            }

            // Expense Items
            if (filteredExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No spends logged for this day.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddDialog = true }
                            ) {
                                Text("+ Add Expense for this Day")
                            }
                        }
                    }
                }
            } else {
                items(filteredExpenses, key = { it.id }) { expense ->
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

    if (showAddDialog) {
        AddExpenseDialog(
            initialCategory = selectedCategoryFilter ?: ExpenseCategory.FOOD,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, category, isReimbursable, note ->
                viewModel.addExpense(
                    title = title,
                    amount = amount,
                    category = category,
                    dateMillis = selectedDateMillis,
                    isReimbursable = isReimbursable,
                    note = note
                )
                showAddDialog = false
            }
        )
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
