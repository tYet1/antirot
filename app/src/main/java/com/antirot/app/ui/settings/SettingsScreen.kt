package com.antirot.app.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.components.AppCard
import com.antirot.app.ui.components.SectionHeader
import com.antirot.app.ui.theme.ThemePreset
import com.antirot.app.ui.theme.spacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val currentPreset by viewModel.themePreset.collectAsState()
    val isA11yRunning by viewModel.isAccessibilityServiceRunning.collectAsState()
    val isUsageGranted by viewModel.isUsageAccessGranted.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Spacer(Modifier.height(MaterialTheme.spacing.small))
        SectionHeader(
            title = "Settings",
            subtitle = "Theme, permissions & privacy",
        )

        // Theme Preset Picker
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.small))
                    Text(
                        text = "Color Theme",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(Modifier.height(MaterialTheme.spacing.small))

                FlowRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                    val presets = listOf(
                        ThemePreset.PINK_LIGHT_BLUE to "Pink + Light Blue",
                        ThemePreset.BLACK_YELLOW_RED to "Black + Yellow + Red",
                        ThemePreset.GREEN_WHITE to "Green + White",
                    )
                    presets.forEach { (preset, label) ->
                        FilterChip(
                            selected = currentPreset == preset,
                            onClick = { viewModel.setThemePreset(preset) },
                            label = { Text(label) },
                        )
                    }
                }
            }
        }

        // Permissions & Services
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    text = "System Services & Permissions",
                    style = MaterialTheme.typography.titleMedium,
                )

                // Accessibility Service
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isA11yRunning) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isA11yRunning) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(MaterialTheme.spacing.extraSmall))
                            Text(
                                "Accessibility Service",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Text(
                            if (isA11yRunning) {
                                "Active — monitoring target app openings"
                            } else {
                                "Disabled — limits cannot be detected"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (!isA11yRunning) {
                        TextButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                        ) {
                            Text("Enable")
                        }
                    }
                }

                // Usage Access
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isUsageGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isUsageGranted) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(MaterialTheme.spacing.extraSmall))
                            Text(
                                "Usage Stats Access",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Text(
                            if (isUsageGranted) {
                                "Granted — reconciling daily statistics"
                            } else {
                                "Optional — grants system-accurate daily totals"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (!isUsageGranted) {
                        TextButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            },
                        ) {
                            Text("Grant")
                        }
                    }
                }
            }
        }

        // Privacy & Offline Promise
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.small))
                    Text(
                        text = "100% Offline & Private",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                Text(
                    text = "Antirot has zero network permissions. No accounts, no cloud sync, " +
                        "and no analytics or telemetry SDKs exist in this app. All session records, " +
                        "rules, and quotes remain strictly on your local device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Danger Zone: Reset Data
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Data Controls",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                OutlinedButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Clear All Usage History & Settings", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Data?") },
            text = {
                Text(
                    "This will permanently delete all session history and reset your preferences. " +
                        "This action cannot be undone.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    },
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
