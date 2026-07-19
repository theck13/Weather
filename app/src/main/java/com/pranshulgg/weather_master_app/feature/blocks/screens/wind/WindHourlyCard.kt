package com.pranshulgg.weather_master_app.feature.blocks.screens.wind

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.barColorsWindSpeed
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.feature.shared.components.Header
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun WindHourlyCard(
    context: Context,
    data: List<WeatherHourly>,
    unit: WindUnit,
    zoneId: String,
) {
    val maximum = data.maxOf { it.windSpeed!! }
    val minimum = data.minOf { it.windSpeed!! }
    val formatter: (Double) -> Double? = {
        WindUnit.KPH.convert(
            from = it,
            to = unit,
        )
    }
    val preferences = LocalAppPrefs.current
    val time: (Long) -> String = {
        if (preferences.timeFormat == "24") to24HourTimeString(
            timeMilli = it,
            zoneId = zoneId,
        ) else to12HourTimeString(
            timeMilli = it,
            zoneId = zoneId,
        )
    }
    Surface(
        modifier = Modifier.padding(
            horizontal = 16.dp,
        ),
        color = MaterialTheme.colorScheme.surfaceBright,
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
                icon = R.drawable.ic_schedule_48,
                text = "${stringResource(R.string.weather_hourly_forecast)} (${
                    unit.toName(
                        context = context,
                        inShort = true,
                    )
                })",
            )

            LazyRow(
                modifier = Modifier.height(
                    height = 200.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 6.dp,
                ),
                verticalAlignment = Alignment.Bottom,
            ) {
                items(
                    count = data.size,
                    key = { "${data[it].time}_$it" },
                ) { index ->
                    val item = data[index]
                    val percentage = ((item.windSpeed!!.minus(minimum)).div((maximum - minimum))).times(100)
                    val barColor = barColorsWindSpeed(item.windSpeed)
                    val barHeight =
                        if (percentage.isNaN().not()) {
                            max((percentage.div(100)).times(140).roundToInt(), 48)
                        } else {
                            48
                        }

                    if (index == 0) {
                        Gap(
                            horizontal = 16.dp,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Surface(
                                modifier = Modifier
                                    .height(
                                        height = 140.dp,
                                    )
                                    .width(
                                        width = 18.dp,
                                    ),
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape,
                            ) {}

                            Surface(
                                Modifier
                                    .height(
                                        height = barHeight.dp,
                                    )
                                    .width(
                                        width = 38.dp,
                                    ),
                                color = barColor,
                                shape = CircleShape,
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(
                                            top = 6.dp,
                                        ),
                                    contentAlignment = Alignment.TopCenter,
                                ) {
                                    if (item.windDirection != null) {
                                        Image(
                                            modifier = Modifier
                                                .rotate(
                                                    degrees = WindDirection.toDegrees(item.windDirection)?.toFloat() ?: 0f,
                                                )
                                                .size(
                                                    size = 24.dp,
                                                ),
                                            contentDescription = "",
                                            colorFilter = ColorFilter.tint(
                                                color = MaterialTheme.colorScheme.surface,
                                            ),
                                            painter = painterResource(R.drawable.il_wind_arrow),
                                        )
                                    }
                                }
                            }
                        }

                        Gap(
                            vertical = 5.dp,
                        )

                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            text = "${formatter(item.windSpeed)?.roundToInt()}",
                        )

                        Text(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge,
                            text = time(item.time),
                        )
                    }

                    if (index == data.size - 1) {
                        Gap(
                            horizontal = 16.dp,
                        )
                    }
                }
            }
        }
    }
}
