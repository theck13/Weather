package com.heckofanapp.weather.data.provider.devicelocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat

data class DeviceLocation(
    val latitude: Double?,
    val longitude: Double?,
)

class GetDeviceLocation {
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private var timeoutHandler: Handler? = null
    private val timeoutMillis = 20_000L
    private var timeoutRunnable: Runnable? = null

    /**
     * Picks the first provider that is both present on the device and currently enabled,
     * preferring GPS over network over passive. Returns null when no provider is enabled,
     * so the caller can report the location as unavailable.
     */
    private fun getProvider(lm: LocationManager): String? {
        val preferred = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )

        return preferred.firstOrNull { provider ->
            lm.allProviders.contains(provider) && lm.isProviderEnabled(provider)
        }
    }

    fun getDeviceLocation(
        context: Context,
        onTimeout: () -> Unit,
        onResult: (DeviceLocation) -> Unit
    ) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            onResult(DeviceLocation(null, null))
            return
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: run {
                onResult(DeviceLocation(null, null))
                return
            }

        locationManager = lm

        val provider = getProvider(lm) ?: run {
            onResult(DeviceLocation(null, null))
            return
        }

        getLocation(
            onLocation = {
                onResult(it)
            },
            locationManager = lm,
            onTimeout = onTimeout,
            provider = provider,
        )
    }

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ]
    )
    private fun getLocation(
        onLocation: (DeviceLocation) -> Unit,
        locationManager: LocationManager,
        onTimeout: () -> Unit,
        provider: String,
    ) {
        val lastKnown = locationManager.getLastKnownLocation(provider)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        if (lastKnown != null) {
            timeoutHandler?.removeCallbacks(timeoutRunnable!!)
            onLocation(
                DeviceLocation(
                    parseCord(lastKnown.latitude),
                    parseCord(lastKnown.longitude)
                )
            )
            return
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                timeoutHandler?.removeCallbacks(timeoutRunnable!!)
                onLocation(
                    DeviceLocation(
                        parseCord(location.latitude),
                        parseCord(location.longitude)
                    )
                )
                stopUpdates()
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            provider,
            0L,
            0f,
            locationListener!!,
            Looper.getMainLooper()
        )

        timeoutHandler = Handler(Looper.getMainLooper())

        timeoutRunnable = Runnable {
            stopUpdates()
            onTimeout()
        }

        timeoutHandler?.postDelayed(timeoutRunnable!!, timeoutMillis)
    }

    fun stopUpdates() {
        locationListener?.let { locationManager?.removeUpdates(it) }
        locationListener = null
    }
}

/**
 * Device might return lat/lon as a string based on the device locale
 * For e.g. "53,85893" -> app crashes, because upstream only takes in double
 * We convert that here
 */
private fun parseCord(value: Any?): Double? {
    return when (value) {
        is Double -> value
        is String -> value.replace(',', '.').toDouble()
        else -> null
    }
}
