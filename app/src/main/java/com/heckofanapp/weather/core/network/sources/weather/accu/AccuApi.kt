package com.heckofanapp.weather.core.network.sources.weather.accu

import com.heckofanapp.weather.BuildConfig
import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuDailyWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuHourlyWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.accu.json.AccuLocationJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AccuApi {
    @GET("currentconditions/v1/{locationKey}")
    suspend fun fetchCurrent(
        @Path("locationKey") locationKey: String,
        @Query("details") details: Boolean = true,
        @Query("metric") metric: Boolean = true,
    ): Response<List<AccuCurrentWeatherJson>>

    @GET("locations/v1/cities/geoposition/search.json")
    suspend fun getLocationKey(
        @Query("q") query: String,
    ): Response<AccuLocationJson>

    @GET("forecasts/v1/hourly/120hour/{locationKey}")
    suspend fun fetchHourly(
        @Path("locationKey") locationKey: String,
        @Query("details") details: Boolean = true,
        @Query("metric") metric: Boolean = true,
    ): Response<List<AccuHourlyWeatherJson>>

    @GET("forecasts/v1/daily/5day/{locationKey}")
    suspend fun fetchDaily(
        @Path("locationKey") locationKey: String,
        @Query("details") details: Boolean = true,
        @Query("metric") metric: Boolean = true,
    ): Response<AccuDailyWeatherJson>

    companion object {
        const val BASE_URL = "https://api.accuweather.com/"

        fun create(): AccuApi {
            val auth = Interceptor { chain ->
                val original = chain.request()
                val newUrl = original.url.newBuilder()
                    .addQueryParameter(
                        name = "apikey",
                        value = BuildConfig.ACCU_KEY,
                    )
                    .build()
                val request = original.newBuilder().url(newUrl).build()

                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(auth)
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
                .create(AccuApi::class.java)
        }
    }
}
