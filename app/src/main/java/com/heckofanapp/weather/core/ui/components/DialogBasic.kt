package com.heckofanapp.weather.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun DialogBasic(
    isConfirmButtonDisabled: Boolean = false,
    isDefaultActions: Boolean = true,
    isDismissActionOnly: Boolean = false,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
    show: Boolean,
    textConfirm: String = "Confirm",
    textDismiss: String = "Cancel",
    textTitle: String,
    content: @Composable () -> Unit,
) {
    if (show.not()) return

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
            Column {
                Text(
                    modifier = Modifier.padding(
                        end = 24.dp,
                        start = 24.dp,
                        top = 24.dp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    text = textTitle,
                )

                Gap(
                    vertical = 16.dp,
                )

                content()

                if (isDefaultActions) {
                    Gap(
                        vertical = 16.dp,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 24.dp,
                                end = 24.dp,
                                start = 24.dp,
                            ),
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
                                text = textDismiss,
                            )
                        }

                        if (isDismissActionOnly.not()) {
                            Gap(
                                horizontal = 8.dp,
                            )

                            TextButton(
                                enabled = !isConfirmButtonDisabled,
                                onClick = {
                                    onConfirm()
                                    onDismiss()
                                },
                                shapes = ButtonDefaults.shapes(),
                            ) {
                                Text(
                                    text = textConfirm,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
