package com.antirot.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.theme.semanticColors

@Composable
fun LimitProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    trackColor: Color = MaterialTheme.semanticColors.timerTrack,
    normalColor: Color = MaterialTheme.semanticColors.timerProgress,
    warningColor: Color = MaterialTheme.semanticColors.warning,
    dangerColor: Color = MaterialTheme.semanticColors.danger,
    warningThreshold: Float = 0.8f,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 400),
        label = "limit_progress_anim",
    )

    val targetColor = when {
        clamped >= 1.0f -> dangerColor
        clamped >= warningThreshold -> warningColor
        else -> normalColor
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 300),
        label = "limit_color_anim",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackColor),
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(shape)
                    .background(animatedColor),
            )
        }
    }
}
