package com.pranshulgg.weather_master_app.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius

data class DialogOption<T>(
    val label: String,
    val value: T,
)

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun <T> DialogOptionTile(
    description: String? = null,
    dialogTitle: String? = null,
    headline: String,
    itemBackgroundColor: Color,
    leading: @Composable (() -> Unit)? = null,
    onOptionChange: (T) -> Unit = {},
    onOptionSelected: (T) -> Unit,
    options: List<DialogOption<T>>,
    selectedOption: T?,
    shapes: RoundedCornerShape,
) {
    val selectedLabel = options.find { it.value == selectedOption }?.label
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable {
                showDialog = true
            },
            colors = ListItemDefaults.colors(
                containerColor = itemBackgroundColor,
            ),
            headlineContent = {
                Text(
                    text = headline,
                )
            },
            leadingContent = leading,
            supportingContent = {
                if (description != null) Text(
                    description,
                )
                else selectedLabel?.let {
                    Text(
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium,
                        text = selectedLabel,
                    )
                }
            },
        )
    }

    if (showDialog) {
        val listState = rememberLazyListState()
        val originalSelection = remember { selectedOption }
        var tempSelection by remember { mutableStateOf(selectedOption) }

        fun revert() {
            originalSelection?.let { onOptionChange(it) }
            showDialog = false
        }

        val showTopDivider by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
            }
        }

        val showBottomDivider by remember {
            derivedStateOf {
                val info = listState.layoutInfo
                val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
                val total = info.totalItemsCount
                total > 0 && lastVisible < total - 1
            }
        }

        Dialog(
            onDismissRequest = {
                revert()
            }
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
                shadowElevation = 6.dp,
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraLarge,
                ),
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
                        text = dialogTitle ?: headline,
                    )

                    Spacer(
                        modifier = Modifier.height(
                            height = 16.dp,
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(
                                fill = false,
                                weight = 1.0f,
                            ),
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            state = listState,
                        ) {
                            items(
                                items = options,
                            ) { option ->
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            tempSelection = option.value
                                            onOptionChange(option.value)
                                        }
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Spacer(
                                        modifier = Modifier.width(
                                            width = 16.dp,
                                        ),
                                    )

                                    RadioButton(
                                        onClick = {
                                            tempSelection = option.value
                                            onOptionChange(option.value)
                                        },
                                        selected = option.value == tempSelection,
                                    )

                                    Spacer(
                                        modifier = Modifier.width(
                                            width = 8.dp,
                                        ),
                                    )

                                    Text(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge,
                                        text = option.label,
                                    )

                                    Spacer(
                                        modifier = Modifier.width(
                                            width = 16.dp,
                                        ),
                                    )
                                }
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            modifier = Modifier.align(
                                alignment = Alignment.TopCenter,
                            ),
                            visible = showTopDivider,
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            modifier = Modifier.align(
                                alignment = Alignment.BottomCenter,
                            ),
                            visible = showBottomDivider,
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(
                            height = 16.dp,
                        ),
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
                                revert()
                            },
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                text = stringResource(R.string.action_cancel),
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(
                                width = 8.dp,
                            ),
                        )

                        TextButton(
                            onClick = {
                                tempSelection?.let { onOptionSelected(it) }
                                showDialog = false
                            },
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                text = stringResource(R.string.action_save),
                            )
                        }
                    }
                }
            }
        }
    }
}
