package com.heckofanapp.weather.core.ui

import androidx.compose.ui.graphics.Color
import com.heckofanapp.weather.core.model.weather.air.AirQualityIndexCategory
import com.heckofanapp.weather.core.ui.theme.Amber800
import com.heckofanapp.weather.core.ui.theme.Blue400
import com.heckofanapp.weather.core.ui.theme.Blue600
import com.heckofanapp.weather.core.ui.theme.Blue800
import com.heckofanapp.weather.core.ui.theme.BlueLight100
import com.heckofanapp.weather.core.ui.theme.BlueLight300
import com.heckofanapp.weather.core.ui.theme.Green400
import com.heckofanapp.weather.core.ui.theme.Green600
import com.heckofanapp.weather.core.ui.theme.Orange600
import com.heckofanapp.weather.core.ui.theme.Pink900
import com.heckofanapp.weather.core.ui.theme.Purple800
import com.heckofanapp.weather.core.ui.theme.Purple900
import com.heckofanapp.weather.core.ui.theme.Red800
import com.heckofanapp.weather.core.ui.theme.RedAlt700
import com.heckofanapp.weather.core.ui.theme.Yellow600

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

// Rain intensity colors so hourly bars, scale legend, and home-tile fill share one scale.  Bounds
// are in mm (hourly bar's value); finite bands fill 0 → 4 in (RainBlock fill maximum) and anything
// above maximum is "Extreme" fallback.
fun barColorsRain(
    amountMm: Double,
): Color = barColor(
    fallback = RedAlt700,
    thresholds = listOf(
        5.08 to BlueLight100,   // 0.20 in
        25.4 to BlueLight300,   // 1.00 in
        50.8 to Blue600,        // 2.00 in
        76.2 to Blue800,        // 3.00 in
        101.6 to Purple800,     // 4.00 in (fill maximum)
        // ≥ 4 in fill maximum → RedAlt700 (Extreme)
    ),
    value = amountMm,
)

// Snow intensity colors so hourly bars, scale legend, and home-tile fill share one scale.  Bounds
// are in mm (hourly bar's value); finite bands fill 0 → 24 in (SnowBlock fill maximum) and anything
// above maximum is "Extreme" fallback.
fun barColorsSnow(
    amountMm: Double,
): Color = barColor(
    fallback = RedAlt700,
    thresholds = listOf(
        25.4 to BlueLight100,   //  1.00 in
        76.2 to BlueLight300,   //  3.00 in
        152.4 to Blue600,       //  6.00 in
        304.8 to Blue800,       // 12.00 in
        609.6 to Purple800,     // 24.00 in (fill maximum)
        // ≥ 24 in fill maximum → RedAlt700 (Extreme)
    ),
    value = amountMm,
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
