package com.heckofanapp.weather.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.heckofanapp.weather.core.ui.theme.ShapeRadius

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun TextAlertDialog(
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    title: String,
) {
    if (!show) return

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
    ) {
        Surface(
            modifier = Modifier
                .heightIn(
                    max = 500.dp,
                )
                .width(
                    width = 300.dp,
                ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(
                size = ShapeRadius.ExtraLarge,
            ),
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(
                    all = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                )
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    text = title,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    text = message,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelLarge,
                            text = dismissText,
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(
                            width = 8.dp,
                        ),
                    )

                    TextButton(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelLarge,
                            text = confirmText,
                        )
                    }
                }
            }
        }
    }
}
