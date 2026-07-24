package com.heckofanapp.weather.core.network.sources.weather.meteoam

import com.heckofanapp.weather.core.network.sources.weather.meteoam.json.MeteoAmCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.meteoam.json.MeteoAmForecastWeatherJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface MeteoAmApi {
    @GET("deda-ows/api/GetStationRadius/{latitude}/{longitude}")
    suspend fun fetchCurrent(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
    ): Response<MeteoAmCurrentWeatherJson>

    @GET("deda-meteograms/api/GetMeteogram/preset1/{latitude},{longitude}")
    suspend fun fetchForecast(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
    ): Response<MeteoAmForecastWeatherJson>

    companion object {
        const val BASE_URL = "https://api.meteoam.it/"

        fun create(): MeteoAmApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(
                    timeout = 30,
                    unit = TimeUnit.SECONDS,
                )
                .readTimeout(
                    timeout = 30,
                    unit = TimeUnit.SECONDS,
                )
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MeteoAmApi::class.java)
        }
    }
}
