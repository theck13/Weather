package com.pranshulgg.weather_master_app.core.utils.formatters

import java.util.Locale

fun formatLocalizedNumber(
    decimalPlaces: Int = 0,
    locale: Locale = Locale.US,
    number: Double,
): String {
    return "%,.${decimalPlaces}f".format(locale, number)
}

fun String?.toSafeDouble(): Double? = this?.toDoubleOrNull()?.takeIf { it.isFinite() }
