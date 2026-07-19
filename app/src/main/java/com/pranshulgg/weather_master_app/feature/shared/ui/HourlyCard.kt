package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherHourlyDomainSafe
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingDaily
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.shared.components.Header
import kotlin.math.roundToInt

@Composable
fun HourlyCard(
    currentMilli: Long = weather.current.time,
    units: WeatherUnits,
    weather: Weather,
) {
    if (!isWeatherHourlyDomainSafe(weather)) return

    val lazyListState = rememberLazyListState()
    val filteredHourly = findMatchingHourly(
        currentMilli = currentMilli,
        data = weather.hourly,
        source = weather.location.source,
    )
    val preferences = LocalAppPrefs.current
    val is24hr = preferences.timeFormat == "24"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = 16.dp,
                )
                .fillMaxWidth(),
        ) {
            Header(
                icon = R.drawable.ic_schedule_48,
                text = stringResource(R.string.weather_hourly_forecast),
            )

            if (filteredHourly.isEmpty()) {
                NoHourlyDataAvailable()
                return@Column
            }

            LazyRow(
                state = lazyListState,
            ) {
                items(
                    count = filteredHourly.size,
                    key = { "${filteredHourly[it].time}_$it" },
                ) { index ->
                    val item = filteredHourly[index]
                    val temperature = TemperatureUnit.CELSIUS.convert(
                        from = item.temperature,
                        to = units.temperature,
                    )
                    val time = if (is24hr) to24HourTimeString(
                        timeMilli = filteredHourly[index].time,
                        zoneId = weather.location.timezone,
                    ) else to12HourTimeString(
                        timeMilli = filteredHourly[index].time,
                        zoneId = weather.location.timezone,
                    )

                    val matchingDaily = findMatchingDaily(
                        dailyList = weather.daily,
                        targetTimeMilli = item.time,
                        timezone = weather.location.timezone,
                    )

                    if (index == 0) {
                        Gap(
                            horizontal = 8.dp,
                        )
                    }

                    HourlyItem(
                        icon = item.weatherCondition.toIcon(
                            daily = matchingDaily,
                            targetTimeMilli = item.time,
                        ),
                        isNow = index == 0,
                        precipitationProbability = item.precipitationProbability ?: 0,
                        temperature = temperature,
                        time = time,
                    )

                    if (index == filteredHourly.size - 1) {
                        Gap(
                            horizontal = 8.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyItem(
    icon: Int,
    isNow: Boolean,
    precipitationProbability: Int,
    temperature: Double?,
    time: String,
) {
    Column(
        modifier = Modifier
            .height(
                height = 120.dp,
            )
            .padding(
                horizontal = 4.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Gap(
            vertical = 4.dp,
        )

        TemperatureWithShape(
            isNow = isNow,
            temperature = temperature,
        )

        Gap(
            vertical = 4.dp,
        )

        WeatherIconBox(
            icon = icon,
            size = 28.dp,
        )

        Gap(
            vertical = 4.dp,
        )

        Text(
            modifier = Modifier
                .alpha(
                    alpha = if (precipitationProbability > 0) 1.00f else 0.00f,
                )
                .padding(
                    bottom = 4.dp,
                ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            text = "${precipitationProbability}%",
        )

        Gap(
            vertical = 4.dp,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            text = time,
        )
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun TemperatureWithShape(
    isNow: Boolean = false,
    temperature: Double?,
) {
    Surface(
        modifier = Modifier.size(
            size = 40.dp,
        ),
        color = if (isNow) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = MaterialShapes.Cookie4Sided.toShape(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                color = if (isNow) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                text = "${temperature?.roundToInt() ?: "-"}°",
            )
        }
    }
}
