package com.heckofanapp.weather.feature.blocks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.Symbol

@Composable
fun NoHourlyDataAvailable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Symbol(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = R.drawable.ic_info_24,
        )

        Gap(
            vertical = 4.dp,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            text = stringResource(R.string.weather_no_data),
        )
    }
}
