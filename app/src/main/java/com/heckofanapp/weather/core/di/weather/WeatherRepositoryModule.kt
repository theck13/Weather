package com.heckofanapp.weather.core.di.weather

import com.heckofanapp.weather.core.network.sources.airquality.openmeteo.OpenMeteoAqiApi
import com.heckofanapp.weather.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.heckofanapp.weather.core.network.sources.weather.bmkg.BmkgApi
import com.heckofanapp.weather.core.network.sources.weather.bmkg.BmkgRepository
import com.heckofanapp.weather.core.network.sources.weather.china.ChinaApi
import com.heckofanapp.weather.core.network.sources.weather.china.ChinaRepository
import com.heckofanapp.weather.core.network.sources.weather.dwd.DwdApi
import com.heckofanapp.weather.core.network.sources.weather.dwd.DwdRepository
import com.heckofanapp.weather.core.network.sources.weather.eccc.EcccApi
import com.heckofanapp.weather.core.network.sources.weather.eccc.EcccRepository
import com.heckofanapp.weather.core.network.sources.weather.fmi.FmiApi
import com.heckofanapp.weather.core.network.sources.weather.fmi.FmiRepository
import com.heckofanapp.weather.core.network.sources.weather.meteofrance.MeteoFranceApi
import com.heckofanapp.weather.core.network.sources.weather.meteofrance.MeteoFranceRepository
import com.heckofanapp.weather.core.network.sources.weather.metnorway.MetNorwayApi
import com.heckofanapp.weather.core.network.sources.weather.metnorway.MetNorwayRepository
import com.heckofanapp.weather.core.network.sources.weather.nws.NwsApi
import com.heckofanapp.weather.core.network.sources.weather.nws.NwsRepository
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.heckofanapp.weather.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.heckofanapp.weather.core.network.sources.weather.smhi.SmhiApi
import com.heckofanapp.weather.core.network.sources.weather.smhi.SmhiRepository
import com.heckofanapp.weather.data.local.dao.airquality.AirQualityDao
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.dao.weather.nws.NwsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class,
)
object WeatherRepositoryModule {
    @Provides
    @Singleton
    fun provideBmkgRepository(
        dao: LocationsDao,
        api: BmkgApi,
        weatherDao: WeatherDao,
    ): BmkgRepository = BmkgRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideChinaRepository(
        dao: LocationsDao,
        api: ChinaApi,
        weatherDao: WeatherDao,
    ): ChinaRepository = ChinaRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideDwdRepository(
        dao: LocationsDao,
        api: DwdApi,
        weatherDao: WeatherDao,
    ): DwdRepository = DwdRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideEcccRepository(
        dao: LocationsDao,
        api: EcccApi,
        weatherDao: WeatherDao,
    ): EcccRepository = EcccRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideFmiRepository(
        dao: LocationsDao,
        api: FmiApi,
        weatherDao: WeatherDao,
    ): FmiRepository = FmiRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideMeteoFranceRepository(
        dao: LocationsDao,
        api: MeteoFranceApi,
        weatherDao: WeatherDao,
    ): MeteoFranceRepository = MeteoFranceRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideMetNorwayRepository(
        dao: LocationsDao,
        api: MetNorwayApi,
        weatherDao: WeatherDao,
    ): MetNorwayRepository = MetNorwayRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideNwsRepository(
        api: NwsApi,
        dao: LocationsDao,
        weatherDao: WeatherDao,
        nwsDao: NwsDao,
    ): NwsRepository = NwsRepository(dao, weatherDao, nwsDao, api)

    @Provides
    @Singleton
    fun provideOpenMeteoAqiRepository(
        api: OpenMeteoAqiApi,
        dao: AirQualityDao,
    ): OpenMeteoAqiRepository = OpenMeteoAqiRepository(api, dao)

    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: LocationsDao,
        api: OpenMeteoApi,
        weatherDao: WeatherDao,
    ): OpenMeteoRepository = OpenMeteoRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideSmhiRepository(
        dao: LocationsDao,
        api: SmhiApi,
        weatherDao: WeatherDao,
    ): SmhiRepository = SmhiRepository(dao, weatherDao, api)
}
