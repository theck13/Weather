package com.heckofanapp.weather.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.components.Symbol

@Composable
fun SwitchTile(
    checked: Boolean,
    description: String? = null,
    enabled: Boolean = true,
    headline: String,
    itemBgColor: Color,
    leading: @Composable (() -> Unit)? = null,
    onCheckedChange: (Boolean) -> Unit,
    shapes: RoundedCornerShape,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier =
                if (enabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else {
                    Modifier
                },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            leadingContent = leading,
            headlineContent = {
                Text(
                    text = headline,
                )
            },
            supportingContent = {
                description?.let{
                    Text(
                        text = description,
                    )
                }
            },
            trailingContent = {
                Switch(
                    enabled = enabled,
                    checked = checked,
                    onCheckedChange = { change ->
                        if (checked != change) {
                            onCheckedChange(change)
                        }
                    },
                    thumbContent = if (checked) {
                        {
                            Symbol(
                                color = MaterialTheme.colorScheme.primary,
                                icon = R.drawable.ic_check_24,
                                size = SwitchDefaults.IconSize,
                            )
                        }
                    } else {
                        {
                            Symbol(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                icon = R.drawable.ic_close_24,
                                size = SwitchDefaults.IconSize,
                            )
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun SingleSwitchTile(
    checked: Boolean,
    description: String? = null,
    enabled: Boolean = true,
    headline: String,
    itemBgColor: Color,
    leading: @Composable (() -> Unit)? = null,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            size = 50.dp,
        ),
    ) {
        ListItem(
            modifier =
                if (enabled) {
                    Modifier
                        .clickable {
                            onCheckedChange(checked.not())
                        }
                        .padding(
                            start = 10.dp,
                        )
                } else {
                    Modifier.padding(
                        start = 10.dp,
                    )
                },
            leadingContent = leading,
            headlineContent = {
                Text(
                    text = headline,
                )
            },
            supportingContent = {
                description?.let{
                    Text(
                        text = description,
                    )
                }
            },
            trailingContent = {
                Switch(
                    enabled = enabled,
                    checked = checked,
                    onCheckedChange = { change ->
                        if (checked != change) {
                            onCheckedChange(change)
                        }
                    },
                    thumbContent = if (checked) {
                        {
                            Symbol(
                                color = MaterialTheme.colorScheme.primary,
                                icon = R.drawable.ic_check_24,
                                size = SwitchDefaults.IconSize,
                            )
                        }
                    } else {
                        {
                            Symbol(
                                color = itemBgColor,
                                icon = R.drawable.ic_close_24,
                                size = SwitchDefaults.IconSize,
                            )
                        }
                    }
                )
            }
        )
    }
}
