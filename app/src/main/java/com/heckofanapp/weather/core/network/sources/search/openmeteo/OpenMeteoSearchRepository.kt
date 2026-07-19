package com.heckofanapp.weather.core.network.sources.search.openmeteo

import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.data.local.mapper.locations.toDomain
import com.heckofanapp.weather.data.repository.SearchRepository
import javax.inject.Inject

class OpenMeteoSearchRepository @Inject constructor(
    private val api: OpenMeteoSearchApi,
) : SearchRepository {
    override suspend fun search(
        query: String,
    ): List<Location> {
        val response = api.search(query)
        val body = response.body() ?: return emptyList()
        val domain = body.toDomain()

        return domain
    }
}
