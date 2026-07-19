package com.pranshulgg.weather_master_app.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun DialogTextFieldTile(
    headline: String,
    description: String? = null,
    initialText: String = "",
    onTextSubmitted: (String) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    placeholder: String,
    placeholderTextField: String,
    shapes: RoundedCornerShape,
    itemBgColor: Color
) {
    var showDialog by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(initialText) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable { showDialog = true },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            headlineContent = {
                Text(
                    text = headline,
                )
            },
            leadingContent = leading,
            supportingContent = {
                description?.let {
                    Text(
                        text = description,
                    )
                } ?: run {
                    if (textFieldValue.isNotEmpty()) {
                        Text(
                            color = MaterialTheme.colorScheme.tertiary,
                            text = textFieldValue,
                        )
                    } else {
                        Text(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = placeholder,
                        )
                    }
                }
            },
        )
    }

    if (showDialog) {
        AlertDialog(
            confirmButton = {
                TextButton(
                    onClick = {
                        onTextSubmitted(textFieldValue)
                        showDialog = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(R.string.action_save),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
            onDismissRequest = {
                showDialog = false
            },
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp,
                        ),
                    onValueChange = {
                        textFieldValue = it
                    },
                    placeholder = {
                        Text(
                            text = placeholderTextField,
                        )
                    },
                    value = textFieldValue,
                )
            },
            title = {
                Text(
                    text = headline,
                )
            }
        )
    }
}
