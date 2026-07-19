package com.heckofanapp.weather.feature.blocks.screens.air

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.airquality.AirQuality
import com.heckofanapp.weather.core.model.domain.airquality.AirQualityHourly
import com.heckofanapp.weather.core.model.weather.air.AirQualityIndexStandard
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.barColorsAirQuality
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.utils.formatters.formatLocalizedNumber
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import com.heckofanapp.weather.core.utils.locale.getCurrentAppLocale
import com.heckofanapp.weather.feature.shared.components.Header
import kotlin.math.max

@Composable
fun AirHourlyCard(
    airQuality: AirQuality,
    data: List<AirQualityHourly>,
    standard: AirQualityIndexStandard,
    zoneId: String,
) {
    val aqis = data.map { airQuality.getAqi(it, standard) }
    val maximum = aqis.max()
    val minimum = aqis.min()
    val preferences = LocalAppPrefs.current
    val time: (Long) -> String = {
        if (preferences.timeFormat == "24") {
            to24HourTimeString(
                timeMilli = it,
                zoneId = zoneId,
            )
        } else {
            to12HourTimeString(
                timeMilli = it,
                zoneId = zoneId,
            )
        }
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
                text = stringResource(R.string.weather_hourly_forecast),
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
                    val aqi = aqis[index]
                    val item = data[index]
                    val percentage = ((aqi.minus(minimum)).toDouble().div((maximum - minimum))).times(100)

                    val barColor = barColorsAirQuality(
                        category = airQuality.getAqiCategory(aqi, standard),
                    )
                    val barHeight =
                        if (percentage.isNaN().not()) {
                            max((percentage.div(100.0)).times(140), 5.0)
                        } else {
                            5.0
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
                            ) {}
                        }

                        Gap(
                            vertical = 5.dp,
                        )

                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            text = formatLocalizedNumber(
                                decimalPlaces = 0,
                                locale = getCurrentAppLocale(),
                                number = aqi.toDouble(),
                            ),
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
