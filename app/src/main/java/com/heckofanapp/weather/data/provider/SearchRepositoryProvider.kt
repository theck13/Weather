package com.heckofanapp.weather.data.provider

import com.heckofanapp.weather.core.model.sources.SearchSource
import com.heckofanapp.weather.core.network.sources.search.geonames.GeoNamesSearchRepository
import com.heckofanapp.weather.core.network.sources.search.openmeteo.OpenMeteoSearchRepository
import com.heckofanapp.weather.data.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryProvider @Inject constructor(
    private val openMeteoSearchRepository: OpenMeteoSearchRepository,
    private val geoNamesSearchRepository: GeoNamesSearchRepository,
) {
    fun getRepository(
        provider: SearchSource,
    ): SearchRepository {
        return when (provider) {
            SearchSource.OPEN_METEO -> openMeteoSearchRepository
            SearchSource.GEO_NAMES -> geoNamesSearchRepository
        }
    }
}
