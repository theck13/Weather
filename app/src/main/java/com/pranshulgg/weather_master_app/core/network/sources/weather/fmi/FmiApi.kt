package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FmiApi {
    @GET("wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::forecast::edited::weather::scandinavia::point::simple")
    suspend fun fetchForecast(
        @Query("latlon") latlon: String,
        @Query("endtime") endtime: String,
        @Query("starttime") starttime: String,
    ): Response<ResponseBody>

    @GET("wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::ef::stations")
    suspend fun fetchStations(): Response<ResponseBody>

    @GET("wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::simple")
    suspend fun fetchCurrent(
        @Query("fmisid") id: String,
        @Query("starttime") starttime: String,
        @Query("endtime") endtime: String,
    ): Response<ResponseBody>
    companion object {
        const val BASE_URL = "https://opendata.fmi.fi/"

        fun create(): FmiApi {
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
                .build()
                .create(FmiApi::class.java)
        }
    }
}
