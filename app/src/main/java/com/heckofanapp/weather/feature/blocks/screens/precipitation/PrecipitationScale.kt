package com.heckofanapp.weather.feature.blocks.screens.precipitation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.toName
import com.heckofanapp.weather.core.ui.barColorsRain
import com.heckofanapp.weather.core.ui.barColorsSnow
import com.heckofanapp.weather.core.utils.formatters.formatLocalizedNumber
import com.heckofanapp.weather.core.utils.locale.getCurrentAppLocale

internal data class PrecipitationLevel(
    val color: Color,
    val description: String,
    val headline: String,
    val scale: String,
)

/**
 * Bounds are expressed in base unit hourly bars use; millimetres for rain, centimetres for snow.
 * Top band closes at matching home-tile fill maximum (4 in for rain, 24 in for snow); bars, this
 * legend, and block fill all share one scale.  [colorFor] resolves each band to same color bars use
 * for a value inside it.
 */
private data class PrecipitationBand(
    val descriptionRes: Int,
    val headlineRes: Int,
    val lower: Double,
    val representative: Double,
    val upper: Double?,
)

// Rain: finite bands fill 0 → 101.6 mm (4 in, RainBlock fill maximum); Extreme is above maximum.
private val rainBands = listOf(
    PrecipitationBand(R.string.rain_scale_description_1, R.string.precipitation_scale_1, 0.0, 2.0, 5.08),
    PrecipitationBand(R.string.rain_scale_description_2, R.string.precipitation_scale_2, 5.08, 15.0, 25.4),
    PrecipitationBand(R.string.rain_scale_description_3, R.string.precipitation_scale_3, 25.4, 38.0, 50.8),
    PrecipitationBand(R.string.rain_scale_description_4, R.string.precipitation_scale_4, 50.8, 63.0, 76.2),
    PrecipitationBand(R.string.rain_scale_description_5, R.string.precipitation_scale_5, 76.2, 90.0, 101.6),
    PrecipitationBand(R.string.rain_scale_description_6, R.string.precipitation_scale_6, 101.6, 120.0, null),
)

// Snow: finite bands fill 0 → 60.96 cm (24 in, SnowBlock fill maximum); Extreme is above maximum.
private val snowBands = listOf(
    PrecipitationBand(R.string.snow_scale_description_1, R.string.precipitation_scale_1, 0.0, 1.2, 2.54),
    PrecipitationBand(R.string.snow_scale_description_2, R.string.precipitation_scale_2, 2.54, 5.0, 7.62),
    PrecipitationBand(R.string.snow_scale_description_3, R.string.precipitation_scale_3, 7.62, 11.0, 15.24),
    PrecipitationBand(R.string.snow_scale_description_4, R.string.precipitation_scale_4, 15.24, 22.0, 30.48),
    PrecipitationBand(R.string.snow_scale_description_5, R.string.precipitation_scale_5, 30.48, 45.0, 60.96),
    PrecipitationBand(R.string.snow_scale_description_6, R.string.precipitation_scale_6, 60.96, 70.0, null),
)

@Composable
internal fun getRainScale(
    unit: PrecipitationUnit,
): List<PrecipitationLevel> = getPrecipitationScale(
    bands = rainBands,
    colorFor = ::barColorsRain,
    unit = unit,
    unitBase = PrecipitationUnit.MM,
)

@Composable
internal fun getSnowScale(
    unit: PrecipitationUnit,
): List<PrecipitationLevel> = getPrecipitationScale(
    bands = snowBands,
    colorFor = ::barColorsSnow,
    unit = unit,
    unitBase = PrecipitationUnit.CM,
)

@Composable
private fun getPrecipitationScale(
    bands: List<PrecipitationBand>,
    colorFor: (Double) -> Color,
    unit: PrecipitationUnit,
    unitBase: PrecipitationUnit,
): List<PrecipitationLevel> {
    val context = LocalContext.current
    val locale = getCurrentAppLocale()
    val suffix = unit.toName(
        context = context,
        inShort = true,
    )
    val decimalPlaces = if (unit == PrecipitationUnit.IN) 2 else 1

    return bands.map { band ->
        val lower = formatLocalizedNumber(
            decimalPlaces = decimalPlaces,
            locale = locale,
            number = unitBase.convert(
                from = band.lower,
                to = unit,
            ) ?: 0.0,
        )
        val upper = band.upper?.let {
            formatLocalizedNumber(
                decimalPlaces = decimalPlaces,
                locale = locale,
                number = unitBase.convert(
                    from = it,
                    to = unit,
                ) ?: 0.0,
            )
        }

        PrecipitationLevel(
            color = colorFor(band.representative),
            description = stringResource(band.descriptionRes),
            headline = stringResource(band.headlineRes),
            scale = if (upper == null) "≥ $lower $suffix" else "$lower - $upper $suffix",
        )
    }
}
