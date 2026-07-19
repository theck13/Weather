package com.heckofanapp.weather.feature.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.toIconPair
import com.heckofanapp.weather.core.model.weather.toLabel
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.WeatherIconBox
import com.heckofanapp.weather.core.utils.formatters.toDateString
import kotlin.math.roundToInt

@Composable
fun DailyForecastHeroHeader(
    daily: WeatherDaily,
    location: Location,
    units: WeatherUnits,
) {
    val context = LocalContext.current
    val date = toDateString(
        timeMilli = daily.time,
        zoneId = location.timezone,
    )
    val temperatureMaximum =
        TemperatureUnit.CELSIUS.convert(
            from = daily.temperatureMax,
            to = units.temperature,
        )?.roundToInt() ?: "-"
    val temperatureMinimum =
        TemperatureUnit.CELSIUS.convert(
            from = daily.temperatureMin,
            to = units.temperature,
        )?.roundToInt() ?: "-"

    Column(
        modifier = Modifier.padding(
            end = 16.dp,
            start = 16.dp,
            top = 24.dp,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    text = date,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    text = location.name,
                )
            }

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                text = daily.weatherCondition.toLabel(
                    context = context,
                ),
            )
        }

        Gap(
            vertical = 5.dp,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.W900,
                    style = MaterialTheme.typography.displayLarge,
                    text = "${temperatureMaximum}°",
                )

                Gap(
                    horizontal = 12.dp,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.W900,
                    style = MaterialTheme.typography.displayLarge,
                    text = "${temperatureMinimum}°",
                )
            }

            Gap(
                horizontal = 16.dp,
            )

            WeatherIconBox(
                icons = daily.weatherCondition.toIconPair(
                    targetTimeMilli = System.currentTimeMillis(),
                ),
                size = 64.dp,
            )
        }
    }
}
