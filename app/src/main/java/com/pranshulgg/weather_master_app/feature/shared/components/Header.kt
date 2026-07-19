package com.pranshulgg.weather_master_app.feature.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.ui.components.Symbol

@Composable
fun Header(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    icon: Int,
    padding: PaddingValues = PaddingValues(
        end = 16.dp,
        start = 16.dp,
        top = 16.dp,
    ),
    text: String,
) {
    Row(
        modifier = modifier.padding(
            paddingValues = padding,
        ),
        horizontalArrangement = Arrangement.spacedBy(
            alignment = Alignment.CenterHorizontally,
            space = 5.dp,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Symbol(
            color = color,
            icon = icon,
        )

        Text(
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            text = text,
        )
    }
}
