package com.antirot.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antirot.app.ui.components.AntirotBottomBar
import com.antirot.app.ui.insights.InsightsScreen
import com.antirot.app.ui.insights.InsightsViewModel
import com.antirot.app.ui.limits.ChooseAppsScreen
import com.antirot.app.ui.limits.EditRuleScreen
import com.antirot.app.ui.limits.LimitsScreen
import com.antirot.app.ui.limits.LimitsViewModel
import com.antirot.app.ui.settings.SettingsScreen
import com.antirot.app.ui.settings.SettingsViewModel
import com.antirot.app.ui.today.TodayScreen
import com.antirot.app.ui.today.TodayViewModel

@Composable
fun AntirotNavHost() {
    val navController = rememberNavController()
    val limitsViewModel: LimitsViewModel = hiltViewModel()
    val todayViewModel: TodayViewModel = hiltViewModel()
    val insightsViewModel: InsightsViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Today.route,
        Screen.Limits.route,
        Screen.Insights.route,
        Screen.Settings.route,
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AntirotBottomBar(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    viewModel = todayViewModel,
                    onNavigateToLimits = {
                        navController.navigate(Screen.Limits.route) {
                            popUpTo(Screen.Today.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onEditRule = { pkg ->
                        navController.navigate("edit_rule/$pkg")
                    },
                )
            }

            composable(Screen.Limits.route) {
                LimitsScreen(
                    viewModel = limitsViewModel,
                    onAddClick = {
                        navController.navigate("choose_apps")
                    },
                    onEditRule = { pkg ->
                        navController.navigate("edit_rule/$pkg")
                    },
                )
            }

            composable("choose_apps") {
                ChooseAppsScreen(
                    viewModel = limitsViewModel,
                    onBack = { navController.popBackStack() },
                    onSelectApp = { pkg ->
                        navController.navigate("edit_rule/$pkg") {
                            popUpTo("choose_apps") { inclusive = true }
                        }
                    },
                )
            }

            composable(
                route = "edit_rule/{packageName}",
                arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
            ) { backStackEntry ->
                val pkg = backStackEntry.arguments?.getString("packageName") ?: ""
                EditRuleScreen(
                    packageName = pkg,
                    viewModel = limitsViewModel,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Screen.Insights.route) {
                InsightsScreen(viewModel = insightsViewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Today : Screen("today")
    data object Limits : Screen("limits")
    data object Insights : Screen("insights")
    data object Settings : Screen("settings")
}
