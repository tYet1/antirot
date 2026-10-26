package com.antirot.app.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.components.AppCard
import com.antirot.app.ui.components.EmptyState
import com.antirot.app.ui.components.ErrorState
import com.antirot.app.ui.components.LimitProgressBar
import com.antirot.app.ui.components.PrimaryButton
import com.antirot.app.ui.components.ProgressRing
import com.antirot.app.ui.components.SecondaryButton
import com.antirot.app.ui.components.SectionHeader
import com.antirot.app.ui.components.StatTile
import com.antirot.app.ui.theme.AntirotTheme
import com.antirot.app.ui.theme.ThemePreset
import com.antirot.app.ui.theme.semanticColors
import com.antirot.app.ui.theme.spacing

@Composable
fun ComponentGalleryContent(themeName: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Text(
                text = "Theme: $themeName",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            // Section Header
            SectionHeader(
                title = "App Limits",
                subtitle = "Manage foreground sessions",
                actionLabel = "Add limit",
                onActionClick = {},
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                PrimaryButton(
                    text = "Continue",
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Go Home",
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }

            PrimaryButton(
                text = "Loading State",
                onClick = {},
                loading = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Stat Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                StatTile(
                    label = "Total Today",
                    value = "1h 45m",
                    subtext = "3 apps tracked",
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(MaterialTheme.spacing.iconSmall),
                        )
                    },
                )
                StatTile(
                    label = "Remaining",
                    value = "15m",
                    subtext = "Instagram limit",
                    valueColor = MaterialTheme.semanticColors.warning,
                    modifier = Modifier.weight(1f),
                )
            }

            // Progress Ring & Progress Bars
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    Text(
                        text = "Session Progress",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        ProgressRing(
                            progress = 0.75f,
                            modifier = Modifier.size(96.dp),
                            strokeWidth = 10.dp,
                            progressColor = MaterialTheme.semanticColors.timerProgress,
                        ) {
                            Text(
                                text = "75%",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        ProgressRing(
                            progress = 0.95f,
                            modifier = Modifier.size(96.dp),
                            strokeWidth = 10.dp,
                            progressColor = MaterialTheme.semanticColors.danger,
                        ) {
                            Text(
                                text = "1m",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.semanticColors.danger,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))

                    Text(
                        text = "Daily Limit (Normal)",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    LimitProgressBar(progress = 0.45f)

                    Text(
                        text = "Daily Limit (Warning at 85%)",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    LimitProgressBar(progress = 0.85f)

                    Text(
                        text = "Daily Limit (Exhausted at 100%)",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    LimitProgressBar(progress = 1.0f)
                }
            }

            // Empty State
            AppCard(modifier = Modifier.fillMaxWidth()) {
                EmptyState(
                    title = "No Rules Configured",
                    description = "Choose apps to track foreground time and add reflection screens.",
                    icon = Icons.Default.HourglassEmpty,
                    action = {
                        PrimaryButton(
                            text = "Add First App",
                            onClick = {},
                        )
                    },
                )
            }

            // Error State
            AppCard(modifier = Modifier.fillMaxWidth()) {
                ErrorState(
                    title = "Permission Required",
                    message = "Accessibility service is currently disabled. Enable it to enforce limits.",
                    onRetry = {},
                    retryLabel = "Open Settings",
                )
            }
        }
    }
}

// =============================================================================
// Preview Gallery: Pink + Light Blue (Light & Dark)
// =============================================================================

@Preview(name = "Pink + Light Blue (Light)", showBackground = true)
@Composable
private fun PreviewPinkLightBlueLight() {
    AntirotTheme(preset = ThemePreset.PINK_LIGHT_BLUE, darkTheme = false) {
        ComponentGalleryContent(themeName = "Pink + Light Blue (Light)")
    }
}

@Preview(
    name = "Pink + Light Blue (Dark)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewPinkLightBlueDark() {
    AntirotTheme(preset = ThemePreset.PINK_LIGHT_BLUE, darkTheme = true) {
        ComponentGalleryContent(themeName = "Pink + Light Blue (Dark)")
    }
}

// =============================================================================
// Preview Gallery: Black + Yellow + Red (Dark & Light)
// =============================================================================

@Preview(
    name = "Black + Yellow + Red (Dark)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewBlackYellowRedDark() {
    AntirotTheme(preset = ThemePreset.BLACK_YELLOW_RED, darkTheme = true) {
        ComponentGalleryContent(themeName = "Black + Yellow + Red (Dark)")
    }
}

@Preview(name = "Black + Yellow + Red (Light)", showBackground = true)
@Composable
private fun PreviewBlackYellowRedLight() {
    AntirotTheme(preset = ThemePreset.BLACK_YELLOW_RED, darkTheme = false) {
        ComponentGalleryContent(themeName = "Black + Yellow + Red (Light)")
    }
}

// =============================================================================
// Preview Gallery: Green + White (Light & Dark)
// =============================================================================

@Preview(name = "Green + White (Light)", showBackground = true)
@Composable
private fun PreviewGreenWhiteLight() {
    AntirotTheme(preset = ThemePreset.GREEN_WHITE, darkTheme = false) {
        ComponentGalleryContent(themeName = "Green + White (Light)")
    }
}

@Preview(
    name = "Green + White (Dark)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGreenWhiteDark() {
    AntirotTheme(preset = ThemePreset.GREEN_WHITE, darkTheme = true) {
        ComponentGalleryContent(themeName = "Green + White (Dark)")
    }
}
