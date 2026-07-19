package com.pranshulgg.weather_master_app.core.ui

import androidx.compose.ui.graphics.Color
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexCategory
import com.pranshulgg.weather_master_app.core.ui.theme.Amber800
import com.pranshulgg.weather_master_app.core.ui.theme.Blue400
import com.pranshulgg.weather_master_app.core.ui.theme.Blue600
import com.pranshulgg.weather_master_app.core.ui.theme.Blue800
import com.pranshulgg.weather_master_app.core.ui.theme.BlueLight100
import com.pranshulgg.weather_master_app.core.ui.theme.BlueLight300
import com.pranshulgg.weather_master_app.core.ui.theme.Green400
import com.pranshulgg.weather_master_app.core.ui.theme.Green600
import com.pranshulgg.weather_master_app.core.ui.theme.Grey300
import com.pranshulgg.weather_master_app.core.ui.theme.Orange600
import com.pranshulgg.weather_master_app.core.ui.theme.Pink900
import com.pranshulgg.weather_master_app.core.ui.theme.Purple800
import com.pranshulgg.weather_master_app.core.ui.theme.Purple900
import com.pranshulgg.weather_master_app.core.ui.theme.Red800
import com.pranshulgg.weather_master_app.core.ui.theme.RedAlt700
import com.pranshulgg.weather_master_app.core.ui.theme.Yellow600

private fun barColor(
    fallback: Color,
    thresholds: List<Pair<Double, Color>>,
    value: Double,
): Color = thresholds.firstOrNull { value < it.first }?.second ?: fallback

fun barColorsAirQuality(
    category: AirQualityIndexCategory,
): Color = when (category.severity) {
    0 -> Green400
    1 -> Yellow600
    2 -> Orange600
    3 -> Red800
    4 -> Purple800
    else -> Pink900
}

fun barColorsDewPoint(
    dewPoint: Double,
): Color = barColor(
    fallback = Red800,
    thresholds = listOf(
        0.0 to Blue800,
        10.0 to Blue400,
        15.0 to Green400,
        20.0 to Yellow600,
        24.0 to Orange600,
    ),
    value = dewPoint,
)

fun barColorsHumidity(
    humidity: Double,
): Color = barColor(
    fallback = Blue800,
    thresholds = listOf(
        20.0 to Red800,
        40.0 to Amber800,
        60.0 to Green600,
        80.0 to Blue600,
    ),
    value = humidity,
)

fun barColorsPrecipitation(
    amount: Double,
): Color = barColor(
    fallback = RedAlt700,
    thresholds = listOf(
        0.001 to Grey300,
        1.0 to BlueLight100,
        2.5 to BlueLight300,
        7.5 to Blue600,
        20.0 to Blue800,
        50.0 to Purple800,
    ),
    value = amount,
)

fun barColorsPressure(
    pressure: Double,
): Color = barColor(
    fallback = Red800,
    thresholds = listOf(
        980.0 to Purple900,
        995.0 to Blue800,
        1010.0 to Green600,
        1025.0 to Orange600,
    ),
    value = pressure,
)

fun barColorsVisibility(
    visibilityKm: Double,
): Color = barColor(
    fallback = Blue800,
    thresholds = listOf(
        1.0 to Red800,
        5.0 to Orange600,
        10.0 to Yellow600,
        20.0 to Green600,
    ),
    value = visibilityKm,
)

fun barColorsWindSpeed(
    windSpeed: Double,
): Color = barColor(
    fallback = Purple800,
    thresholds = listOf(
        10.0 to Blue400,
        20.0 to Green400,
        40.0 to Yellow600,
        60.0 to Orange600,
        90.0 to Red800,
    ),
    value = windSpeed,
)
