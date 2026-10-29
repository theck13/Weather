package com.heckofanapp.weather.core.utils.weather.astronomy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class AstronomyTest {
    private val latitude = 39.74
    private val longitude = -104.99
    private val zoneId = "America/Denver"

    /**
     * Days in future, so first day is always after today.  Matches sources that drop today near
     * end of day, which previously prepended extra day and shifted every result by one.
     */
    private val days = (0L..6L).map {
        LocalDate.of(2099, 1, 1)
            .plusDays(it)
            .atStartOfDay(ZoneId.of(zoneId))
            .toInstant()
            .toEpochMilli()
    }

    @Test
    fun moonTimingsEmptyForEmptyDays() {
        assertTrue(
            getMoonTimings(
                latitude = latitude,
                longitude = longitude,
                timeMilli = emptyList(),
                zoneId = zoneId,
            ).isEmpty()
        )
    }

    @Test
    fun moonTimingsOnePerDayInSameOrder() {
        val moonTimings = getMoonTimings(
            latitude = latitude,
            longitude = longitude,
            timeMilli = days,
            zoneId = zoneId,
        )

        assertEquals(days.size, moonTimings.size)
        assertEquals(days, moonTimings.map { it.time })
    }

    @Test
    fun moonTimingsMatchEachDayComputedAlone() {
        val moonTimings = getMoonTimings(
            latitude = latitude,
            longitude = longitude,
            timeMilli = days,
            zoneId = zoneId,
        )

        days.forEachIndexed { index, day ->
            assertEquals(
                getMoonTimings(
                    latitude = latitude,
                    longitude = longitude,
                    timeMilli = listOf(day),
                    zoneId = zoneId,
                ).single(),
                moonTimings[index],
            )
        }
    }
}
