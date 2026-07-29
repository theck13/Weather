package com.heckofanapp.weather.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault
import com.heckofanapp.weather.core.model.domain.airquality.AirQuality
import com.heckofanapp.weather.core.model.weather.air.AirQualityIndexStandard
import com.heckofanapp.weather.core.ui.barColorsAirQuality
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.ui.theme.onSurfaceDim
import com.heckofanapp.weather.feature.shared.components.Header

@Composable
fun AirQualityBlock(
    airQuality: AirQuality?,
    airQualityIndex: Int,
    context: Context,
    standard: AirQualityIndexStandard,
    onClickBlock: () -> Unit,
) {
    val airQualityBar = airQuality!!.getAqiBarValue(airQualityIndex, standard)
    val category = airQuality.getAqiCategory(airQualityIndex, standard)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        onClick = onClickBlock,
        shadowElevation = ShadowElevation.level2,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(
                    ratio = 1.00f,
                )
                .fillMaxSize(),
        ) {
            Column(
                Modifier
                    .align(
                        alignment = Alignment.BottomEnd,
                    )
                    .padding(
                        bottom = SpaceDefault,
                        end = SpaceDefault,
                        start = SpaceDefault,
                    ),
            ) {
                Header(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceDim,
                    icon = R.drawable.ic_airwave_24,
                    padding = PaddingValues(
                        end = 12.dp,
                        start = 12.dp,
                        top = SpaceDefault,
                    ),
                    text = stringResource(R.string.weather_air),
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(
                            weight = 1.00f,
                        )
                        .wrapContentHeight(
                            align = Alignment.CenterVertically,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayMedium,
                    text = airQualityIndex.toString(),
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    text = category.toName(
                        context = context,
                    ),
                    textAlign = TextAlign.Center,
                )

                Gap(
                    vertical = SpaceDefault / 2,
                )

                LinearProgressIndicator(
                    modifier = Modifier.height(
                        height = SpaceDefault / 2,
                    ),
                    color = barColorsAirQuality(
                        category = category,
                    ),
                    progress = {
                        airQualityBar
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
        }
    }
}
