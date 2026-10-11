package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.NavDestination
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.BudgetAndAlertsScreen
import com.example.ui.screens.DailyExpenseScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.RentAndMilestonesScreen
import com.example.ui.theme.PaisaWiseTheme
import com.example.ui.viewmodel.PaisaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaisaWiseTheme {
                PaisaWiseApp()
            }
        }
    }
}

@Composable
fun PaisaWiseApp(
    viewModel: PaisaViewModel = viewModel()
) {
    var currentDestination by rememberSaveable { mutableStateOf(NavDestination.DASHBOARD) }
    val context = LocalContext.current

    // Request notification permission on Android 13+ for custom daily reminders
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission granted or denied handled gracefully
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Handle back button: if not on dashboard, return to dashboard
    if (currentDestination != NavDestination.DASHBOARD) {
        BackHandler {
            currentDestination = NavDestination.DASHBOARD
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(destination.testTag)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToDaily = { _ ->
                        currentDestination = NavDestination.DAILY_SPEND
                    },
                    onNavigateToMilestones = {
                        currentDestination = NavDestination.RENT_MILESTONES
                    },
                    onNavigateToBudget = {
                        currentDestination = NavDestination.BUDGET_ALERTS
                    }
                )

                NavDestination.DAILY_SPEND -> DailyExpenseScreen(
                    viewModel = viewModel
                )

                NavDestination.RENT_MILESTONES -> RentAndMilestonesScreen(
                    viewModel = viewModel
                )

                NavDestination.BUDGET_ALERTS -> BudgetAndAlertsScreen(
                    viewModel = viewModel
                )

                NavDestination.AI_ADVISOR -> AiAdvisorScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
