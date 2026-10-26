package com.antirot.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.antirot.app.ui.theme.spacing

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = MaterialTheme.spacing.minTouchTarget),
        enabled = enabled && !loading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp,
            )
        } else {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = MaterialTheme.spacing.minTouchTarget),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
        ),
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
