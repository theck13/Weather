package com.heckofanapp.weather.feature.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.utils.formatters.toWeekdayString
import kotlin.math.roundToInt

@Composable
fun DailyDaysHeader(
    onSelect: (Int) -> Unit,
    selectedIndex: Int,
    units: WeatherUnits,
    weather: Weather,
) {
    val motionScheme = MotionScheme.expressive()
    val scrollState = rememberLazyListState()
    val weatherDaily = weather.daily

    LaunchedEffect(weather) {
        scrollState.animateScrollToItem(
            index = selectedIndex,
            scrollOffset = -16,
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 6.dp,
        ),
        state = scrollState,
    ) {
        itemsIndexed(
            items = weatherDaily,
            key = { _, weatherItem -> weatherItem.time }) { index, it ->
            val day = toWeekdayString(
                timeMilli = it.time,
                zoneId = weather.location.timezone,
            )
            val temperatureMaximum =
                TemperatureUnit.CELSIUS.convert(
                    from = it.temperatureMax,
                    to = units.temperature,
                )?.roundToInt()
                    ?: "-"
            val temperatureMinimum =
                TemperatureUnit.CELSIUS.convert(
                    from = it.temperatureMin,
                    to = units.temperature,
                )?.roundToInt()
                    ?: "-"

            if (index == 0) {
                Gap(
                    horizontal = 16.dp,
                )
            }

            SelectableDayItem(
                conditions = it.weatherCondition,
                isSelected = index == selectedIndex,
                motionScheme = motionScheme,
                onSelect = {
                    onSelect(index)
                },
                temperatureMaximum = temperatureMaximum.toString(),
                temperatureMinimum = temperatureMinimum.toString(),
                weekday = day,
            )

            if (index == weatherDaily.size - 1) {
                Gap(
                    horizontal = 16.dp,
                )
            }
        }
    }
}
