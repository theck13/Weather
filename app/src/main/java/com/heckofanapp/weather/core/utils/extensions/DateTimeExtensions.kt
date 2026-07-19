package com.heckofanapp.weather.core.utils.extensions

import com.heckofanapp.weather.core.utils.formatters.safeZoneId
import java.time.Instant

object DateTimeExtensions {
    private val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun String.iso8601TimestampToMilliseconds(): Long {
        val cleaned = this.trim().substringBefore("/").trim()

        return java.time.OffsetDateTime
            .parse(cleaned, formatter)
            .toInstant()
            .toEpochMilli()
    }

    fun Long.normalizeToDay(
        zoneId: String,
    ): Long {
        val zone = safeZoneId(
            id = zoneId,
        )

        val localDate = Instant.ofEpochMilli(this)
            .atZone(zone)
            .toLocalDate()

        return localDate
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }

    fun Long.secondsToMilliseconds(): Long {
        return (this * 1000L)
    }
}
