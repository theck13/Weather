package com.pranshulgg.weather_master_app.feature.main.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.utils.formatters.getLastUpdatedTimeString
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    context: Context,
    isFroggyLayout: Boolean = true,
    units: WeatherUnits,
    weather: Weather,
) {
    if (isFroggyLayout) {
        Column(
            modifier = Modifier.padding(
                end = 16.dp,
                start = 16.dp,
                top = 6.dp,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CurrentWeatherFroggy(
                    context = context,
                    units = units,
                    weather = weather,
                )
            }
        }
    } else {
        CurrentWeatherPixel(
            context = context,
            units = units,
            weather = weather,
        )
    }
}

@Composable
private fun CurrentWeatherFroggy(
    context: Context,
    units: WeatherUnits,
    weather: Weather,
) {
    val current = weather.current
    val currentTemp = TemperatureUnit.CELSIUS.convert(
        from = current.temperature,
        to = units.temperature,
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                text = current.weatherCondition.toLabel(context),
            )

            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.labelLarge,
                text = getLastUpdatedTimeString(
                    context = context,
                    timeMilli = weather.current.lastUpdatedInMilli,
                ),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 64.sp,
                fontWeight = FontWeight.Medium,
                text = "${currentTemp?.roundToInt() ?: "-"}°",
            )

            WeatherIconBox(
                icon = current.weatherCondition.toIcon(
                    daily = weather.daily.firstOrNull(),
                    targetTimeMilli = weather.current.time,
                ),
                size = 42.dp,
            )

            Spacer(
                modifier = Modifier
                    .height(height = 0.dp)
                    .weight(weight = 1.00f),
            )

            FeelsMaxMinLayout(
                isFroggyLayout = true,
                units = units,
                weather = weather,
            )
        }
    }
}

@Composable
fun CurrentWeatherPixel(
    context: Context,
    units: WeatherUnits,
    weather: Weather,
) {
    val current = weather.current
    val currentTemp = TemperatureUnit.CELSIUS.convert(
        from = current.temperature,
        to = units.temperature,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = 16.dp,
                start = 16.dp,
                top = 16.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleLarge,
                text = current.weatherCondition.toLabel(context),
            )

            WeatherIconBox(
                icon = current.weatherCondition.toIcon(
                    daily = weather.daily.firstOrNull(),
                    targetTimeMilli = weather.current.time,
                ),
                size = 32.dp,
            )
        }

        Text(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 128.sp,
            text = "${currentTemp?.roundToInt() ?: "-"}°",
        )

        FeelsMaxMinLayout(
            isFroggyLayout = false,
            units = units,
            weather = weather,
        )
    }
}

@Composable
private fun FeelsMaxMinLayout(
    isFroggyLayout: Boolean,
    units: WeatherUnits,
    weather: Weather,
) {
    val daily = weather.daily.getOrNull(0)
    val temperatureFeels = TemperatureUnit.CELSIUS.convert(
        from = weather.current.feelsLike,
        to = units.temperature,
    )
    val temperatureMaximum = TemperatureUnit.CELSIUS.convert(
        from = daily?.temperatureMax,
        to = units.temperature,
    )
    val temperatureMinimum = TemperatureUnit.CELSIUS.convert(
        from = daily?.temperatureMin,
        to = units.temperature,
    )

    if (isFroggyLayout) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(
                space = 2.dp,
            ),
        ) {
            FeelsMaxMinTemps(
                isFroggyLayout = isFroggyLayout,
                temperatureFeels = temperatureFeels,
                temperatureMaximum = temperatureMaximum,
                temperatureMinimum = temperatureMinimum,
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
            ),
        ) {
            FeelsMaxMinTemps(
                isFroggyLayout = isFroggyLayout,
                temperatureFeels = temperatureFeels,
                temperatureMaximum = temperatureMaximum,
                temperatureMinimum = temperatureMinimum,
            )
        }
    }
}

@Composable
private fun FeelsMaxMinTemps(
    isFroggyLayout: Boolean,
    temperatureFeels: Double?,
    temperatureMaximum: Double?,
    temperatureMinimum: Double?,
) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val style = if (isFroggyLayout) MaterialTheme.typography.labelLarge else  MaterialTheme.typography.titleLarge

    Text(
        color = color,
        style = style,
        text = stringResource(R.string.temp_feels_like, "${temperatureFeels?.roundToInt() ?: "-"}°"),
    )

    Text(
        color = color,
        style = style,
        text = stringResource(R.string.temp_max, "${temperatureMaximum?.roundToInt() ?: "-"}°"),
    )

    Text(
        color = color,
        style = style,
        text = stringResource(R.string.temp_min, "${temperatureMinimum?.roundToInt() ?: "-"}°"),
    )
}
