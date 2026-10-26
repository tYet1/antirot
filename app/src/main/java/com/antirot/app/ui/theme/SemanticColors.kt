package com.antirot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens for status, limits, and timer progress across all themes.
 */
data class SemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val danger: Color,
    val onDanger: Color,
    val dangerContainer: Color,
    val onDangerContainer: Color,
    val timerTrack: Color,
    val timerProgress: Color,
    val timerWarning: Color,
)

val LocalSemanticColors = staticCompositionLocalOf {
    SemanticColors(
        success = Color(0xFF2E7D32),
        onSuccess = Color(0xFFFFFFFF),
        successContainer = Color(0xFFA5D6A7),
        onSuccessContainer = Color(0xFF003300),
        warning = Color(0xFFE65100),
        onWarning = Color(0xFFFFFFFF),
        warningContainer = Color(0xFFFFCC80),
        onWarningContainer = Color(0xFF4E1D00),
        danger = Color(0xFFC62828),
        onDanger = Color(0xFFFFFFFF),
        dangerContainer = Color(0xFFFFCDD2),
        onDangerContainer = Color(0xFF4A0004),
        timerTrack = Color(0x33000000),
        timerProgress = Color(0xFF007A99),
        timerWarning = Color(0xFFC62828),
    )
}

val MaterialTheme.semanticColors: SemanticColors
    @Composable
    @ReadOnlyComposable
    get() = LocalSemanticColors.current
