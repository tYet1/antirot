package com.antirot.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.theme.spacing

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        ) {
            Box(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                content()
            }
        }
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = elevation,
            shadowElevation = elevation,
            border = border,
        ) {
            Box(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                content()
            }
        }
    }
}
