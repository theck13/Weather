package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIconPair
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.navigation.NavigationRoutes
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.toWeekdayString
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherDailyDomainSafe
import com.pranshulgg.weather_master_app.feature.shared.components.Header
import kotlin.math.roundToInt

@Composable
fun DailyCard(
    navController: NavController,
    units: WeatherUnits,
    weather: Weather,
) {
    if (isWeatherDailyDomainSafe(weather).not()) return

    val daily = weather.daily

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = 16.dp,
                )
                .fillMaxWidth(),
        ) {
            Header(
                icon = R.drawable.ic_date_range_24,
                text = stringResource(R.string.weather_daily_forecast),
            )

            Gap(
                vertical = 14.dp,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 6.dp,
                ),
            ) {
                items(
                    count = daily.size,
                    key = { "${daily[it].time}_$it" },
                ) { index ->
                    val item = daily[index]
                    val weekDay = toWeekdayString(
                        timeMilli = item.time,
                        zoneId = weather.location.timezone,
                    )

                    if (index == 0) {
                        Gap(
                            horizontal = 16.dp,
                        )
                    }

                    DailyItem(
                        weekDay,
                        item.temperatureMax,
                        item.temperatureMin,
                        item.weatherCondition.toIconPair(
                            targetTimeMilli = System.currentTimeMillis(),
                        ),
                        item.precipitationProbabilityMax,
                        units,
                        onDailyItemClick = {
                            navController.navigate(
                                NavigationRoutes.daily(
                                    index,
                                    weather.location.id
                                )
                            )
                        }
                    )

                    if (index == daily.size - 1) {
                        Gap(
                            horizontal = 16.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyItem(
    weekday: String,
    maxTemp: Double?,
    minTemp: Double?,
    icons: Pair<Int, Int?>,
    precipitationProbability: Int?,
    units: WeatherUnits,
    onDailyItemClick: () -> Unit
) {
    val maxTemp = TemperatureUnit.CELSIUS.convert(
        from = maxTemp,
        to = units.temperature,
    )?.roundToInt() ?: "-"
    val minTemp = TemperatureUnit.CELSIUS.convert(
        from = minTemp,
        to = units.temperature,
    )?.roundToInt() ?: "-"

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = onDailyItemClick,
        shape = CircleShape,
    ) {
        Column(
            Modifier
                .height(
                    height = 210.dp,
                )
                .padding(
                    vertical = 24.dp,
                )
                .width(
                    width = 65.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    text = "${maxTemp}°",
                )
                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    text = "${minTemp}°",
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WeatherIconBox(
                    icons = icons,
                    size = 38.dp,
                )

                Gap(
                    vertical = 8.dp,
                )

                Text(
                    modifier = Modifier.alpha(
                        alpha = if (precipitationProbability == null) 0.00f else 1.00f,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    text = "${precipitationProbability}%",
                )

                Gap(
                    vertical = 4.dp,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    text = weekday,
                )
            }
        }
    }
}
