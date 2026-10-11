package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlueCompany
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GoldSecondaryLight

@Composable
fun QuickAddBar(
    morningFoodAmount: Int,
    nightFoodAmount: Int,
    onAddMorning: () -> Unit,
    onAddNight: () -> Unit,
    onAddChai: () -> Unit,
    onAddPorter: () -> Unit,
    onAddCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onAddMorning,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.BakeryDining,
                    contentDescription = "Morning Food",
                    tint = GoldSecondaryLight,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = { Text("Morning ₹$morningFoodAmount", fontWeight = FontWeight.SemiBold) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("quick_add_morning_chip")
        )

        AssistChip(
            onClick = onAddNight,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Nightlife,
                    contentDescription = "Night Food",
                    tint = EmeraldPrimaryLight,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = { Text("Night ₹$nightFoodAmount", fontWeight = FontWeight.SemiBold) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("quick_add_night_chip")
        )

        AssistChip(
            onClick = onAddChai,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Coffee,
                    contentDescription = "Chai",
                    tint = GoldSecondaryLight,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = { Text("Chai ₹15", fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("quick_add_chai_chip")
        )

        AssistChip(
            onClick = onAddPorter,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "Porter",
                    tint = BlueCompany,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = { Text("Company Porter", fontWeight = FontWeight.SemiBold) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("quick_add_porter_chip")
        )

        AssistChip(
            onClick = onAddCustom,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add custom",
                    modifier = Modifier.size(18.dp)
                )
            },
            label = { Text("+ Custom Spend", fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("quick_add_custom_chip")
        )
    }
}
