package com.antirot.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalThemePreset = staticCompositionLocalOf { ThemePreset.DEFAULT }

val MaterialTheme.themePreset: ThemePreset
    @Composable
    @ReadOnlyComposable
    get() = LocalThemePreset.current

/**
 * Main theme provider for the Antirot app.
 *
 * Supports three preset palettes:
 * - [ThemePreset.PINK_LIGHT_BLUE]: Soft pink surfaces with light-blue accents.
 * - [ThemePreset.BLACK_YELLOW_RED]: Dark/black surfaces, yellow highlights, red for limit warnings.
 * - [ThemePreset.GREEN_WHITE]: Clean white surfaces with crisp green accents.
 */
@Composable
fun AntirotTheme(
    preset: ThemePreset = ThemePreset.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // For Black + Yellow + Red, default to dark unless explicitly requested otherwise,
    // as dark/black surfaces are its primary defining characteristic.
    val effectiveDarkTheme = if (preset == ThemePreset.BLACK_YELLOW_RED) {
        // If system is dark, always dark. If system is light, Black-Yellow-Red can still be dark
        // unless darkTheme is explicitly false. Here we respect the darkTheme parameter.
        darkTheme
    } else {
        darkTheme
    }

    val colorScheme = when (preset) {
        ThemePreset.PINK_LIGHT_BLUE ->
            if (effectiveDarkTheme) PinkLightBlueDarkColors else PinkLightBlueLightColors

        ThemePreset.BLACK_YELLOW_RED ->
            if (effectiveDarkTheme) BlackYellowRedDarkColors else BlackYellowRedLightColors

        ThemePreset.GREEN_WHITE ->
            if (effectiveDarkTheme) GreenWhiteDarkColors else GreenWhiteLightColors
    }

    val semanticColors = when (preset) {
        ThemePreset.PINK_LIGHT_BLUE ->
            if (effectiveDarkTheme) PinkLightBlueSemanticDark else PinkLightBlueSemanticLight

        ThemePreset.BLACK_YELLOW_RED ->
            if (effectiveDarkTheme) BlackYellowRedSemanticDark else BlackYellowRedSemanticLight

        ThemePreset.GREEN_WHITE ->
            if (effectiveDarkTheme) GreenWhiteSemanticDark else GreenWhiteSemanticLight
    }

    CompositionLocalProvider(
        LocalThemePreset provides preset,
        LocalSemanticColors provides semanticColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
