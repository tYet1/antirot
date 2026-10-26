package com.antirot.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.theme.semanticColors

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 10.dp,
    trackColor: Color = MaterialTheme.semanticColors.timerTrack,
    progressColor: Color = MaterialTheme.semanticColors.timerProgress,
    content: (@Composable () -> Unit)? = null,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 500),
        label = "progress_ring_anim",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.minDimension - strokePx
            val topLeftOffset = androidx.compose.ui.geometry.Offset(strokePx / 2f, strokePx / 2f)
            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Progress Arc
            if (animatedProgress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeftOffset,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
            }
        }

        if (content != null) {
            content()
        }
    }
}
