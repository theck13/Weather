package com.heckofanapp.weather.feature.shared.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.utils.weather.computing.summary.computeDaySummary
import com.heckofanapp.weather.feature.shared.components.Header

@Composable
fun SummaryCard(
    context: Context,
    dailyIndex: Int = 0,
    units: WeatherUnits,
    weather: Weather,
) {
    val summary = computeDaySummary(
        context = context,
        dailyIndex = dailyIndex,
        units = units,
        weather = weather,
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp,
                ),
        ) {
            Header(
                icon = R.drawable.ic_article_24,
                text = stringResource(R.string.setting_day_summary),
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                text = summary,
            )
        }
    }
}
