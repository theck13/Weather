package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.data.local.entity.weather.blocks.WeatherBlockEntity

enum class WeatherBlockType {
    HUMIDITY_BLOCK,
    VISIBILITY_BLOCK,
    UV_INDEX_BLOCK,
    PRESSURE_BLOCK,
    SUN_BLOCK,
    MOON_BLOCK,
    AIR_QUALITY_BLOCK,

    RAIN_BLOCK,
    SNOW_BLOCK,

    WIND_BLOCK

}

data class WeatherBlock(
    val id: Long,
    val isDaily: Boolean,
    val isHidden: Boolean,
    val position: Int,
    val type: WeatherBlockType,
) {
    companion object {
        fun getDefault() = listOf(
            WeatherBlock(
                id = 0,
                isDaily = false,
                isHidden = false,
                position = 0,
                type = WeatherBlockType.SUN_BLOCK,
            ),
            WeatherBlock(
                id = 1,
                isDaily = false,
                isHidden = false,
                position = 1,
                type = WeatherBlockType.MOON_BLOCK,
            ),
            WeatherBlock(
                id = 2,
                isDaily = false,
                isHidden = false,
                position = 2,
                type = WeatherBlockType.WIND_BLOCK,
            ),
            WeatherBlock(
                id = 3,
                isDaily = false,
                isHidden = false,
                position = 3,
                type = WeatherBlockType.HUMIDITY_BLOCK,
            ),
            WeatherBlock(
                id = 4,
                isDaily = false,
                isHidden = false,
                position = 4,
                type = WeatherBlockType.VISIBILITY_BLOCK,
            ),
            WeatherBlock(
                id = 5,
                isDaily = false,
                isHidden = false,
                position = 5,
                type = WeatherBlockType.RAIN_BLOCK,
            ),
            WeatherBlock(
                id = 6,
                isDaily = false,
                isHidden = false,
                position = 6,
                type = WeatherBlockType.PRESSURE_BLOCK,
            ),
            WeatherBlock(
                id = 7,
                isDaily = false,
                isHidden = false,
                position = 7,
                type = WeatherBlockType.SNOW_BLOCK,
            ),
            WeatherBlock(
                id = 8,
                isDaily = false,
                isHidden = false,
                position = 8,
                type = WeatherBlockType.UV_INDEX_BLOCK,
            ),
            WeatherBlock(
                id = 9,
                isDaily = false,
                isHidden = false,
                position = 9,
                type = WeatherBlockType.AIR_QUALITY_BLOCK,
            ),
        )

        fun getDefaultForDaily() = listOf(
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 0,
                type = WeatherBlockType.SUN_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 1,
                type = WeatherBlockType.MOON_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 2,
                type = WeatherBlockType.WIND_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 3,
                type = WeatherBlockType.HUMIDITY_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 4,
                type = WeatherBlockType.VISIBILITY_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 5,
                type = WeatherBlockType.RAIN_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 6,
                type = WeatherBlockType.PRESSURE_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 7,
                type = WeatherBlockType.SNOW_BLOCK,
            ),
            WeatherBlock(
                id = (999L..2000L).random(),
                isDaily = true,
                isHidden = false,
                position = 8,
                type = WeatherBlockType.UV_INDEX_BLOCK,
            ),
        )
    }
}

fun WeatherBlockEntity.toDomain(): WeatherBlock {
    return WeatherBlock(
        id = id,
        isDaily = isDaily,
        isHidden = isHidden,
        position = position,
        type = type,
    )
}
