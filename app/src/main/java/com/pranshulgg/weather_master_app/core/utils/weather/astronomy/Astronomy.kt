package com.pranshulgg.weather_master_app.core.utils.weather.astronomy

import com.pranshulgg.weather_master_app.core.model.astro.MoonTimings
import com.pranshulgg.weather_master_app.core.model.astro.SunTimings
import com.pranshulgg.weather_master_app.core.model.astro.getMoonPhase
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonTimes
import org.shredzone.commons.suncalc.SunTimes
import java.time.Instant

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
            it,
            sunTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            sunTimes.set?.toEpochSecond()?.secondsToMilliseconds(),
            dawn?.toEpochSecond()?.secondsToMilliseconds(),
            dusk?.toEpochSecond()?.secondsToMilliseconds()
        )

    }

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

        val phase = MoonIllumination.compute().on(date).execute().phase
        val phaseName = getMoonPhase(phase)

        MoonTimings(
            time = it,
            moonrise = moonTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            moonset = moonTimes.set?.toEpochSecond()?.secondsToMilliseconds(),
            phase = phaseName,
        )
    }
}
