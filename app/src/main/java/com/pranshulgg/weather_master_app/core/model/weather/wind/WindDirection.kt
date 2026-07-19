package com.pranshulgg.weather_master_app.core.model.weather.wind

enum class WindDirection {
    N,
    NE,
    SE,
    S,
    SW,
    W,
    NW,
    E;

    companion object {
        // For rotating the arrow.
        fun toDegrees(
            windDirection: WindDirection?,
        ): Int? {
            return when (windDirection) {
                N -> 0
                NE -> 45
                E -> 90
                SE -> 135
                S -> 180
                SW -> 225
                W -> 270
                NW -> 315
                else -> null
            }
        }

        // For sources that return values in degrees.
        fun toWindDirectionFromDegrees(
            value: Int?,
        ): WindDirection? {
            return when (value) {
                null -> null
                in 0..22,
                in 338..360 -> N
                in 23..67 -> NE
                in 68..112 -> E
                in 113..157 -> SE
                in 158..202 -> S
                in 203..247 -> SW
                in 248..292 -> W
                in 293..337 -> NW
                else -> null
            }
        }

        // For sources that return values in cardinal directions (N, NE, etc.).
        fun toWindDirectionFromString(
            value: String?,
        ): WindDirection? {
            return when (value?.uppercase()) {
                "N", "NNE", "NNW" -> N
                "NE", "ENE" -> NE
                "E", "ESE" -> E
                "SE", "SSE" -> SE
                "S", "SSW" -> S
                "SW", "WSW" -> SW
                "W", "WNW" -> W
                "NW" -> NW
                else -> null
            }
        }
    }
}
