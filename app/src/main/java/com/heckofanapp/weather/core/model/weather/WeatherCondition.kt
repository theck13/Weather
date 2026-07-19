package com.heckofanapp.weather.core.model.weather

import android.content.Context
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.weather.WeatherDaily

// TODO: ADD SLEET, HAIL, WINDY, BLIZZARD, TORNADO, HURRICANE
enum class WeatherCondition {
    CLEAR_SKY,
    OVERCAST,
    PARTLY_CLOUDY,
    HEAVY_RAIN,
    HEAVY_SNOW,
    LIGHT_SNOW,
    LIGHT_RAIN,
    MOSTLY_CLEAR,
    SNOW,
    THUNDERSTORM,
    VERY_HOT,
    VERY_COLD,
    FOG_HAZE,
    RAIN,
    NO_CONDITION_FOUND,

    // USED FOR DAILY
    CLEAR_WITH_CLOUDY,
    CLEAR_WITH_RAIN,
    CLEAR_WITH_SNOW,

    CLOUDY_WITH_CLEAR,
    CLOUDY_WITH_RAIN,
    CLOUDY_WITH_SNOW,

    RAIN_WITH_CLEAR,
    RAIN_WITH_CLOUDY,
    RAIN_WITH_SNOW,

    SNOW_WITH_CLEAR,
    SNOW_WITH_CLOUDY,
    SNOW_WITH_RAIN,

    CLEAR_THEN_CLOUDY,
    CLEAR_THEN_RAIN,
    CLEAR_THEN_SNOW,

    CLOUDY_THEN_CLEAR,
    CLOUDY_THEN_RAIN,
    CLOUDY_THEN_SNOW,

    RAIN_THEN_CLEAR,
    RAIN_THEN_CLOUDY,
    RAIN_THEN_SNOW,

    SNOW_THEN_CLEAR,
    SNOW_THEN_CLOUDY,
    SNOW_THEN_RAIN,
}

fun WeatherCondition.toIcon(
    daily: WeatherDaily? = null,
    targetTimeMilli: Long,
): Int {
    val isDay = if (daily != null) {
        targetTimeMilli in daily.sunrise..daily.sunset
    } else {
        true
    }

    return when (this) {
        WeatherCondition.CLEAR_SKY -> if (isDay) R.drawable.il_weather_clear_day else R.drawable.il_weather_clear_night
        WeatherCondition.CLEAR_WITH_CLOUDY, WeatherCondition.CLEAR_THEN_CLOUDY -> R.drawable.il_weather_partly_cloudy_day
        WeatherCondition.CLEAR_WITH_RAIN, WeatherCondition.CLEAR_THEN_RAIN -> R.drawable.il_weather_light_rain_day
        WeatherCondition.CLEAR_WITH_SNOW, WeatherCondition.CLEAR_THEN_SNOW -> R.drawable.il_weather_light_snow_day
        WeatherCondition.CLOUDY_WITH_CLEAR, WeatherCondition.CLOUDY_THEN_CLEAR -> R.drawable.il_weather_mostly_clear_day
        WeatherCondition.CLOUDY_WITH_RAIN, WeatherCondition.CLOUDY_THEN_RAIN -> R.drawable.il_weather_rain
        WeatherCondition.CLOUDY_WITH_SNOW, WeatherCondition.CLOUDY_THEN_SNOW -> R.drawable.il_weather_snow
        WeatherCondition.FOG_HAZE -> R.drawable.il_weather_haze_fog_dust_smoke
        WeatherCondition.HEAVY_RAIN -> R.drawable.il_weather_heavy_rain
        WeatherCondition.HEAVY_SNOW -> R.drawable.il_weather_heavy_snow
        WeatherCondition.LIGHT_RAIN -> if (isDay) R.drawable.il_weather_light_rain_day else R.drawable.il_weather_light_rain_night
        WeatherCondition.LIGHT_SNOW -> if (isDay) R.drawable.il_weather_light_snow_day else R.drawable.il_weather_light_snow_night
        WeatherCondition.MOSTLY_CLEAR -> if (isDay) R.drawable.il_weather_mostly_clear_day else R.drawable.il_weather_mostly_clear_night
        WeatherCondition.NO_CONDITION_FOUND -> R.drawable.il_weather_unavailable
        WeatherCondition.OVERCAST -> R.drawable.il_weather_overcast
        WeatherCondition.PARTLY_CLOUDY -> if (isDay) R.drawable.il_weather_partly_cloudy_day else R.drawable.il_weather_partly_cloudy_night
        WeatherCondition.RAIN -> R.drawable.il_weather_rain
        WeatherCondition.RAIN_WITH_CLEAR, WeatherCondition.RAIN_THEN_CLEAR -> R.drawable.il_weather_light_rain_day
        WeatherCondition.RAIN_WITH_CLOUDY, WeatherCondition.RAIN_THEN_CLOUDY -> R.drawable.il_weather_rain
        WeatherCondition.RAIN_WITH_SNOW, WeatherCondition.RAIN_THEN_SNOW -> R.drawable.il_weather_mixed_rain_snow
        WeatherCondition.SNOW -> R.drawable.il_weather_snow
        WeatherCondition.SNOW_WITH_CLEAR, WeatherCondition.SNOW_THEN_CLEAR -> R.drawable.il_weather_light_snow_day
        WeatherCondition.SNOW_WITH_CLOUDY, WeatherCondition.SNOW_THEN_CLOUDY -> R.drawable.il_weather_snow
        WeatherCondition.SNOW_WITH_RAIN, WeatherCondition.SNOW_THEN_RAIN -> R.drawable.il_weather_mixed_rain_snow
        WeatherCondition.THUNDERSTORM -> R.drawable.il_weather_thunderstorm
        WeatherCondition.VERY_COLD -> R.drawable.il_weather_very_cold
        WeatherCondition.VERY_HOT -> R.drawable.il_weather_very_hot
    }
}

fun WeatherCondition.toIconPair(
    daily: WeatherDaily? = null,
    targetTimeMilli: Long,
): Pair<Int, Int?> {
    return when (this) {
        WeatherCondition.CLEAR_THEN_CLOUDY,
        WeatherCondition.CLEAR_WITH_CLOUDY -> Pair(R.drawable.il_weather_clear_day, R.drawable.il_weather_cloudy)
        WeatherCondition.CLEAR_THEN_RAIN,
        WeatherCondition.CLEAR_WITH_RAIN -> Pair(R.drawable.il_weather_clear_day, R.drawable.il_weather_rain)
        WeatherCondition.CLEAR_THEN_SNOW,
        WeatherCondition.CLEAR_WITH_SNOW -> Pair(R.drawable.il_weather_clear_day, R.drawable.il_weather_snow)
        WeatherCondition.CLOUDY_THEN_CLEAR,
        WeatherCondition.CLOUDY_WITH_CLEAR -> Pair(R.drawable.il_weather_cloudy, R.drawable.il_weather_clear_day)
        WeatherCondition.CLOUDY_THEN_RAIN,
        WeatherCondition.CLOUDY_WITH_RAIN -> Pair(R.drawable.il_weather_cloudy, R.drawable.il_weather_rain)
        WeatherCondition.CLOUDY_THEN_SNOW,
        WeatherCondition.CLOUDY_WITH_SNOW -> Pair(R.drawable.il_weather_cloudy, R.drawable.il_weather_snow)
        WeatherCondition.RAIN_THEN_CLEAR,
        WeatherCondition.RAIN_WITH_CLEAR -> Pair(R.drawable.il_weather_rain, R.drawable.il_weather_clear_day)
        WeatherCondition.RAIN_THEN_CLOUDY,
        WeatherCondition.RAIN_WITH_CLOUDY -> Pair(R.drawable.il_weather_rain, R.drawable.il_weather_cloudy)
        WeatherCondition.RAIN_THEN_SNOW,
        WeatherCondition.RAIN_WITH_SNOW -> Pair(R.drawable.il_weather_rain, R.drawable.il_weather_snow)
        WeatherCondition.SNOW_THEN_CLEAR,
        WeatherCondition.SNOW_WITH_CLEAR -> Pair(R.drawable.il_weather_snow, R.drawable.il_weather_clear_day)
        WeatherCondition.SNOW_THEN_CLOUDY,
        WeatherCondition.SNOW_WITH_CLOUDY -> Pair(R.drawable.il_weather_snow, R.drawable.il_weather_cloudy)
        WeatherCondition.SNOW_THEN_RAIN,
        WeatherCondition.SNOW_WITH_RAIN -> Pair(R.drawable.il_weather_snow, R.drawable.il_weather_rain)

        else -> Pair(toIcon(daily, targetTimeMilli), null)
    }
}

fun WeatherCondition.toLabel(
    context: Context,
): String {
    return context.getString(
        when (this) {
            WeatherCondition.CLEAR_SKY -> R.string.weather_clear_sky
            WeatherCondition.CLEAR_THEN_CLOUDY -> R.string.weather_clear_then_cloudy
            WeatherCondition.CLEAR_THEN_RAIN -> R.string.weather_clear_then_rain
            WeatherCondition.CLEAR_THEN_SNOW -> R.string.weather_clear_then_snow
            WeatherCondition.CLEAR_WITH_CLOUDY -> R.string.weather_clear_with_cloudy
            WeatherCondition.CLEAR_WITH_RAIN -> R.string.weather_clear_with_rain
            WeatherCondition.CLEAR_WITH_SNOW -> R.string.weather_clear_with_snow
            WeatherCondition.CLOUDY_THEN_CLEAR -> R.string.weather_cloudy_then_clear
            WeatherCondition.CLOUDY_THEN_RAIN -> R.string.weather_cloudy_then_rain
            WeatherCondition.CLOUDY_THEN_SNOW -> R.string.weather_cloudy_then_snow
            WeatherCondition.CLOUDY_WITH_CLEAR -> R.string.weather_cloudy_with_clear
            WeatherCondition.CLOUDY_WITH_RAIN -> R.string.weather_cloudy_with_rain
            WeatherCondition.CLOUDY_WITH_SNOW -> R.string.weather_cloudy_with_snow
            WeatherCondition.FOG_HAZE -> R.string.weather_haze
            WeatherCondition.HEAVY_RAIN -> R.string.weather_heavy_rain
            WeatherCondition.HEAVY_SNOW -> R.string.weather_heavy_snow
            WeatherCondition.LIGHT_RAIN -> R.string.weather_light_rain
            WeatherCondition.LIGHT_SNOW -> R.string.weather_light_snow
            WeatherCondition.MOSTLY_CLEAR -> R.string.weather_mostly_clear
            WeatherCondition.NO_CONDITION_FOUND -> R.string.weather_no_condition
            WeatherCondition.OVERCAST -> R.string.weather_overcast
            WeatherCondition.PARTLY_CLOUDY -> R.string.weather_partly_cloudy
            WeatherCondition.RAIN -> R.string.weather_rain
            WeatherCondition.RAIN_THEN_CLEAR -> R.string.weather_rain_then_clear
            WeatherCondition.RAIN_THEN_CLOUDY -> R.string.weather_rain_then_cloudy
            WeatherCondition.RAIN_THEN_SNOW -> R.string.weather_rain_then_snow
            WeatherCondition.RAIN_WITH_CLEAR -> R.string.weather_rain_with_clear
            WeatherCondition.RAIN_WITH_CLOUDY -> R.string.weather_rain_with_cloudy
            WeatherCondition.RAIN_WITH_SNOW -> R.string.weather_rain_with_snow
            WeatherCondition.SNOW -> R.string.weather_snow
            WeatherCondition.SNOW_THEN_CLEAR -> R.string.weather_snow_then_clear
            WeatherCondition.SNOW_THEN_CLOUDY -> R.string.weather_snow_then_cloudy
            WeatherCondition.SNOW_THEN_RAIN -> R.string.weather_snow_then_rain
            WeatherCondition.SNOW_WITH_CLEAR -> R.string.weather_snow_with_clear
            WeatherCondition.SNOW_WITH_CLOUDY -> R.string.weather_snow_with_cloudy
            WeatherCondition.SNOW_WITH_RAIN -> R.string.weather_snow_with_rain
            WeatherCondition.THUNDERSTORM -> R.string.weather_thunderstorm
            WeatherCondition.VERY_COLD -> R.string.weather_very_cold
            WeatherCondition.VERY_HOT -> R.string.weather_very_hot
        }
    )
}

fun WeatherCondition.toFroggy(
    daily: WeatherDaily? = null,
    targetTimeMilli: Long,
): Int {
    val isDay = if (daily != null) {
        targetTimeMilli in daily.sunrise..daily.sunset
    } else {
        true
    }

    return when (this) {
        WeatherCondition.CLEAR_SKY -> if (isDay) R.drawable.im_froggy_clear_day else R.drawable.im_froggy_clear_night
        WeatherCondition.FOG_HAZE -> R.drawable.im_froggy_haze
        WeatherCondition.HEAVY_RAIN -> R.drawable.im_froggy_rain
        WeatherCondition.HEAVY_SNOW -> R.drawable.im_froggy_snow
        WeatherCondition.LIGHT_RAIN -> R.drawable.im_froggy_light_rain
        WeatherCondition.LIGHT_SNOW -> R.drawable.im_froggy_snow
        WeatherCondition.MOSTLY_CLEAR -> if (isDay) R.drawable.im_froggy_mostly_clear_day else R.drawable.im_froggy_mostly_clear_night
        WeatherCondition.NO_CONDITION_FOUND -> R.drawable.il_weather_unavailable
        WeatherCondition.OVERCAST -> R.drawable.im_froggy_overcast
        WeatherCondition.PARTLY_CLOUDY -> R.drawable.im_froggy_partly_cloudy
        WeatherCondition.RAIN -> R.drawable.im_froggy_rain
        WeatherCondition.SNOW -> R.drawable.im_froggy_snow
        WeatherCondition.THUNDERSTORM -> R.drawable.im_froggy_thunder
        WeatherCondition.VERY_COLD -> R.drawable.il_weather_unavailable // WILL NEVER HAPPEN WITH FROGGY
        WeatherCondition.VERY_HOT -> R.drawable.il_weather_unavailable // WILL NEVER HAPPEN WITH FROGGY

        else -> R.drawable.il_weather_unavailable
    }
}
