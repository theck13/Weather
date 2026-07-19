package com.heckofanapp.weather.core.di

import android.content.Context
import com.heckofanapp.weather.core.network.github.GithubApi
import com.heckofanapp.weather.core.network.github.GithubRepository
import com.heckofanapp.weather.core.network.sources.address.nominatim.NominatimApi
import com.heckofanapp.weather.core.network.sources.address.nominatim.json.NominatimRepository
import com.heckofanapp.weather.data.local.WeatherMasterDatabase
import com.heckofanapp.weather.data.local.dao.airquality.AirQualityDao
import com.heckofanapp.weather.data.local.dao.github.GithubDao
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherBlocksDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherUnitsDao
import com.heckofanapp.weather.data.local.dao.weather.nws.NwsDao
import com.heckofanapp.weather.data.repository.LocationsRepository
import com.heckofanapp.weather.data.repository.WeatherBlocksRepository
import com.heckofanapp.weather.data.repository.WeatherDataReconcilerRepository
import com.heckofanapp.weather.data.repository.WeatherUnitsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): WeatherMasterDatabase =
        WeatherMasterDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideLocationsRepository(
        dao: LocationsDao,
        airQualityDao: AirQualityDao,
        nominatimRepository: NominatimRepository,
        @ApplicationContext context: Context
    ): LocationsRepository = LocationsRepository(dao, airQualityDao, context, nominatimRepository)

    @Provides
    @Singleton
    fun provideWeatherUnitsRepository(dao: WeatherUnitsDao): WeatherUnitsRepository =
        WeatherUnitsRepository(dao)

    @Provides
    @Singleton
    fun provideWeatherBlocksRepository(
        weatherBlocksDao: WeatherBlocksDao
    ): WeatherBlocksRepository =
        WeatherBlocksRepository(weatherBlocksDao)

    @Provides
    @Singleton
    fun provideWeatherDataReconcilerRepository(
        nwsDao: NwsDao,
        locationsDao: LocationsDao
    ): WeatherDataReconcilerRepository = WeatherDataReconcilerRepository(nwsDao, locationsDao)

    @Provides
    @Singleton
    fun provideGithubRepository(
        api: GithubApi,
        dao: GithubDao
    ): GithubRepository = GithubRepository(api, dao)

    @Provides
    @Singleton
    fun provideNominatimRepository(api: NominatimApi): NominatimRepository =
        NominatimRepository(api)
}