package com.heckofanapp.weather.data.local.mapper.weather

import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.data.local.entity.location.WeatherLocationEntity
import com.heckofanapp.weather.data.local.entity.weather.WeatherWithRelations
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherWithRelationsMapperTest {
    @Test
    fun domainDailyEmptyForLocationWithoutDaily() {
        val weather = WeatherWithRelations(
            current = null,
            daily = emptyList(),
            hourly = emptyList(),
            location = WeatherLocationEntity(
                country = "United States",
                id = "denver",
                lat = 39.74,
                lon = -104.99,
                name = "Denver",
                source = WeatherSource.ACCU,
                timezone = "America/Denver",
            ),
        ).toDomain()

        assertTrue(weather.daily.isEmpty())
    }
}
