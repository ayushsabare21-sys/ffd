package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ExpenseCard
import com.example.ui.theme.BlueCompany
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GoldSecondaryLight
import com.example.ui.theme.GreenGain
import com.example.ui.theme.RedExpense
import com.example.ui.viewmodel.PaisaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetAndAlertsScreen(
    viewModel: PaisaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val reimbursableExpenses by viewModel.reimbursableExpenses.collectAsStateWithLifecycle()
    val pendingClaims by viewModel.pendingReimbursementAmount.collectAsStateWithLifecycle()

    var dailyLimitSlider by remember(settings.dailySpendLimit) {
        mutableFloatStateOf(settings.dailySpendLimit.toFloat())
    }
    var reminderEnabled by remember(settings.reminderEnabled) {
        mutableStateOf(settings.reminderEnabled)
    }
    var reminderHourText by remember(settings.reminderHour) {
        mutableStateOf(settings.reminderHour.toString())
    }
    var reminderMinuteText by remember(settings.reminderMinute) {
        mutableStateOf(settings.reminderMinute.toString())
    }

    var isEditingBudget by remember { mutableStateOf(false) }
    var stipendInput by remember(settings.monthlyStipend) {
        mutableStateOf(settings.monthlyStipend.toInt().toString())
    }
    var roomRentInput by remember(settings.roomRentTotal) {
        mutableStateOf(settings.roomRentTotal.toInt().toString())
    }
    var morningFoodInput by remember(settings.morningFoodBudget) {
        mutableStateOf(settings.morningFoodBudget.toInt().toString())
    }
    var nightFoodInput by remember(settings.nightFoodBudget) {
        mutableStateOf(settings.nightFoodBudget.toInt().toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Budget & Alerts Setup", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Daily Spend Limit Customizer
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("card_daily_limit_customizer")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimaryLight.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = EmeraldPrimaryLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Daily Spending Limit",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Alerts trigger if today's spend exceeds this",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "₹${dailyLimitSlider.toInt()}/day",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimaryLight
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Slider(
                            value = dailyLimitSlider,
                            onValueChange = { dailyLimitSlider = it },
                            onValueChangeFinished = {
                                viewModel.updateSettings(
                                    settings.copy(dailySpendLimit = dailyLimitSlider.toDouble())
                                )
                                Toast.makeText(context, "Daily target updated to ₹${dailyLimitSlider.toInt()}", Toast.LENGTH_SHORT).show()
                            },
                            valueRange = 100f..700f,
                            steps = 11,
                            modifier = Modifier.fillMaxWidth().testTag("daily_limit_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("₹100 (Ultra Saver)", style = MaterialTheme.typography.labelSmall)
                            Text("₹250 (Recommended)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("₹700 (High)", style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val monthlyDiscretionary = dailyLimitSlider * 30.0
                        val estimatedSavings = (settings.monthlyStipend - settings.myRentShare - settings.myLightBillShare - settings.mobileRechargeBudget - settings.transportBudget - settings.healthBudget - monthlyDiscretionary).coerceAtLeast(0.0)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 At ₹${dailyLimitSlider.toInt()}/day, you spend ~₹${monthlyDiscretionary.toInt()}/mo on daily needs. Projected end-of-month savings: ₹${estimatedSavings.toInt()}!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Section 2: Customize Daily Reminder
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("card_reminder_customizer")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GoldSecondaryLight.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = GoldSecondaryLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Daily Expense Reminder",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Evening nudge to log all day spends",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = {
                                    reminderEnabled = it
                                    viewModel.updateSettings(settings.copy(reminderEnabled = it))
                                },
                                modifier = Modifier.testTag("switch_reminder_enabled")
                            )
                        }

                        if (reminderEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Reminder Time: ${settings.reminderHour}:${if (settings.reminderMinute < 10) "0" else ""}${settings.reminderMinute} (Evening)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateSettings(settings.copy(reminderHour = 20, reminderMinute = 30))
                                        Toast.makeText(context, "Reminder set for 8:30 PM", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("8:30 PM", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateSettings(settings.copy(reminderHour = 21, reminderMinute = 0))
                                        Toast.makeText(context, "Reminder set for 9:00 PM", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("9:00 PM", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateSettings(settings.copy(reminderHour = 22, reminderMinute = 0))
                                        Toast.makeText(context, "Reminder set for 10:00 PM", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("10:00 PM", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "🔔 PaisaWise Reminder: Did you log your ₹70 morning tiffin, night food & porter spends today?",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test Reminder Alert Now", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Section 3: Company Porter & Errand Reimbursement Manager
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("card_company_claims")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(BlueCompany.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = BlueCompany
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Company & Porter Spends",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Track reimbursements from office",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${pendingClaims.toInt()} Pending",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (pendingClaims > 0) BlueCompany else GreenGain
                                    )
                                )
                                Text(
                                    text = if (pendingClaims == 0.0) "All Claimed!" else "Claim from HR",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (reimbursableExpenses.isEmpty()) {
                            Text(
                                text = "No company or Porter delivery spends logged yet. When you pay for courier or office errands, check 'Reimbursable by Company' so it shows here!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            reimbursableExpenses.forEach { exp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exp.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text("₹${exp.amount.toInt()} • ${if (exp.isReimbursed) "Claimed" else "Unpaid"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Button(
                                        onClick = { viewModel.toggleReimbursementStatus(exp) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (exp.isReimbursed) "Mark Unpaid" else "Mark Claimed", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Stipend & Fixed Costs Setup
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Internship Budget Overview",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            IconButton(onClick = { isEditingBudget = !isEditingBudget }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit budget")
                            }
                        }

                        if (!isEditingBudget) {
                            BudgetItemRow("Monthly Stipend", "₹${settings.monthlyStipend.toInt()}")
                            BudgetItemRow("Room Rent (Total ₹4,000 / 2)", "₹${settings.myRentShare.toInt()}")
                            BudgetItemRow("Food (₹70 Morning + ₹35 Night)", "₹${settings.monthlyFoodEstimate.toInt()}/mo")
                            BudgetItemRow("Light Bill (₹1,500 split)", "₹${settings.myLightBillShare.toInt()}")
                            BudgetItemRow("Mobile Recharge", "₹${settings.mobileRechargeBudget.toInt()}")
                            BudgetItemRow("Transport", "₹${settings.transportBudget.toInt()}")
                            BudgetItemRow("Health Care", "₹${settings.healthBudget.toInt()}")
                            Spacer(modifier = Modifier.height(8.dp))
                            BudgetItemRow("Target Monthly Savings", "₹${settings.projectedMonthlySavings.toInt()}", isHighlight = true)
                        } else {
                            OutlinedTextField(
                                value = stipendInput,
                                onValueChange = { stipendInput = it },
                                label = { Text("Monthly Stipend (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = roomRentInput,
                                onValueChange = { roomRentInput = it },
                                label = { Text("Total Room Rent (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = morningFoodInput,
                                    onValueChange = { morningFoodInput = it },
                                    label = { Text("Morning Food (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = nightFoodInput,
                                    onValueChange = { nightFoodInput = it },
                                    label = { Text("Night Food (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val st = stipendInput.toDoubleOrNull() ?: 15000.0
                                    val rent = roomRentInput.toDoubleOrNull() ?: 4000.0
                                    val mFood = morningFoodInput.toDoubleOrNull() ?: 70.0
                                    val nFood = nightFoodInput.toDoubleOrNull() ?: 35.0
                                    viewModel.updateSettings(
                                        settings.copy(
                                            monthlyStipend = st,
                                            roomRentTotal = rent,
                                            morningFoodBudget = mFood,
                                            nightFoodBudget = nFood
                                        )
                                    )
                                    isEditingBudget = false
                                    Toast.makeText(context, "Budget parameters updated!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Configuration")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetItemRow(title: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlight) EmeraldPrimaryLight else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) EmeraldPrimaryLight else MaterialTheme.colorScheme.onSurface
        )
    }
}
