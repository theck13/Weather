package com.pranshulgg.weather_master_app.core.model.weather

import android.content.Context
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit.KM
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit.M
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit.MI
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit.CM
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit.IN
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit.MM
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit.HPA
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit.INHG
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit.MMHG
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit.CELSIUS
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit.FAHRENHEIT
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit.BFT
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit.KPH
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit.KT
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit.MPH
import com.pranshulgg.weather_master_app.core.model.weather.WindUnit.MPS

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    fun convert(
        from: Double?,
        to: TemperatureUnit,
    ): Double? {
        if (from == null) return null
        if (this == to) return from

        val celsius = when (this) {
            CELSIUS -> from
            FAHRENHEIT -> (from - 32) * 5 / 9
        }

        return when (to) {
            CELSIUS -> celsius
            FAHRENHEIT -> (celsius * 9 / 5) + 32
        }
    }
}

enum class WindUnit {
    MPS,
    MPH,
    KPH,
    BFT,
    KT;

    fun convert(
        from: Double?,
        to: WindUnit,
    ): Double? {
        if (from == null) return null
        if (this == to) return from

        val mps = when (this) {
            MPS -> from
            KPH -> from / 3.6
            MPH -> from / 2.237
            KT -> from / 1.943844
            BFT -> beaufortToMps(from)
        }

        return when (to) {
            MPS -> mps
            KPH -> mps * 3.6
            MPH -> mps * 2.237
            KT -> mps * 1.943844
            BFT -> mpsToBeaufort(mps).toDouble()
        }
    }

    companion object {
        private fun beaufortToMps(
            bft: Double,
        ): Double = when (bft.toInt()) {
            0 -> 0.0
            1 -> 0.9
            2 -> 2.4
            3 -> 4.4
            4 -> 6.7
            5 -> 9.3
            6 -> 12.3
            7 -> 15.5
            8 -> 18.9
            9 -> 22.6
            10 -> 26.4
            11 -> 30.5
            else -> 32.7
        }

        private fun mpsToBeaufort(
            mps: Double,
        ): Int = when {
            mps < 0.3 -> 0
            mps < 1.6 -> 1
            mps < 3.4 -> 2
            mps < 5.5 -> 3
            mps < 8.0 -> 4
            mps < 10.8 -> 5
            mps < 13.9 -> 6
            mps < 17.2 -> 7
            mps < 20.8 -> 8
            mps < 24.5 -> 9
            mps < 28.5 -> 10
            mps < 32.7 -> 11
            else -> 12
        }
    }
}

enum class PressureUnit {
    HPA,
    INHG,
    MMHG;

    fun convert(
        from: Double?,
        to: PressureUnit,
    ): Double? {
        if (from == null) return null
        if (this == to) return from

        val hpa = when (this) {
            HPA -> from
            INHG -> from * 33.8639
            MMHG -> from * 1.33322
        }

        return when (to) {
            HPA -> hpa
            INHG -> hpa * 0.02953
            MMHG -> hpa * 0.75006
        }
    }
}

enum class DistanceUnit {
    KM,
    MI,
    M;

    fun convert(
        from: Double?,
        to: DistanceUnit,
    ): Double? {
        if (from == null) return null
        if (this == to) return from
        val meters = when (this) {
            M -> from
            KM -> from * 1000
            MI -> from * 1609
        }

        return when (to) {
            M -> meters
            KM -> meters / 1000
            MI -> meters / 1609
        }
    }
}

enum class PrecipitationUnit {
    CM,
    IN,
    MM;

    fun convert(
        from: Double?,
        to: PrecipitationUnit,
    ): Double? {
        if (from == null) return null
        if (this == to) return from

        val mm = when (this) {
            CM -> from * 10
            IN -> from * 25.4
            MM -> from
        }

        return when (to) {
            CM -> mm / 10
            IN -> mm / 25.4
            MM -> mm
        }
    }
}

fun TemperatureUnit.toName(
    context: Context,
): String {
    return when (this) {
        CELSIUS -> context.getString(R.string.unit_temperature_celsius)
        FAHRENHEIT -> context.getString(R.string.unit_temperature_fahrenheit)
    }
}

fun WindUnit.toName(
    context: Context,
    inShort: Boolean = false,
): String {
    return when (this) {
        BFT -> context.getString(if (inShort) R.string.unit_wind_bft_short else R.string.unit_wind_bft)
        KPH -> context.getString(if (inShort) R.string.unit_wind_kph_short else R.string.unit_wind_kph)
        KT -> context.getString(if (inShort) R.string.unit_wind_kt_short else R.string.unit_wind_kt)
        MPH -> context.getString(if (inShort) R.string.unit_wind_mph_short else R.string.unit_wind_mph)
        MPS -> context.getString(if (inShort) R.string.unit_wind_mps_short else R.string.unit_wind_mps)
    }
}

fun PressureUnit.toName(
    context: Context,
    inShort: Boolean = false,
): String {
    return when (this) {
        HPA -> context.getString(if (inShort) R.string.unit_pressure_hpa_short else R.string.unit_pressure_hpa)
        INHG -> context.getString(if (inShort) R.string.unit_pressure_inhg_short else R.string.unit_pressure_inhg)
        MMHG -> context.getString(if (inShort) R.string.unit_pressure_mmhg_short else R.string.unit_pressure_mmhg)
    }
}

fun DistanceUnit.toName(
    context: Context,
    inShort: Boolean = false,
): String {
    return when (this) {
        KM -> context.getString(if (inShort) R.string.unit_distance_km_short else R.string.unit_distance_km)
        M -> context.getString(if (inShort) R.string.unit_distance_m_short else R.string.unit_distance_m)
        MI -> context.getString(if (inShort) R.string.unit_distance_mi_short else R.string.unit_distance_mi)
    }
}

fun PrecipitationUnit.toName(
    context: Context,
    inShort: Boolean = false,
): String {
    return when (this) {
        CM -> context.getString(if (inShort) R.string.unit_precipitation_cm_short else R.string.unit_precipitation_cm)
        IN -> context.getString(if (inShort) R.string.unit_precipitation_inch_short else R.string.unit_precipitation_inch)
        MM -> context.getString(if (inShort) R.string.unit_precipitation_mm_short else R.string.unit_precipitation_mm)
    }
}
