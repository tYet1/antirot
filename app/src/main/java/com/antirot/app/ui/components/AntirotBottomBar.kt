package com.antirot.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.antirot.app.ui.Screen

private data class NavItem(val screen: Screen, val label: String, val icon: @Composable () -> Unit)

@Composable
fun AntirotBottomBar(navController: NavController) {
    val items = listOf(
        NavItem(Screen.Today, "Today") { Icon(Icons.Default.Home, "Today") },
        NavItem(Screen.Limits, "Limits") { Icon(Icons.Default.Timer, "Limits") },
        NavItem(Screen.Insights, "Insights") { Icon(Icons.Default.BarChart, "Insights") },
        NavItem(Screen.Settings, "Settings") { Icon(Icons.Default.Settings, "Settings") },
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = item.icon,
                label = { Text(item.label) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Today.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
