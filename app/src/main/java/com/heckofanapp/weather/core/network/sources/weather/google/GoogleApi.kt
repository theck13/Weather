package com.heckofanapp.weather.core.network.sources.weather.google

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.heckofanapp.weather.BuildConfig
import com.heckofanapp.weather.core.network.sources.weather.google.GoogleApi.Companion.HOURLY_HOURS
import com.heckofanapp.weather.core.network.sources.weather.google.GoogleApi.Companion.HOURLY_PAGE_SIZE
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleCurrentWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleDailyWeatherJson
import com.heckofanapp.weather.core.network.sources.weather.google.json.GoogleHourlyWeatherJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

interface GoogleApi {
    @GET("v1/currentConditions:lookup")
    suspend fun fetchCurrent(
        @Query("location.latitude") latitude: Double,
        @Query("location.longitude") longitude: Double,
        @Query("unitsSystem") unitsSystem: String = UNITS_SYSTEM,
    ): Response<GoogleCurrentWeatherJson>

    /**
     * Page holds at most [HOURLY_PAGE_SIZE] hours; [HOURLY_HOURS] takes several calls chained by [pageToken].
     */
    @GET("v1/forecast/hours:lookup")
    suspend fun fetchHourly(
        @Query("hours") hours: Int = HOURLY_HOURS,
        @Query("location.latitude") latitude: Double,
        @Query("location.longitude") longitude: Double,
        @Query("pageSize") pageSize: Int = HOURLY_PAGE_SIZE,
        @Query("pageToken") pageToken: String? = null,
        @Query("unitsSystem") unitsSystem: String = UNITS_SYSTEM,
    ): Response<GoogleHourlyWeatherJson>

    @GET("v1/forecast/days:lookup")
    suspend fun fetchDaily(
        @Query("days") days: Int = DAILY_DAYS,
        @Query("location.latitude") latitude: Double,
        @Query("location.longitude") longitude: Double,
        @Query("pageSize") pageSize: Int = DAILY_DAYS,
        @Query("unitsSystem") unitsSystem: String = UNITS_SYSTEM,
    ): Response<GoogleDailyWeatherJson>

    companion object {
        const val BASE_URL = "https://weather.googleapis.com/"

        // Every call is billed.  Keep hour count only as high as app displays.
        const val DAILY_DAYS = 10 // Maximum endpoint allows.
        const val HOURLY_HOURS = 120
        const val HOURLY_PAGE_SIZE = 24 // Maximum endpoint allows.
        const val UNITS_SYSTEM = "METRIC"

        fun create(context: Context): GoogleApi {
            // Key is restricted to specific app.  So, every call has to identify which app
            // it came from.  Google's own client libraries add these headers.  So, plain
            // OkHttp has to send them or API answers 403 API_KEY_ANDROID_APP_BLOCKED.
            val packageName = context.packageName
            val certificate = signingCertificate(context)

            val auth = Interceptor { chain ->
                val original = chain.request()
                val newUrl = original.url.newBuilder()
                    .addQueryParameter(
                        name = "key",
                        value = BuildConfig.SOURCE_GOOGLE_PIXEL,
                    )
                    .build()
                val builder = original.newBuilder()
                    .url(newUrl)
                    .header(
                        name = "X-Android-Package",
                        value = packageName,
                    )

                if (certificate != null) {
                    builder.header(
                        name = "X-Android-Cert",
                        value = certificate,
                    )
                }

                chain.proceed(builder.build())
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
                .create(GoogleApi::class.java)
        }

        /**
         * SHA-1 of certificate app was signed with, uppercase hex without separators, which is
         * shape header expects.  Since debug and release builds sign with different certificates,
         * both have to be registered against key.  Null leaves header off rather than sending a
         * wrong one.
         */
        private fun signingCertificate(
            context: Context,
        ): String? = try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES,
                ).signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES,
                ).signatures
            }

            signatures?.firstOrNull()?.let { signature ->
                MessageDigest.getInstance("SHA-1")
                    .digest(signature.toByteArray())
                    .joinToString("") { byte -> "%02X".format(byte) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Signing certificate could not be read", e)

            null
        }

        private const val TAG = "GoogleApi"
    }
}
