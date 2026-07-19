package com.pranshulgg.weather_master_app.core.utils.formatters

import android.content.Context
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.zone.ZoneRulesException
import java.util.concurrent.TimeUnit

fun getLastUpdatedTimeString(
    context: Context,
    timeMilli: Long,
): String {
    val ageMillis = System.currentTimeMillis() - timeMilli
    val days = TimeUnit.MILLISECONDS.toDays(ageMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(ageMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ageMillis)

    val lastUpdated = when {
        seconds < 60 -> context.getString(R.string.time_just_now)

        minutes < 60 -> context.resources.getQuantityString(
            R.plurals.time_minutes_ago,
            minutes.toInt(),
            minutes
        )

        hours < 24 -> context.resources.getQuantityString(
            R.plurals.time_hours_ago,
            hours.toInt(),
            hours
        )

        else -> context.resources.getQuantityString(
            R.plurals.time_days_ago,
            days.toInt(),
            days
        )
    }

    return lastUpdated
}

fun safeZoneId(
    id: String,
): ZoneId =
    try {
        ZoneId.of(id)
    } catch (e: ZoneRulesException) {
        ZoneId.systemDefault()
    }

fun to12HourTimeString(
    pattern: String = "ha",
    timeMilli: Long,
    zoneId: String,
): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, getCurrentAppLocale()).withZone(safeZoneId(zoneId))
    val instant = Instant.ofEpochMilli(timeMilli)
    return formatter.format(instant)
}

fun to24HourTimeString(
    pattern: String = "HH:mm",
    timeMilli: Long,
    zoneId: String,
): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, getCurrentAppLocale()).withZone(safeZoneId(zoneId))
    val instant = Instant.ofEpochMilli(timeMilli)
    return formatter.format(instant)
}

fun toDateString(
    pattern: String = "dd MMMM",
    timeMilli: Long,
    zoneId: String,
): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, getCurrentAppLocale())
    val instant = Instant.ofEpochMilli(timeMilli)
    val zonedDateTime = instant.atZone(safeZoneId(zoneId))
    return formatter.format(zonedDateTime)
}

fun toWeekdayString(
    timeMilli: Long,
    zoneId: String,
): String {
    val formatter = DateTimeFormatter.ofPattern("EEE", getCurrentAppLocale())
    val instant = Instant.ofEpochMilli(timeMilli)
    val zonedDateTime = instant.atZone(safeZoneId(zoneId))
    return formatter.format(zonedDateTime)
}
