package com.heckofanapp.weather.core.network.sources.weather.openweather

import com.heckofanapp.weather.BuildConfig
import com.heckofanapp.weather.core.network.sources.weather.openweather.json.OpenWeatherJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface OpenWeatherApi {
    /**
     * One Call gathers current, forty eight hours of hourly, and eight days of daily into a
     * single response.  Key travels as query parameter, so interceptor attaches it and it
     * stays out of this interface.
     */
    @GET("onecall")
    suspend fun fetchWeather(
        @Query("exclude") exclude: String = EXCLUDE,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = UNITS,
    ): Response<OpenWeatherJson>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
        const val EXCLUDE = "minutely,alerts" // App has no minute by minute or alert display.  So, neither block is asked for.
        const val UNITS = "metric"

        fun create(): OpenWeatherApi {
            val auth = Interceptor { chain ->
                val original = chain.request()
                val newUrl = original.url.newBuilder()
                    .addQueryParameter(
                        name = "appid",
                        value = BuildConfig.SOURCE_OPEN_WEATHER,
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
                .create(OpenWeatherApi::class.java)
        }
    }
}
