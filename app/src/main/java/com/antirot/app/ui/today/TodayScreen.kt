package com.antirot.app.ui.today

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.components.AppCard
import com.antirot.app.ui.components.EmptyState
import com.antirot.app.ui.components.LimitProgressBar
import com.antirot.app.ui.components.PrimaryButton
import com.antirot.app.ui.components.ProgressRing
import com.antirot.app.ui.components.SectionHeader
import com.antirot.app.ui.components.StatTile
import com.antirot.app.ui.theme.spacing

@Composable
fun TodayScreen(viewModel: TodayViewModel, onNavigateToLimits: () -> Unit, onEditRule: (String) -> Unit) {
    val context = LocalContext.current
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
    val appsProgress by viewModel.appsProgress.collectAsState()
    val rules by viewModel.rules.collectAsState()
    val totalMinutes by viewModel.totalScreenTimeMinutes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshUsage()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        item {
            Spacer(Modifier.height(MaterialTheme.spacing.small))
            SectionHeader(
                title = "Today's Focus",
                subtitle = "Mindful screen time breakdown",
            )
        }

        // Service Warning if not active
        if (!isServiceRunning) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(Modifier.width(MaterialTheme.spacing.small))
                            Column {
                                Text(
                                    text = "Accessibility Service is off",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                                Text(
                                    text = "Required to detect when limited apps open.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                        ) {
                            Text("Enable", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                val hours = totalMinutes / 60
                val mins = totalMinutes % 60
                val timeString = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                StatTile(
                    value = timeString,
                    label = "Limited screentime",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = "${rules.count { !it.isPaused }}",
                    label = "Active limits",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Apps Progress List
        if (appsProgress.isEmpty()) {
            item {
                EmptyState(
                    title = "No Limits Tracked Today",
                    description = "Configure apps in Limits tab to monitor and limit your sessions.",
                    icon = Icons.Default.HourglassEmpty,
                    action = {
                        PrimaryButton(
                            text = "Add a limit",
                            onClick = onNavigateToLimits,
                        )
                    },
                )
            }
        } else {
            item {
                SectionHeader(
                    title = "Monitored Apps",
                    subtitle = "Usage vs configured limits",
                )
            }

            items(appsProgress, key = { it.rule.packageName }) { appProgress ->
                TodayAppUsageCard(
                    progress = appProgress,
                    onClick = { onEditRule(appProgress.rule.packageName) },
                )
            }
        }

        item {
            Spacer(Modifier.height(MaterialTheme.spacing.large))
        }
    }
}

@Composable
private fun TodayAppUsageCard(progress: MonitoredAppProgress, onClick: () -> Unit) {
    val rule = progress.rule
    val limit = if (rule.dailyLimitMinutes > 0) rule.dailyLimitMinutes else rule.sessionLimitMinutes

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProgressRing(
                progress = progress.progressRatio,
                modifier = Modifier.size(56.dp),
                strokeWidth = 6.dp,
            )

            Spacer(Modifier.width(MaterialTheme.spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (limit > 0) {
                        "${progress.usageMinutes}m used of ${limit}m limit"
                    } else {
                        "${progress.usageMinutes}m used (no hard limit)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))

                LimitProgressBar(
                    progress = progress.progressRatio,
                    warningThreshold = if (rule.warningThresholdMinutes > 0 && limit > 0) {
                        (limit - rule.warningThresholdMinutes).toFloat() / limit
                    } else {
                        0.8f
                    },
                )
            }
        }
    }
}
