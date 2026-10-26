package com.antirot.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.antirot.app.ui.theme.spacing

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingBadge: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (trailingBadge != null) {
                    trailingBadge()
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = valueColor,
            )

            if (subtext != null) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
