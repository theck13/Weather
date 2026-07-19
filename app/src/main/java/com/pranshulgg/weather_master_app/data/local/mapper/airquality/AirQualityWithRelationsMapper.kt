package com.pranshulgg.weather_master_app.data.local.mapper.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrently
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.data.local.entity.airquality.AirQualityWithRelations

fun AirQualityWithRelations.toDomain(): AirQuality {
    return AirQuality(
        current = AirQualityCurrently(
            carbonMonoxide = this.current?.carbonMonoxide,
            lastUpdatedInMilli = this.current?.lastUpdatedInMilli ?: System.currentTimeMillis(),
            nitrogenDioxide = this.current?.nitrogenDioxide,
            ozone = this.current?.ozone,
            pm10 = this.current?.pm10,
            pm25 = this.current?.pm25,
            sulphurDioxide = this.current?.sulphurDioxide,
            usAqi = this.current?.usAqi,
        ),
        hourly = this.hourly.map { hourly ->
            AirQualityHourly(
                carbonMonoxide = hourly.carbonMonoxide,
                nitrogenDioxide = hourly.nitrogenDioxide,
                ozone = hourly.ozone,
                pm10 = hourly.pm10,
                pm25 = hourly.pm25,
                sulphurDioxide = hourly.sulphurDioxide,
                time = hourly.time,
                usAqi = hourly.usAqi,
            )
        },
    )
}
