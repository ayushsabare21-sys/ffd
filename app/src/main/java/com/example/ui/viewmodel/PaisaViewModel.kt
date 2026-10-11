package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PaisaWiseDatabase
import com.example.data.local.SettingsManager
import com.example.data.model.BudgetSettings
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.model.RentPaymentEntity
import com.example.data.model.SalaryCreditEntity
import com.example.data.remote.GeminiAdvisorClient
import com.example.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PaisaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = PaisaWiseDatabase.getDatabase(application, viewModelScope)
    private val repository = ExpenseRepository(
        database.expenseDao(),
        database.salaryDao(),
        database.rentDao()
    )
    private val settingsManager = SettingsManager(application)
    private val geminiClient = GeminiAdvisorClient()

    val settings: StateFlow<BudgetSettings> = settingsManager.settings

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salaryCredits: StateFlow<List<SalaryCreditEntity>> = repository.allSalaryCredits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salaryCount: StateFlow<Int> = repository.salaryCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSalary: StateFlow<Double> = repository.totalSalary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val rentPayments: StateFlow<List<RentPaymentEntity>> = repository.allRentPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentPaidCount: StateFlow<Int> = repository.rentPaidCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRentPaid: StateFlow<Double> = repository.totalMyShareRentPaid
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val reimbursableExpenses: StateFlow<List<ExpenseEntity>> = repository.reimbursableExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    // Today's total spend calculation
    val todayExpenses: StateFlow<List<ExpenseEntity>> = combine(allExpenses, _selectedDateMillis) { list, selectedDate ->
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
        val startOfDay = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000L

        list.filter { it.dateMillis in startOfDay until endOfDay }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotalSpend: StateFlow<Double> = todayExpenses.combine(todayExpenses) { list, _ ->
        list.filter { !it.isReimbursable || it.isReimbursed }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Current month total spend
    val monthlyTotalSpend: StateFlow<Double> = allExpenses.combine(settings) { list, _ ->
        val cal = Calendar.getInstance()
        val startOfMonth = cal.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        list.filter { it.dateMillis >= startOfMonth && (!it.isReimbursable || it.isReimbursed) }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Pending reimbursement amount
    val pendingReimbursementAmount: StateFlow<Double> = reimbursableExpenses.combine(reimbursableExpenses) { list, _ ->
        list.filter { !it.isReimbursed }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Gemini Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            sender = ChatSender.AI,
            text = "Namaste! I am your PaisaWise Financial Coach. I'm here to help you maximize your ₹15,000 internship salary, manage your ₹2,000 split room rent, track daily food (₹70 morning + ₹35 night), handle office porter claims, and build emergency savings. How can I help you today?"
        )
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    fun setSelectedDate(millis: Long) {
        _selectedDateMillis.value = millis
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: ExpenseCategory,
        dateMillis: Long = System.currentTimeMillis(),
        isReimbursable: Boolean = false,
        isPresetFood: Boolean = false,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category.name,
                    dateMillis = dateMillis,
                    isReimbursable = isReimbursable,
                    isPresetFood = isPresetFood,
                    note = note
                )
            )
        }
    }

    fun quickAddMorningFood(customNote: String = "") {
        val st = settings.value
        addExpense(
            title = "Morning Breakfast & Tiffin",
            amount = st.morningFoodBudget,
            category = ExpenseCategory.FOOD,
            isPresetFood = true,
            note = customNote.ifBlank { "Daily morning food ₹${st.morningFoodBudget.toInt()}" }
        )
    }

    fun quickAddNightFood(customNote: String = "") {
        val st = settings.value
        addExpense(
            title = "Night Dinner",
            amount = st.nightFoodBudget,
            category = ExpenseCategory.FOOD,
            isPresetFood = true,
            note = customNote.ifBlank { "Daily night dinner ₹${st.nightFoodBudget.toInt()}" }
        )
    }

    fun quickAddChai(amount: Double = 15.0) {
        addExpense(
            title = "Chai & Tea Break",
            amount = amount,
            category = ExpenseCategory.FOOD,
            isPresetFood = true,
            note = "Chai break"
        )
    }

    fun quickAddPorterCompanySpend(amount: Double, description: String) {
        addExpense(
            title = "Company Porter / Courier",
            amount = amount,
            category = ExpenseCategory.WORK_PORTER,
            isReimbursable = true,
            note = description.ifBlank { "Porter delivery expense (Claim reimbursement from company)" }
        )
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpenseById(id)
        }
    }

    fun toggleReimbursementStatus(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(
                expense.copy(isReimbursed = !expense.isReimbursed)
            )
        }
    }

    fun addSalaryCredit(
        amount: Double = 15000.0,
        dateMillis: Long = System.currentTimeMillis(),
        source: String = "Internship Stipend",
        monthLabel: String = "",
        note: String = ""
    ) {
        viewModelScope.launch {
            val label = if (monthLabel.isBlank()) {
                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(dateMillis))
            } else monthLabel
            repository.insertSalary(
                SalaryCreditEntity(
                    amount = amount,
                    dateMillis = dateMillis,
                    companyOrSource = source,
                    monthLabel = label,
                    note = note
                )
            )
        }
    }

    fun deleteSalaryCredit(id: Long) {
        viewModelScope.launch {
            repository.deleteSalaryById(id)
        }
    }

    fun addRentPayment(
        totalRent: Double = 4000.0,
        myShare: Double = 2000.0,
        roommateShare: Double = 2000.0,
        dateMillis: Long = System.currentTimeMillis(),
        ownerName: String = "Room Owner",
        paymentMode: String = "UPI (GPay/PhonePe)",
        receiptNote: String = ""
    ) {
        viewModelScope.launch {
            val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(dateMillis))
            repository.insertRentPayment(
                RentPaymentEntity(
                    totalRent = totalRent,
                    myShare = myShare,
                    roommateShare = roommateShare,
                    dateMillis = dateMillis,
                    monthLabel = monthLabel,
                    ownerName = ownerName,
                    paymentMode = paymentMode,
                    isConfirmedPaid = true,
                    receiptNote = receiptNote
                )
            )
            // Also log the user's rent share into expenses table so monthly tracking accounts for it
            repository.insertExpense(
                ExpenseEntity(
                    title = "Room Rent Paid to $ownerName",
                    amount = myShare,
                    category = ExpenseCategory.ROOM_RENT.name,
                    dateMillis = dateMillis,
                    note = "Total ₹${totalRent.toInt()} split (My share: ₹${myShare.toInt()})"
                )
            )
        }
    }

    fun deleteRentPayment(id: Long) {
        viewModelScope.launch {
            repository.deleteRentPaymentById(id)
        }
    }

    fun updateSettings(newSettings: BudgetSettings) {
        settingsManager.updateSettings(newSettings)
    }

    fun sendChatMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(sender = ChatSender.USER, text = trimmed)
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(userMsg)
        _chatMessages.value = currentList
        _isAiThinking.value = true

        viewModelScope.launch {
            val st = settings.value
            val todaySpent = todayTotalSpend.value
            val monthlySpent = monthlyTotalSpend.value
            val salCount = salaryCount.value
            val rentCount = rentPaidCount.value
            val pendingClaims = pendingReimbursementAmount.value

            val systemInstruction = """
                You are PaisaWise AI, an expert, encouraging financial coach specialized for an Indian student or graduate interning on a ₹${st.monthlyStipend.toInt()} INR monthly stipend.
                The user's setup:
                - Monthly Internship Stipend: ₹${st.monthlyStipend.toInt()}
                - Room Rent: ₹${st.roomRentTotal.toInt()} total, split among ${st.roommateCount} people -> User share is ₹${st.myRentShare.toInt()}
                - Food Routine: Morning Breakfast/Tiffin ₹${st.morningFoodBudget.toInt()} + Night Dinner ₹${st.nightFoodBudget.toInt()} (= ₹${st.dailyFoodEstimate.toInt()}/day, ~₹${st.monthlyFoodEstimate.toInt()}/month)
                - Light / Electricity Bill: ₹${st.lightBillTotal.toInt()} (User share: ₹${st.myLightBillShare.toInt()})
                - Mobile Recharge: ₹${st.mobileRechargeBudget.toInt()}
                - Transport: ₹${st.transportBudget.toInt()}
                - Health & Medical buffer: ₹${st.healthBudget.toInt()}
                - Daily spending target: ₹${st.dailySpendLimit.toInt()}/day
                - Total Salary Received: $salCount times
                - Total Room Rent Paid to Owner: $rentCount times
                - Today's actual spend so far: ₹${todaySpent.toInt()}
                - Month's total spend so far: ₹${monthlySpent.toInt()}
                - Unreimbursed Company/Porter Expenses: ₹${pendingClaims.toInt()}

                Guidelines:
                - Provide practical, realistic advice in Indian context (use ₹ and INR).
                - Explain how to stay within budget, claim office porter/courier reimbursements promptly, save at least ₹3,000-₹5,000 monthly, and build an emergency fund.
                - Keep responses crisp, well-structured with clear bullet points.
                - Tone: Friendly, motivating, practical, and financial-savvy.
            """.trimIndent()

            val result = geminiClient.sendChatConversation(systemInstruction, currentList)
            _isAiThinking.value = false

            result.onSuccess { reply ->
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = ChatSender.AI,
                    text = reply
                )
            }.onFailure { err ->
                val fallbackAnswer = generateOfflineFinancialAdvice(trimmed, st, todaySpent, monthlySpent, pendingClaims)
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = ChatSender.AI,
                    text = fallbackAnswer
                )
            }
        }
    }

    private fun generateOfflineFinancialAdvice(
        query: String,
        st: BudgetSettings,
        todaySpent: Double,
        monthlySpent: Double,
        pendingClaims: Double
    ): String {
        val q = query.lowercase()
        return when {
            q.contains("porter") || q.contains("company") || q.contains("reimburse") -> {
                "💼 **Company & Porter Payments Tip:**\n\n" +
                "• Currently you have **₹${pendingClaims.toInt()}** pending in reimbursable office spends.\n" +
                "• **Immediate Action:** Keep the Porter receipt/tracking link and bill screenshot.\n" +
                "• Submit your reimbursement claim to HR/Finance every Friday so your stipend balance is not locked up!\n" +
                "• Never let company expenses stay uncollected past month-end."
            }
            q.contains("save") || q.contains("stipend") || q.contains("budget") -> {
                "💰 **Monthly Savings Strategy on ₹${st.monthlyStipend.toInt()}:**\n\n" +
                "• **Fixed Commitments:**\n" +
                "  - Rent share (₹4,000/2): ₹${st.myRentShare.toInt()}\n" +
                "  - Food (₹70+₹35 x 30 days): ₹${st.monthlyFoodEstimate.toInt()}\n" +
                "  - Light bill share: ₹${st.myLightBillShare.toInt()}\n" +
                "  - Mobile recharge: ₹${st.mobileRechargeBudget.toInt()}\n" +
                "  - Transport: ₹${st.transportBudget.toInt()}\n" +
                "  - Health buffer: ₹${st.healthBudget.toInt()}\n" +
                "• **Total Estimated Needs:** ~₹${st.estimatedTotalFixedExpenses.toInt()}\n" +
                "• **Projected Monthly Savings:** **₹${st.projectedMonthlySavings.toInt()}**!\n" +
                "• Keep your daily discretionary spends under ₹${st.dailySpendLimit.toInt()} to guarantee ₹5,000+ in savings every month."
            }
            q.contains("food") || q.contains("morning") || q.contains("night") -> {
                "🍛 **Food Budget Optimization:**\n\n" +
                "• Your plan is ₹70 morning + ₹35 night = **₹105/day** (~₹3,150/month). This is exceptionally disciplined!\n" +
                "• Make sure you get sufficient protein (eggs, dal, chana, sprouts) to maintain energy at your internship.\n" +
                "• Avoid impulsive Swiggy/Zomato snacks which can wipe out 3 days of food budget in one meal."
            }
            q.contains("rent") || q.contains("owner") || q.contains("landlord") -> {
                "🏠 **Room Rent Protocol:**\n\n" +
                "• Total room rent is ₹${st.roomRentTotal.toInt()}, your 50% split is ₹${st.myRentShare.toInt()}.\n" +
                "• Always transfer via UPI with transaction note like *'Rent Oct 2026 Sharma Ji'*.\n" +
                "• You have logged payment to owner successfully. Keep logging every month to have an undeniable digital record."
            }
            else -> {
                "📊 **PaisaWise Quick Insights:**\n\n" +
                "• **Today's Spend:** ₹${todaySpent.toInt()} / Target ₹${st.dailySpendLimit.toInt()}\n" +
                "• **Month-to-Date Spend:** ₹${monthlySpent.toInt()} of ₹${st.monthlyStipend.toInt()} stipend\n" +
                "• **Savings Target:** ~₹${st.projectedMonthlySavings.toInt()} this month\n\n" +
                "💡 *Pro-tip:* If you configure your Gemini API Key in the Secrets panel, I can provide dynamic live answers for any personalized question!"
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = ChatSender.AI,
                text = "Chat cleared. What financial question can I help you with?"
            )
        )
    }
}
