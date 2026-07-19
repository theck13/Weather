package com.heckofanapp.weather.core.model.astro

import com.heckofanapp.weather.R

data class SunTimings(
    val time: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val dawn: Long?,
    val dusk: Long?
)

data class MoonTimings(
    val time: Long,
    val moonrise: Long?,
    val moonset: Long?,
    val phase: MoonPhase
)

enum class MoonPhase(
    val description: Int,
    val displayName: Int,
    val icon: Int,
    val image: Int,
) {
    NEW_MOON(
        description = R.string.moon_phase_new_moon_description,
        displayName = R.string.moon_phase_new_moon,
        icon = R.drawable.ic_rise_set_moon_1_new,
        image = R.drawable.ic_phase_moon_1_new,
    ),
    WAXING_CRESCENT(
        description = R.string.moon_phase_waxing_crescent_description,
        displayName = R.string.moon_phase_waxing_crescent,
        icon = R.drawable.ic_rise_set_moon_2_crescent_waxing,
        image = R.drawable.ic_phase_moon_2_crescent_waxing,
    ),
    FIRST_QUARTER(
        description = R.string.moon_phase_first_quarter_description,
        displayName = R.string.moon_phase_first_quarter,
        icon = R.drawable.ic_rise_set_moon_3_quarter_first,
        image = R.drawable.ic_phase_moon_3_quarter_first,
    ),
    WAXING_GIBBOUS(
        description = R.string.moon_phase_waxing_gibbous_description,
        displayName = R.string.moon_phase_waxing_gibbous,
        icon = R.drawable.ic_rise_set_moon_4_gibbous_waxing,
        image = R.drawable.ic_phase_moon_4_gibbous_waxing,
    ),
    FULL_MOON(
        description = R.string.moon_phase_full_moon_description,
        displayName = R.string.moon_phase_full_moon,
        icon = R.drawable.ic_rise_set_moon_5_full,
        image = R.drawable.ic_phase_moon_5_full,
    ),
    WANING_GIBBOUS(
        description = R.string.moon_phase_waning_gibbous_description,
        displayName = R.string.moon_phase_waning_gibbous,
        icon = R.drawable.ic_rise_set_moon_6_gibbous_waning,
        image = R.drawable.ic_phase_moon_6_gibbous_waning,
    ),
    LAST_QUARTER(
        description = R.string.moon_phase_last_quarter_description,
        displayName = R.string.moon_phase_last_quarter,
        icon = R.drawable.ic_rise_set_moon_7_quarter_third,
        image = R.drawable.ic_phase_moon_7_quarter_third,
    ),
    WANING_CRESCENT(
        description = R.string.moon_phase_waning_crescent_description,
        displayName = R.string.moon_phase_waning_crescent,
        icon = R.drawable.ic_rise_set_moon_8_crescent_waning,
        image = R.drawable.ic_phase_moon_8_crescent_waning,
    ),
}

fun getMoonPhase(phase: Double): MoonPhase {
    return when {
        phase < -135 -> MoonPhase.NEW_MOON
        phase < -90 -> MoonPhase.WAXING_CRESCENT
        phase < -45 -> MoonPhase.FIRST_QUARTER
        phase < 0 -> MoonPhase.WAXING_GIBBOUS
        phase < 45 -> MoonPhase.FULL_MOON
        phase < 90 -> MoonPhase.WANING_GIBBOUS
        phase < 135 -> MoonPhase.LAST_QUARTER
        else -> MoonPhase.WANING_CRESCENT
    }
}