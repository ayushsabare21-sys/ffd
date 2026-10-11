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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.AddRentDialog
import com.example.ui.components.AddSalaryDialog
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GoldSecondaryLight
import com.example.ui.theme.GreenGain
import com.example.ui.theme.PurpleRent
import com.example.ui.viewmodel.PaisaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentAndMilestonesScreen(
    viewModel: PaisaViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val salaryCredits by viewModel.salaryCredits.collectAsStateWithLifecycle()
    val salaryCount by viewModel.salaryCount.collectAsStateWithLifecycle()
    val totalSalary by viewModel.totalSalary.collectAsStateWithLifecycle()
    val rentPayments by viewModel.rentPayments.collectAsStateWithLifecycle()
    val rentPaidCount by viewModel.rentPaidCount.collectAsStateWithLifecycle()
    val totalRentPaid by viewModel.totalRentPaid.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddSalaryDialog by remember { mutableStateOf(false) }
    var showAddRentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Salary & Rent Milestones", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Salary Credits ($salaryCount)", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Room Rent ($rentPaidCount)", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Fixed Bills", fontWeight = FontWeight.SemiBold) }
                )
            }

            when (selectedTabIndex) {
                0 -> SalaryMilestonesTab(
                    salaryCount = salaryCount,
                    totalSalary = totalSalary,
                    salaryCredits = salaryCredits,
                    defaultStipend = settings.monthlyStipend,
                    onOpenAddSalary = { showAddSalaryDialog = true },
                    onDeleteSalary = { id -> viewModel.deleteSalaryCredit(id) }
                )
                1 -> RentMilestonesTab(
                    rentPaidCount = rentPaidCount,
                    totalRentPaid = totalRentPaid,
                    rentPayments = rentPayments,
                    settings = settings,
                    onOpenAddRent = { showAddRentDialog = true },
                    onDeleteRent = { id -> viewModel.deleteRentPayment(id) }
                )
                2 -> FixedBillsTab(
                    settings = settings,
                    onQuickLogBill = { title, amount, category ->
                        viewModel.addExpense(
                            title = title,
                            amount = amount,
                            category = category,
                            note = "Monthly routine bill payment"
                        )
                    }
                )
            }
        }
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
private fun SalaryMilestonesTab(
    salaryCount: Int,
    totalSalary: Double,
    salaryCredits: List<com.example.data.model.SalaryCreditEntity>,
    defaultStipend: Double,
    onOpenAddSalary: () -> Unit,
    onDeleteSalary: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("salary_milestone_hero")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "How Many Times Got Salary?",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$salaryCount Times",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimaryLight
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimaryLight.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Salary Icon",
                                tint = EmeraldPrimaryLight,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Total Internship Stipend Credited: ₹${totalSalary.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Standard monthly stipend: ₹${defaultStipend.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onOpenAddSalary,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryLight),
                        modifier = Modifier.fillMaxWidth().testTag("btn_log_new_salary")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log New Salary Credit (+₹${defaultStipend.toInt()})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Salary Credits History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (salaryCredits.isEmpty()) {
            item {
                Text(
                    text = "No salary credits logged yet. Click the button above to log your first salary!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(salaryCredits, key = { it.id }) { item ->
                val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(item.dateMillis))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GreenGain.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenGain)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.monthLabel.ifBlank { "Salary Credit" },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${item.companyOrSource} • $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (item.note.isNotBlank()) {
                                Text(
                                    text = item.note,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+₹${item.amount.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GreenGain
                                )
                            )
                            IconButton(
                                onClick = { onDeleteSalary(item.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RentMilestonesTab(
    rentPaidCount: Int,
    totalRentPaid: Double,
    rentPayments: List<com.example.data.model.RentPaymentEntity>,
    settings: com.example.data.model.BudgetSettings,
    onOpenAddRent: () -> Unit,
    onDeleteRent: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PurpleRent.copy(alpha = 0.12f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("rent_milestone_hero")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Times Room Rent Given to Owner",
                                style = MaterialTheme.typography.labelLarge,
                                color = PurpleRent
                            )
                            Text(
                                text = "$rentPaidCount Times",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PurpleRent
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PurpleRent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Rent Icon",
                                tint = PurpleRent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Split Room Calculation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "🏠 Room Rent Split Agreement:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Owner Rent: ₹${settings.roomRentTotal.toInt()}")
                                Text("Split by: ${settings.roommateCount} people")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Your Share: ₹${settings.myRentShare.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleRent
                                )
                                Text(
                                    "Roommate Share: ₹${(settings.roomRentTotal - settings.myRentShare).toInt()}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Total Your Share Paid: ₹${totalRentPaid.toInt()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenAddRent,
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleRent),
                        modifier = Modifier.fillMaxWidth().testTag("btn_log_new_rent")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Rent Payment to Owner (₹${settings.myRentShare.toInt()})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Rent Payments Given to Owner",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (rentPayments.isEmpty()) {
            item {
                Text(
                    text = "No rent payments recorded yet. Click above to log your first payment to the owner!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(rentPayments, key = { it.id }) { item ->
                val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(item.dateMillis))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PurpleRent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = PurpleRent)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Paid to: ${item.ownerName}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${item.paymentMode} • $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Total ₹${item.totalRent.toInt()} (My share: ₹${item.myShare.toInt()}, Roommate: ₹${item.roommateShare.toInt()})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            if (item.receiptNote.isNotBlank()) {
                                Text(
                                    text = "Ref: ${item.receiptNote}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleRent
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${item.myShare.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleRent
                                )
                            )
                            IconButton(
                                onClick = { onDeleteRent(item.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FixedBillsTab(
    settings: com.example.data.model.BudgetSettings,
    onQuickLogBill: (title: String, amount: Double, category: ExpenseCategory) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "⚡ Monthly Recurring Bills & Budgets",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Track your fixed internship living expenses in Mumbai/city",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Electricity / Light bill card
        item {
            BillItemCard(
                title = "Electricity / Light Bill",
                description = "Total ~₹${settings.lightBillTotal.toInt()} (Your split share: ₹${settings.myLightBillShare.toInt()})",
                amount = "₹${settings.myLightBillShare.toInt()}",
                icon = Icons.Default.Bolt,
                iconTint = Color(0xFFF59E0B),
                onLogNow = {
                    onQuickLogBill("Electricity / Light Bill (My Share)", settings.myLightBillShare, ExpenseCategory.LIGHT_BILL)
                }
            )
        }

        // Mobile Recharge
        item {
            BillItemCard(
                title = "Mobile Recharge",
                description = "28-day 1.5GB/day data pack",
                amount = "₹${settings.mobileRechargeBudget.toInt()}",
                icon = Icons.Default.PhoneAndroid,
                iconTint = Color(0xFF0284C7),
                onLogNow = {
                    onQuickLogBill("Monthly Mobile Recharge", settings.mobileRechargeBudget, ExpenseCategory.MOBILE_RECHARGE)
                }
            )
        }

        // Transport
        item {
            BillItemCard(
                title = "Transport & Commute",
                description = "Metro / Bus pass / Train pass to office",
                amount = "₹${settings.transportBudget.toInt()}",
                icon = Icons.Default.DirectionsBus,
                iconTint = Color(0xFF0D9488),
                onLogNow = {
                    onQuickLogBill("Office Commute / Bus Pass", settings.transportBudget, ExpenseCategory.TRANSPORT)
                }
            )
        }

        // Health
        item {
            BillItemCard(
                title = "Health & Medical Buffer",
                description = "Emergency medicines, vitamins, doctor",
                amount = "₹${settings.healthBudget.toInt()}",
                icon = Icons.Default.LocalHospital,
                iconTint = Color(0xFFE11D48),
                onLogNow = {
                    onQuickLogBill("Health & Medical Care", settings.healthBudget, ExpenseCategory.HEALTH)
                }
            )
        }

        // Food Overview
        item {
            BillItemCard(
                title = "Daily Food Budget (Morning + Night)",
                description = "Morning ₹${settings.morningFoodBudget.toInt()} + Night ₹${settings.nightFoodBudget.toInt()} x 30 days = ₹${settings.monthlyFoodEstimate.toInt()}/mo",
                amount = "₹${settings.dailyFoodEstimate.toInt()} /day",
                icon = Icons.Default.Receipt,
                iconTint = GoldSecondaryLight,
                onLogNow = {
                    onQuickLogBill("Full Day Food (Morning + Night)", settings.dailyFoodEstimate, ExpenseCategory.FOOD)
                }
            )
        }
    }
}

@Composable
private fun BillItemCard(
    title: String,
    description: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onLogNow: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = amount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = iconTint)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onLogNow,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Log", fontSize = 12.sp)
            }
        }
    }
}
