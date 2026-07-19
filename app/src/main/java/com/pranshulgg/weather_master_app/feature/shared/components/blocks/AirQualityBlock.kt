package com.pranshulgg.weather_master_app.feature.shared.components.blocks

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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityIndexStandard
import com.pranshulgg.weather_master_app.core.ui.barColorsAirQuality
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.onSurfaceDim
import com.pranshulgg.weather_master_app.feature.shared.components.Header

@Composable
fun AirQualityBlock(
    airQuality: AirQuality?,
    context: Context,
    standard: AirQualityIndexStandard,
    onClickBlock: () -> Unit,
) {
    val aqi = airQuality!!.getAqi(standard)
    val aqiBar = airQuality.getAqiBarValue(aqi, standard)
    val category = airQuality.getAqiCategory(aqi, standard)

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
                        bottom = 16.dp,
                        end = 16.dp,
                        start = 16.dp,
                    ),
            ) {
                Header(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceDim,
                    icon = R.drawable.ic_airwave_24,
                    padding = PaddingValues(
                        end = 12.dp,
                        start = 12.dp,
                        top = 16.dp,
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
                    text = aqi.toString(),
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
                    vertical = 8.dp,
                )

                LinearProgressIndicator(
                    modifier = Modifier.height(
                        height = 8.dp,
                    ),
                    color = barColorsAirQuality(
                        category = category,
                    ),
                    progress = {
                        aqiBar
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
        }
    }
}
