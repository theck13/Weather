package com.heckofanapp.weather.core.di.search

import com.heckofanapp.weather.core.network.sources.search.geonames.GeoNamesSearchApi
import com.heckofanapp.weather.core.network.sources.search.geonames.GeoNamesSearchRepository
import com.heckofanapp.weather.core.network.sources.search.geonames.GeoNamesTimezoneRepository
import com.heckofanapp.weather.core.network.sources.search.geonames.timezone.GeoNamesTimezoneApi
import com.heckofanapp.weather.core.network.sources.search.openmeteo.OpenMeteoSearchApi
import com.heckofanapp.weather.core.network.sources.search.openmeteo.OpenMeteoSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class,
)
object SearchRepositoryModule {
    @Provides
    @Singleton
    fun provideGeoNamesSearchRepository(
        api: GeoNamesSearchApi,
    ): GeoNamesSearchRepository = GeoNamesSearchRepository(
        api = api,
    )

    @Provides
    @Singleton
    fun provideGeoNamesTimezoneRepository(
        api: GeoNamesTimezoneApi,
    ): GeoNamesTimezoneRepository = GeoNamesTimezoneRepository(
        api = api,
    )

    @Provides
    @Singleton
    fun provideOpenMeteoSearchRepository(
        api: OpenMeteoSearchApi,
    ): OpenMeteoSearchRepository = OpenMeteoSearchRepository(
        api = api,
    )
}
