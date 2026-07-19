package com.pranshulgg.weather_master_app.data.local.mapper.locations

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.json.GeoNamesSearchJson
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.json.OpenMeteoSearchJson
import com.pranshulgg.weather_master_app.core.utils.ids.UuidGenerator
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

fun OpenMeteoSearchJson.toDomain(): List<Location> {
    if (results.isNullOrEmpty()) {
        return emptyList()
    }

    val filtered = results.filter { it.country != null }

    return List(filtered.size) {
        Location(
            country = filtered[it].country!!,
            countryCode = filtered[it].countryCode,
            id = UuidGenerator.generateId(),
            isDefault = false,
            latitude = filtered[it].latitude,
            longitude = filtered[it].longitude,
            name = filtered[it].name,
            state = filtered[it].state ?: filtered[it].state2 ?: "",
            timezone = filtered[it].timezone,
        )
    }
}

fun GeoNamesSearchJson.toDomain(): List<Location> {
    if (geonames.isNullOrEmpty()) {
        return emptyList()
    }

    val filtered = geonames.filter { it.countryName != null }

    return List(filtered.size) {
        Location(
            country = filtered[it].countryName!!,
            countryCode = filtered[it].countryCode,
            id = UuidGenerator.generateId(),
            isDefault = false,
            latitude = filtered[it].latitude,
            longitude = filtered[it].longitude,
            name = filtered[it].name,
            state = filtered[it].state ?: "",
            timezone = "",
        )
    }
}

fun Location.toEntity(): WeatherLocationEntity =
    WeatherLocationEntity(
        country = country,
        countryCode = countryCode,
        id = id,
        isDefault = isDefault,
        isDeviceLocation = isDeviceLocation,
        isFavorite = false,
        isPinned = false,
        lat = latitude,
        lon = longitude,
        name = name,
        source = source,
        state = state,
        timezone = timezone,
    )

fun List<WeatherLocationEntity>.toDomain(): List<Location> =
    map { item ->
        item.toDomain()
    }

fun WeatherLocationEntity.toDomain(): Location =
    Location(
        country = country,
        countryCode = countryCode,
        id = id,
        isDefault = isDefault,
        isDeviceLocation = isDeviceLocation,
        isFavorite = isFavorite,
        isPinned = isPinned,
        latitude = lat,
        longitude = lon,
        name = name,
        source = source,
        state = state ?: "",
        timezone = timezone,
    )
