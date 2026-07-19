package com.pranshulgg.weather_master_app.feature.blocks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic

/**
 * Shared dialog for single scale level (e.g. air or ultraviolet), showing its name as title, its
 * value range, and health-guidance description.  Callers resolve strings from their own level type.
 */
@Composable
fun ScaleDialog(
    description: String,
    onDismiss: () -> Unit,
    range: String,
    title: String,
) {
    DialogBasic(
        isDismissActionOnly = true,
        onDismiss = onDismiss,
        show = true,
        textDismiss = stringResource(R.string.action_ok),
        textTitle = title,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
            ),
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                text = range,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                text = description,
            )
        }
    }
}
