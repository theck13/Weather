package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.FmiRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryProvider @Inject constructor(
    private val openMeteoRepository: OpenMeteoRepository,
    private val nwsRepository: NwsRepository,
    private val metNorwayRepository: MetNorwayRepository,
    private val smhiRepository: SmhiRepository,
    private val dwdRepository: DwdRepository,
    private val meteoFranceRepository: MeteoFranceRepository,
    private val ecccRepository: EcccRepository,
    private val fmiRepository: FmiRepository,
    private val chinaRepository: ChinaRepository,
    private val bmkgRepository: BmkgRepository,
) {
    fun getRepository(source: WeatherSource): WeatherRepository {
        return when (source) {
            WeatherSource.BMKG -> bmkgRepository
            WeatherSource.CNEMC -> chinaRepository
            WeatherSource.DWD -> dwdRepository
            WeatherSource.ECCC -> ecccRepository
            WeatherSource.FMI -> fmiRepository
            WeatherSource.MET -> metNorwayRepository
            WeatherSource.METEO -> meteoFranceRepository
            WeatherSource.NWS -> nwsRepository
            WeatherSource.OPEN -> openMeteoRepository
            WeatherSource.SMHI -> smhiRepository
        }
    }
}
