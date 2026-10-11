package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.RedExpense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailySpendPoint(
    val dayLabel: String,
    val dateMillis: Long,
    val amount: Double
)

@Composable
fun DailySpendBarChart(
    expenses: List<ExpenseEntity>,
    dailyLimit: Double,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyPoints = remember(expenses) {
        val list = mutableListOf<DailySpendPoint>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        // Calculate for the past 7 days
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = c.timeInMillis
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000L

            val dayTotal = expenses.filter {
                it.dateMillis in startOfDay until endOfDay && (!it.isReimbursable || it.isReimbursed)
            }.sumOf { it.amount }

            list.add(
                DailySpendPoint(
                    dayLabel = dayFormat.format(Date(startOfDay)),
                    dateMillis = startOfDay,
                    amount = dayTotal
                )
            )
        }
        list
    }

    val maxAmount = remember(dailyPoints, dailyLimit) {
        val peak = dailyPoints.maxOfOrNull { it.amount } ?: 100.0
        maxOf(peak, dailyLimit * 1.25, 200.0)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "7-Day Daily Spending Trend",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Target limit: ₹${dailyLimit.toInt()}/day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(EmeraldPrimaryLight, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OK", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(RedExpense, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Over", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area
            val pointsCount = dailyPoints.size
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val barWidth = canvasWidth / (pointsCount * 2f)
                    val limitY = canvasHeight - (canvasHeight * (dailyLimit / maxAmount).toFloat())

                    // Draw target limit dashed line
                    drawLine(
                        color = AmberAlert,
                        start = Offset(0f, limitY),
                        end = Offset(canvasWidth, limitY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    // Draw bars
                    dailyPoints.forEachIndexed { index, point ->
                        val barHeight = (canvasHeight * (point.amount / maxAmount).toFloat()).coerceAtLeast(6f)
                        val x = (index * 2f + 0.5f) * barWidth
                        val y = canvasHeight - barHeight

                        val barColor = when {
                            point.amount == 0.0 -> Color.LightGray.copy(alpha = 0.4f)
                            point.amount <= dailyLimit -> EmeraldPrimaryLight
                            point.amount <= dailyLimit * 1.3 -> AmberAlert
                            else -> RedExpense
                        }

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day labels and amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dailyPoints.forEach { point ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onDaySelected(point.dateMillis) }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = if (point.amount > 0) "₹${point.amount.toInt()}" else "-",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (point.amount > dailyLimit) RedExpense else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = point.dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
