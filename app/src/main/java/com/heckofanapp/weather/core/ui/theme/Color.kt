package com.heckofanapp.weather.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Application

// Background Gradients – Dark
internal val ClearSkyDayDarkStart = Color(0xff08579c)
internal val ClearSkyDayDarkEnd = Color(0xff04008e)
internal val ClearSkyNightDarkStart = Color(0xff01023d)
internal val ClearSkyNightDarkEnd = Color(0xff162155)
internal val FogHazeDarkStart = Color(0xff191209)
internal val FogHazeDarkEnd = Color(0xff352603)
internal val MostlyClearPartlyCloudyDarkStart = Color(0xff404558)
internal val MostlyClearPartlyCloudyDarkEnd = Color(0xff262c3d)
internal val OvercastDarkStart = Color(0xff2f2f34)
internal val OvercastDarkEnd = Color(0xff202537)
internal val RainDarkStart = Color(0xff242429)
internal val RainDarkEnd = Color(0xff1e2c3a)
internal val SnowDarkStart = Color(0xff171717)
internal val SnowDarkEnd = Color(0xff202537)
internal val ThunderstormDarkStart = Color(0xff15021a)
internal val ThunderstormDarkEnd = Color(0xff4c2858)

// Background Gradients – Light
internal val ClearSkyDayLightStart = Color(0xff9dceff)
internal val ClearSkyDayLightEnd = Color(0xffcee5ff)
internal val ClearSkyNightLightStart = Color(0xff969eeb)
internal val ClearSkyNightLightEnd = Color(0xffe4dfff)
internal val FogHazeLightStart = Color(0xfffacb8e)
internal val FogHazeLightEnd = Color(0xfffff4db)
internal val MostlyClearPartlyCloudyLightStart = Color(0xffc6d3e4)
internal val MostlyClearPartlyCloudyLightEnd = Color(0xffd5e4f7)
internal val OvercastLightStart = Color(0xffacacad)
internal val OvercastLightEnd = Color(0xffd5e4f7)
internal val RainLightStart = Color(0xffaab8ca)
internal val RainLightEnd = Color(0xffc4d3e5)
internal val SnowLightStart = Color(0xffacacad)
internal val SnowLightEnd = Color(0xffffffff)
internal val ThunderstormLightStart = Color(0xffaab8ca)
internal val ThunderstormLightEnd = Color(0xfff2c9ff)

// Material
internal val Amber800 = Color(0xfff9a825)
internal val Blue400 = Color(0xff42a5f5)
internal val Blue600 = Color(0xff1e88e5)
internal val Blue800 = Color(0xff1565c0)
internal val BlueLight100 = Color(0xffb3e5fc)
internal val BlueLight300 = Color(0xff4fc3f7)
internal val Green400 = Color(0xff66bb6a)
internal val Green600 = Color(0xff43a047)
internal val Grey300 = Color(0xffe0e0e0)
internal val Orange600 = Color(0xfffb8c00)
internal val Pink900 = Color(0xff880e4f)
internal val Purple800 = Color(0xff6a1b9a)
internal val Purple900 = Color(0xff4a148c)
internal val Red800 = Color(0xffc62828)
internal val RedAlt700 = Color(0xffd50000)
internal val Yellow600 = Color(0xfffdd835)

val ColorScheme.onSurfaceDim: Color
    get() = onSurface.copy(
        alpha = 0.90f,
    )
