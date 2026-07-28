package com.heckofanapp.weather.core.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun Tooltip(
    modifier: Modifier = Modifier,
    preferredPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    spacing: Dp = 4.dp,
    tooltipText: String,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = preferredPosition,
            spacingBetweenTooltipAndAnchor = spacing,
        ),
        state = rememberTooltipState(),
        tooltip = {
            PlainTooltip {
                Text(
                    text = tooltipText,
                )
            }
        },
    ) {
        content()
    }
}
