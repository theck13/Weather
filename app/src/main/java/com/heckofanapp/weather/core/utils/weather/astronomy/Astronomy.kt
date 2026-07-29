package com.heckofanapp.weather.core.utils.weather.astronomy

import com.heckofanapp.weather.core.model.astro.MoonTimings
import com.heckofanapp.weather.core.model.astro.SunTimings
import com.heckofanapp.weather.core.model.astro.getMoonPhase
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.core.utils.formatters.safeZoneId
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonTimes
import org.shredzone.commons.suncalc.SunTimes
import java.time.Instant
import java.time.LocalDate

/**
 * Number of consecutive days, starting from [date], that share same [MoonPhase]
 * bucket.  Since phase angle is location independent, only date is needed.
 * Count is inclusive of [date], and it is always at least 1.
 */
fun getDaysRemainingInPhase(
    date: LocalDate,
): Int {
    val phase = getMoonPhase(
        MoonIllumination.compute().on(date).execute().phase,
    )

    var days = 0
    var cursor = date

    while (
        getMoonPhase(
            MoonIllumination.compute().on(cursor).execute().phase,
        ) == phase
    ) {
        days++
        cursor = cursor.plusDays(1)
    }

    return days
}

fun getMoonTimings(
    timeMilli: List<Long>,
    zoneId: String,
    latitude: Double,
    longitude: Double,
): List<MoonTimings> {
    return timeMilli.map {
        val date = Instant.ofEpochMilli(it)
            .atZone(
                safeZoneId(
                    id = zoneId,
                )
            )
            .toLocalDate()

        val moonTimes = MoonTimes.compute()
            .on(date)
            .at(latitude, longitude)
            .timezone(
                safeZoneId(
                    id = zoneId,
                )
            )
            .execute()

        val moonIllumination = MoonIllumination.compute().on(date).execute()
        val phaseName = getMoonPhase(moonIllumination.phase)

        MoonTimings(
            daysRemaining = getDaysRemainingInPhase(date),
            illumination = moonIllumination.fraction * 100,
            moonrise = moonTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            moonset = moonTimes.set?.toEpochSecond()?.secondsToMilliseconds(),
            phase = phaseName,
            time = it,
        )
    }
}

fun getSunTimings(
    timeMilli: List<Long>,
    zoneId: String,
    latitude: Double,
    longitude: Double,
): List<SunTimings> {
    return timeMilli.map {
        val date = Instant.ofEpochMilli(it)
            .atZone(
                safeZoneId(
                    id = zoneId,
                )
            )
            .toLocalDate()

        val sunTimes = SunTimes.compute()
            .on(date)
            .fullCycle()
            .timezone(
                safeZoneId(
                    id = zoneId,
                )
            )
            .at(latitude, longitude)
            .execute()

        val civilTwilight = SunTimes.compute()
            .on(date)
            .timezone(
                safeZoneId(
                    id = zoneId,
                )
            )
            .at(latitude, longitude)
            .twilight(SunTimes.Twilight.CIVIL)
            .execute()

        val dawn = civilTwilight.rise
        val dusk = civilTwilight.set

        SunTimings(
            dusk = dusk?.toEpochSecond()?.secondsToMilliseconds(),
            dawn = dawn?.toEpochSecond()?.secondsToMilliseconds(),
            sunrise = sunTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            sunset = sunTimes.set?.toEpochSecond()?.secondsToMilliseconds(),
            time = it,
        )
    }
}
