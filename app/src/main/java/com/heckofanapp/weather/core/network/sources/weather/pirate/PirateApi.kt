package com.heckofanapp.weather.core.network.sources.weather.pirate

import com.heckofanapp.weather.BuildConfig
import com.heckofanapp.weather.core.network.sources.weather.pirate.json.PirateJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PirateApi {
    /**
     * Key rides in path rather than as query parameter.  So, interceptor can't add it and it
     * travels as defaulted argument instead, which keeps it out of repository.
     *
     * Retrofit rejects any path that trails a query.  So, paths group ahead of queries rather
     * than sorting alphabetically alongside them.
     */
    @GET("forecast/{apiKey}/{latitude},{longitude}")
    suspend fun fetchWeather(
        @Path("apiKey") apiKey: String = BuildConfig.SOURCE_PIRATE_BOOTY,
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
        @Query("exclude") exclude: String = EXCLUDE,
        @Query("extend") extend: String = EXTEND,
        @Query("icon") icon: String = ICON,
        @Query("units") units: String = UNITS,
        @Query("version") version: Int = VERSION,
    ): Response<PirateJson>

    companion object {
        const val BASE_URL = "https://api.pirateweather.net/"
        const val EXCLUDE = "minutely,alerts" // App has no minute by minute or alert display.  So, neither block is asked for.
        const val EXTEND = "hourly" // Stretches hourly from 48 to 168 hours, covering every forecast day.
        const val ICON = "pirate" // Wider icon set than Dark Sky's original eleven.
        const val UNITS = "si"
        const val VERSION = 2 // Splits precipitation by type and adds accumulation fields.

        fun create(): PirateApi {
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
                .create(PirateApi::class.java)
        }
    }
}
