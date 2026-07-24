package com.heckofanapp.weather.data.provider

import com.heckofanapp.weather.core.model.sources.SearchSource
import com.heckofanapp.weather.core.network.sources.search.geonames.GeoNamesSearchRepository
import com.heckofanapp.weather.core.network.sources.search.openmeteo.OpenMeteoSearchRepository
import com.heckofanapp.weather.data.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryProvider @Inject constructor(
    private val geoNamesSearchRepository: GeoNamesSearchRepository,
    private val openMeteoSearchRepository: OpenMeteoSearchRepository,
) {
    fun getRepository(
        provider: SearchSource,
    ): SearchRepository {
        return when (provider) {
            SearchSource.GEO_NAMES -> geoNamesSearchRepository
            SearchSource.OPEN_METEO -> openMeteoSearchRepository
        }
    }
}
