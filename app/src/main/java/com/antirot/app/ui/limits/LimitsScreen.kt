package com.antirot.app.ui.limits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antirot.app.data.local.entity.AppRuleEntity
import com.antirot.app.ui.components.AppCard
import com.antirot.app.ui.components.EmptyState
import com.antirot.app.ui.components.PrimaryButton
import com.antirot.app.ui.components.SectionHeader
import com.antirot.app.ui.theme.spacing

@Composable
fun LimitsScreen(viewModel: LimitsViewModel, onAddClick: () -> Unit, onEditRule: (String) -> Unit) {
    val rules by viewModel.rules.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (rules.isEmpty()) {
            EmptyState(
                title = "No App Limits Set",
                description = "Take back control of your screen time by creating " +
                    "a daily or session limit for distracting apps.",
                icon = Icons.Default.HourglassEmpty,
                action = {
                    PrimaryButton(
                        text = "Add your first limit",
                        onClick = onAddClick,
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.medium),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                item {
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SectionHeader(
                        title = "App Limits",
                        subtitle = "${rules.size} active monitored app${if (rules.size == 1) "" else "s"}",
                    )
                }

                items(rules, key = { it.packageName }) { rule ->
                    RuleItemCard(
                        rule = rule,
                        onTogglePause = { viewModel.togglePause(rule) },
                        onDelete = { viewModel.deleteRule(rule.packageName) },
                        onClick = { onEditRule(rule.packageName) },
                    )
                }

                item {
                    Spacer(Modifier.height(80.dp)) // padding for FAB
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(MaterialTheme.spacing.large),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Limit")
        }
    }
}

@Composable
private fun RuleItemCard(rule: AppRuleEntity, onTogglePause: () -> Unit, onDelete: () -> Unit, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (rule.isPaused) "Paused" else "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (rule.isPaused) {
                            MaterialTheme.colorScheme.outline
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = !rule.isPaused,
                        onCheckedChange = { onTogglePause() },
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.extraSmall))
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Rule",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))

            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
            ) {
                if (rule.sessionLimitMinutes > 0) {
                    SuggestionChip(
                        onClick = onClick,
                        label = { Text("${rule.sessionLimitMinutes}m session") },
                    )
                }
                if (rule.dailyLimitMinutes > 0) {
                    SuggestionChip(
                        onClick = onClick,
                        label = { Text("${rule.dailyLimitMinutes}m daily") },
                    )
                }
                if (rule.reflectionEnabled) {
                    SuggestionChip(
                        onClick = onClick,
                        icon = { Icon(Icons.Default.SelfImprovement, null, Modifier.size(16.dp)) },
                        label = { Text("${rule.reflectionDelaySeconds}s reflect") },
                    )
                }
                if (rule.reminderIntervalMinutes > 0) {
                    SuggestionChip(
                        onClick = onClick,
                        icon = { Icon(Icons.Default.NotificationsActive, null, Modifier.size(16.dp)) },
                        label = { Text("${rule.reminderIntervalMinutes}m remind") },
                    )
                }
            }
        }
    }
}
