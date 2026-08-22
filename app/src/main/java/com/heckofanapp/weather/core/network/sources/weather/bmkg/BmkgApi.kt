package com.heckofanapp.weather.core.network.sources.weather.bmkg

import com.heckofanapp.weather.core.network.sources.weather.bmkg.json.BmkgCurrentForecastJson
import com.heckofanapp.weather.core.network.sources.weather.bmkg.json.BmkgForecastJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BmkgApi {

    @GET("api/presentwx/coord")
    suspend fun fetchCurrent(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<BmkgCurrentForecastJson>

    @GET("api/df/v1/forecast/coord")
    suspend fun fetchForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<BmkgForecastJson>
    companion object {
        const val BASE_URL = "https://cuaca.bmkg.go.id/"

        fun create(): BmkgApi {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    // BMKG's Cloudflare WAF rejects api/df/v1/forecast/coord with 403 unless
                    // request carries same-origin Referer, matching their own frontend's request
                    // pattern - confirmed live, doesn't affect other endpoint either way.
                    val request = chain.request().newBuilder()
                        .header(
                            name = "Referer",
                            value = BASE_URL,
                        )
                        .build()

                    chain.proceed(request)
                }
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
                .create(BmkgApi::class.java)
        }
    }
}

