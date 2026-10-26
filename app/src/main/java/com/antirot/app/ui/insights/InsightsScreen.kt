package com.antirot.app.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.antirot.app.data.local.dao.DailyTotal
import com.antirot.app.ui.components.AppCard
import com.antirot.app.ui.components.EmptyState
import com.antirot.app.ui.components.SectionHeader
import com.antirot.app.ui.components.StatTile
import com.antirot.app.ui.theme.spacing

@Composable
fun InsightsScreen(viewModel: InsightsViewModel) {
    val weeklyTotals by viewModel.weeklyTotals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Spacer(Modifier.height(MaterialTheme.spacing.small))
        SectionHeader(
            title = "Usage Insights",
            subtitle = "7-day trend & mindful statistics",
        )

        if (weeklyTotals.isEmpty()) {
            EmptyState(
                title = "No History Recorded Yet",
                description = "As you use your device and monitored apps, your daily session trends " +
                    "and statistics will appear here.",
                icon = Icons.Default.BarChart,
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.large),
            )
        } else {
            val totalMinutes = weeklyTotals.sumOf { it.totalMs } / 60_000L
            val avgMinutes = totalMinutes / weeklyTotals.size.coerceAtLeast(1)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                StatTile(
                    value = "${totalMinutes / 60}h ${totalMinutes % 60}m",
                    label = "Total 7-day usage",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = "${avgMinutes}m",
                    label = "Daily average",
                    modifier = Modifier.weight(1f),
                )
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Daily Screen Time Trend",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SimpleBarChart(
                        data = weeklyTotals,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))
    }
}

@Composable
private fun SimpleBarChart(data: List<DailyTotal>, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val maxMs = data.maxOfOrNull { it.totalMs }?.coerceAtLeast(60_000L) ?: 60_000L
        val barCount = data.size.coerceAtLeast(7)
        val spacingPx = 16.dp.toPx()
        val totalSpacing = spacingPx * (barCount + 1)
        val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(8.dp.toPx())

        data.reversed().forEachIndexed { index, dailyTotal ->
            val left = spacingPx + index * (barWidth + spacingPx)
            val ratio = dailyTotal.totalMs.toFloat() / maxMs.toFloat()
            val availableHeight = size.height - 20.dp.toPx()
            val barHeight = (ratio * availableHeight).coerceAtLeast(4.dp.toPx())
            val top = size.height - barHeight

            // Background track
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(left, 0f),
                size = Size(barWidth, size.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            )

            // Progress bar
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            )
        }
    }
}
