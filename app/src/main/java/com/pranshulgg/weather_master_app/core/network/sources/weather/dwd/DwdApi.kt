package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd

import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.DwdCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.DwdWeatherForecastJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface DwdApi {

    @GET("current_weather")
    suspend fun fetchCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<DwdCurrentWeatherJson>

    @GET("weather")
    suspend fun fetchWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("date") date: String,
        @Query("last_date") lastDate: String,
    ): Response<DwdWeatherForecastJson>

    companion object {
        const val BASE_URL = "https://api.brightsky.dev/"

        fun create(): DwdApi {
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
                .create(DwdApi::class.java)
        }
    }
}
