package com.heckofanapp.weather.core.ui.theme

import com.materialkolor.PaletteStyle

enum class ThemeVariant {
    EXPRESSIVE,
    NEUTRAL,
    TONAL,
    VIBRANT,
}

fun ThemeVariant.toPaletteStyle(): PaletteStyle {
    return when (this) {
        ThemeVariant.EXPRESSIVE -> PaletteStyle.Expressive
        ThemeVariant.NEUTRAL -> PaletteStyle.Neutral
        ThemeVariant.TONAL -> PaletteStyle.TonalSpot
        ThemeVariant.VIBRANT -> PaletteStyle.Vibrant
    }
}
