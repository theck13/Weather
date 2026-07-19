package com.pranshulgg.weather_master_app.core.model.weather.ultraviolet

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.theme.Green400
import com.pranshulgg.weather_master_app.core.ui.theme.Orange600
import com.pranshulgg.weather_master_app.core.ui.theme.Purple800
import com.pranshulgg.weather_master_app.core.ui.theme.Red800
import com.pranshulgg.weather_master_app.core.ui.theme.Yellow600

enum class UltravioletIndex {
    LOW,
    MODERATE,
    HIGH,
    VERY_HIGH,
    EXTREME,
}

fun getUltravioletIndex(
    ultravioletIndex: Int,
): UltravioletIndex {
    return when (ultravioletIndex) {
        in 0..2 -> UltravioletIndex.LOW
        in 3..5 -> UltravioletIndex.MODERATE
        in 6..7 -> UltravioletIndex.HIGH
        in 8..10 -> UltravioletIndex.VERY_HIGH
        else -> UltravioletIndex.EXTREME
    }
}

fun UltravioletIndex.toColor(): Color {
    return when (this) {
        UltravioletIndex.LOW -> Green400
        UltravioletIndex.MODERATE -> Yellow600
        UltravioletIndex.HIGH -> Orange600
        UltravioletIndex.VERY_HIGH -> Red800
        UltravioletIndex.EXTREME -> Purple800
    }
}

fun UltravioletIndex.toDescription(
    context: Context,
): String {
    return context.getString(
        when (this) {
            UltravioletIndex.LOW -> R.string.weather_ultraviolet_description_1
            UltravioletIndex.MODERATE -> R.string.weather_ultraviolet_description_2
            UltravioletIndex.HIGH -> R.string.weather_ultraviolet_description_3
            UltravioletIndex.VERY_HIGH -> R.string.weather_ultraviolet_description_4
            UltravioletIndex.EXTREME -> R.string.weather_ultraviolet_description_5
        }
    )
}

fun UltravioletIndex.toLabel(
    context: Context,
): String {
    return when (this) {
        UltravioletIndex.LOW -> context.getString(R.string.weather_ultraviolet_scale_1)
        UltravioletIndex.MODERATE -> context.getString(R.string.weather_ultraviolet_scale_2)
        UltravioletIndex.HIGH -> context.getString(R.string.weather_ultraviolet_scale_3)
        UltravioletIndex.VERY_HIGH -> context.getString(R.string.weather_ultraviolet_scale_4)
        UltravioletIndex.EXTREME -> context.getString(R.string.weather_ultraviolet_scale_5)
    }
}
