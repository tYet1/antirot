package com.antirot.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.antirot.app.ui.components.AntirotBottomBar
import com.antirot.app.ui.insights.InsightsScreen
import com.antirot.app.ui.limits.LimitsScreen
import com.antirot.app.ui.settings.SettingsScreen
import com.antirot.app.ui.today.TodayScreen

@Composable
fun AntirotNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AntirotBottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Today.route) { TodayScreen() }
            composable(Screen.Limits.route) { LimitsScreen() }
            composable(Screen.Insights.route) { InsightsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

sealed class Screen(val route: String) {
    data object Today : Screen("today")
    data object Limits : Screen("limits")
    data object Insights : Screen("insights")
    data object Settings : Screen("settings")
}
