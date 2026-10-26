package com.antirot.app.ui.limits

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.antirot.app.data.local.entity.AppRuleEntity
import com.antirot.app.ui.components.AppCard
import com.antirot.app.ui.components.PrimaryButton
import com.antirot.app.ui.components.SecondaryButton
import com.antirot.app.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditRuleScreen(packageName: String, viewModel: LimitsViewModel, onBack: () -> Unit) {
    val appInfo = remember(packageName) { viewModel.getAppInfo(packageName) }
    val appName = appInfo?.appName ?: packageName

    var sessionLimitMinutes by remember { mutableIntStateOf(0) }
    var dailyLimitMinutes by remember { mutableIntStateOf(0) }
    var reminderIntervalMinutes by remember { mutableIntStateOf(0) }
    var reflectionEnabled by remember { mutableStateOf(false) }
    var reflectionDelaySeconds by remember { mutableIntStateOf(5) }
    var reflectionType by remember { mutableStateOf("QUOTE") }
    var reflectionPhotoUri by remember { mutableStateOf<String?>(null) }
    var customMessage by remember { mutableStateOf("") }
    var isExistingRule by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            reflectionPhotoUri = uri.toString()
        }
    }

    LaunchedEffect(packageName) {
        val existing = viewModel.getRule(packageName)
        if (existing != null) {
            isExistingRule = true
            sessionLimitMinutes = existing.sessionLimitMinutes
            dailyLimitMinutes = existing.dailyLimitMinutes
            reminderIntervalMinutes = existing.reminderIntervalMinutes
            reflectionEnabled = existing.reflectionEnabled
            reflectionDelaySeconds = existing.reflectionDelaySeconds
            reflectionType = existing.reflectionType
            reflectionPhotoUri = existing.reflectionPhotoUri
            customMessage = existing.customMessage ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isExistingRule) "Edit Limit" else "Configure Limit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isExistingRule) {
                        IconButton(onClick = {
                            viewModel.deleteRule(packageName)
                            onBack()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            // Header: App icon and info
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (appInfo?.icon != null) {
                        val bitmap = appInfo.icon.toBitmap(width = 56, height = 56)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = appName,
                            modifier = Modifier.size(48.dp),
                        )
                    } else {
                        Icon(
                            Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(Modifier.width(MaterialTheme.spacing.medium))

                    Column {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = packageName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Session Limit
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Per-Session Limit",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Maximum continuous time allowed before taking a break.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))

                    val sessionOptions = listOf(0 to "Off", 5 to "5m", 15 to "15m", 30 to "30m", 60 to "1h")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)) {
                        sessionOptions.forEach { (mins, label) ->
                            FilterChip(
                                selected = sessionLimitMinutes == mins,
                                onClick = { sessionLimitMinutes = mins },
                                label = { Text(label) },
                            )
                        }
                    }
                }
            }

            // Daily Limit
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Daily Limit",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Total cumulative usage allowed across the entire day.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))

                    val dailyOptions = listOf(0 to "Off", 30 to "30m", 60 to "1h", 120 to "2h", 240 to "4h")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)) {
                        dailyOptions.forEach { (mins, label) ->
                            FilterChip(
                                selected = dailyLimitMinutes == mins,
                                onClick = { dailyLimitMinutes = mins },
                                label = { Text(label) },
                            )
                        }
                    }
                }
            }

            // Reminders
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Session Reminders",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Periodic notification while you are using the app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))

                    val reminderOptions = listOf(0 to "Off", 5 to "Every 5m", 10 to "Every 10m", 15 to "Every 15m")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)) {
                        reminderOptions.forEach { (mins, label) ->
                            FilterChip(
                                selected = reminderIntervalMinutes == mins,
                                onClick = { reminderIntervalMinutes = mins },
                                label = { Text(label) },
                            )
                        }
                    }
                }
            }

            // Opening Reflection Interruption
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App-Opening Reflection",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "A mindful pause when opening the app.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = reflectionEnabled,
                            onCheckedChange = { reflectionEnabled = it },
                        )
                    }

                    if (reflectionEnabled) {
                        Spacer(Modifier.height(MaterialTheme.spacing.medium))

                        Text(
                            text = "Countdown Delay: ${reflectionDelaySeconds}s",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Slider(
                            value = reflectionDelaySeconds.toFloat(),
                            onValueChange = { reflectionDelaySeconds = it.toInt() },
                            valueRange = 0f..15f,
                            steps = 14,
                        )

                        Spacer(Modifier.height(MaterialTheme.spacing.small))

                        Text("Reflection Content", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                            listOf("QUOTE", "PHOTO", "BOTH").forEach { type ->
                                FilterChip(
                                    selected = reflectionType == type,
                                    onClick = { reflectionType = type },
                                    label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
                                )
                            }
                        }

                        if (reflectionType == "PHOTO" || reflectionType == "BOTH") {
                            Spacer(Modifier.height(MaterialTheme.spacing.small))
                            SecondaryButton(
                                text = if (reflectionPhotoUri == null) {
                                    "Select Photo from Device"
                                } else {
                                    "Change Selected Photo"
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                },
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                    )
                                },
                            )
                        }
                    }
                }
            }

            // Custom Message
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Custom Intervention Prompt",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Override the default mindful message shown on interruption.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    OutlinedTextField(
                        value = customMessage,
                        onValueChange = { customMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Is this essential right now?") },
                    )
                }
            }

            // Save Button
            PrimaryButton(
                text = "Save Limit",
                onClick = {
                    val rule = AppRuleEntity(
                        packageName = packageName,
                        appName = appName,
                        dailyLimitMinutes = dailyLimitMinutes,
                        sessionLimitMinutes = sessionLimitMinutes,
                        reminderIntervalMinutes = reminderIntervalMinutes,
                        reflectionEnabled = reflectionEnabled,
                        reflectionDelaySeconds = reflectionDelaySeconds,
                        reflectionType = reflectionType,
                        reflectionPhotoUri = reflectionPhotoUri,
                        customMessage = customMessage.ifBlank { null },
                    )
                    viewModel.saveRule(rule)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
