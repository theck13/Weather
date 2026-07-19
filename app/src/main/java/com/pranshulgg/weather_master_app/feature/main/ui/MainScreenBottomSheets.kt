package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.feature.main.MainScreenViewModel

object MainScreenBottomSheets {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WeatherSourcesInfoForLocationSheet(
        location: Location?,
        sheetState: SheetState,
        viewModel: MainScreenViewModel,
    ) {
        val airQualityUrl = "https://open-meteo.com/en/docs/air-quality-api"
        val uiState = viewModel.uiState.value
        val uriHandler = LocalUriHandler.current

        if (uiState.isWeatherSourcesInfoForLocationSheetOpen) {
            ActionBottomSheet(
                cancelText = stringResource(R.string.action_ok),
                hideConfirmBtn = true,
                onCancel = viewModel::hideWeatherSourcesInfoForLocationSheet,
                onConfirm = {},
                sheetState = sheetState,
            ) {
                SettingSection(
                    title = stringResource(R.string.source),
                    tiles = listOf(
                        SettingTile.ActionTile(
                            description = location!!.source.displayLink,
                            onClick = {
                                uriHandler.openUri(location.source.displayLink)
                            },
                            title = location.source.fullName,
                            trailing = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_open_in_new_24,
                                )
                            },
                        )
                    ),
                )

                Gap(
                    vertical = 8.dp,
                )

                SettingSection(
                    title = stringResource(R.string.weather_air),
                    tiles = listOf(
                        SettingTile.ActionTile(
                            description = airQualityUrl,
                            title = WeatherSource.OPEN.displayName,
                            onClick = {
                                uriHandler.openUri(airQualityUrl)
                            },
                            trailing = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_open_in_new_24,
                                )
                            },
                        )
                    ),
                )
            }
        }
    }
}
