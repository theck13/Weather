package com.heckofanapp.weather.data.local.mapper.airquality

import com.heckofanapp.weather.core.model.domain.airquality.AirQuality
import com.heckofanapp.weather.core.model.domain.airquality.AirQualityCurrently
import com.heckofanapp.weather.core.model.domain.airquality.AirQualityHourly
import com.heckofanapp.weather.core.network.sources.airquality.openmeteo.OpenMeteoAqiDto
import com.heckofanapp.weather.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.heckofanapp.weather.data.local.entity.airquality.CurrentAirQualityEntity
import com.heckofanapp.weather.data.local.entity.airquality.HourlyAirQualityEntity

fun OpenMeteoAqiDto.toDomain(): AirQuality =
    AirQuality(
        current = AirQualityCurrently(
            carbonMonoxide = this.current.carbonMonoxide,
            lastUpdatedInMilli = System.currentTimeMillis(),
            nitrogenDioxide = this.current.nitrogenDioxide,
            ozone = this.current.ozone,
            pm10 = this.current.pm10,
            pm25 = this.current.pm25,
            sulphurDioxide = this.current.sulphurDioxide,
            usAqi = this.current.usAqi,
        ),
        hourly = this.hourly?.let { hourly ->
            List(hourly.time.size) {
                AirQualityHourly(
                    carbonMonoxide = hourly.carbonMonoxide.getOrNull(it),
                    nitrogenDioxide = hourly.nitrogenDioxide.getOrNull(it),
                    ozone = hourly.ozone.getOrNull(it),
                    pm10 = hourly.pm10.getOrNull(it),
                    pm25 = hourly.pm25.getOrNull(it),
                    sulphurDioxide = hourly.sulphurDioxide.getOrNull(it),
                    time = hourly.time[it].secondsToMilliseconds(), // Open-Meteo returns in seconds.
                    usAqi = hourly.usAqi.getOrNull(it),
                )
            }
        } ?: emptyList(),
    )

fun AirQualityCurrently.toEntity(
    locationId: String,
): CurrentAirQualityEntity =
    CurrentAirQualityEntity(
        locationId = locationId,
        usAqi = usAqi,
        pm10 = pm10,
        pm25 = pm25,
        carbonMonoxide = carbonMonoxide,
        nitrogenDioxide = nitrogenDioxide,
        sulphurDioxide = sulphurDioxide,
        ozone = ozone,
        lastUpdatedInMilli = lastUpdatedInMilli
    )

fun List<AirQualityHourly>.toHourlyEntity(
    locationId: String,
): List<HourlyAirQualityEntity> =
    map { hourly ->
        HourlyAirQualityEntity(
            carbonMonoxide = hourly.carbonMonoxide,
            locationId = locationId,
            nitrogenDioxide = hourly.nitrogenDioxide,
            ozone = hourly.ozone,
            pm10 = hourly.pm10,
            pm25 = hourly.pm25,
            sulphurDioxide = hourly.sulphurDioxide,
            time = hourly.time,
            usAqi = hourly.usAqi,
        )
    }
