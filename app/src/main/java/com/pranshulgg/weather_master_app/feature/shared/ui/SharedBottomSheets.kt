package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesForCountry
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesGlobal
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.Symbol

object SharedBottomSheet {
    @OptIn(
        ExperimentalMaterial3Api::class,
    )
    @Composable
    fun WeatherSourcesForLocationSheet(
        countryCode: String?,
        isEditing: Boolean = false,
        onDismiss: () -> Unit,
        onSave: (WeatherSource) -> Unit,
        selectedSource: WeatherSource = WeatherSource.OPEN,
        sheetState: SheetState,
        show: Boolean,
    ) {
        if (show) {
            val recommendedSources = getWeatherSourcesForCountry(countryCode?.uppercase())

            var currentSelectedSource by remember(
                show,
                selectedSource
            ) {
                mutableStateOf(if (recommendedSources.isNotEmpty() && !isEditing) recommendedSources[0] else selectedSource)
            }
            val globalSources = getWeatherSourcesGlobal()

            ActionBottomSheet(
                cancelText = stringResource(R.string.action_cancel),
                confirmText = stringResource(R.string.action_save),
                onCancel = {
                    onDismiss()
                },
                onConfirm = {
                    onSave(currentSelectedSource)
                },
                sheetState = sheetState,
            ) {
                if (recommendedSources.isNotEmpty()) {
                    SettingSection(
                        title = stringResource(R.string.recommended_sources),
                        tiles = recommendedSources.map { source ->
                            val isSelected = currentSelectedSource == source

                            SettingTile.ActionTile(
                                onClick = {
                                    currentSelectedSource = source
                                },
                                selected = isSelected,
                                title = source.displayName,
                                trailing = {
                                    if (isSelected) Symbol(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        icon = R.drawable.ic_check_24,
                                    )
                                },
                            )
                        },
                    )
                }

                Gap(
                    vertical = 8.dp,
                )

                SettingSection(
                    title = stringResource(R.string.global_sources),
                    tiles = globalSources.map { source ->
                        val isSelected = currentSelectedSource == source

                        SettingTile.ActionTile(
                            onClick = {
                                currentSelectedSource = source
                            },
                            title = source.displayName,
                            trailing = {
                                if (isSelected) Symbol(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    icon = R.drawable.ic_check_24,
                                )
                            },
                            selected = isSelected,
                        )
                    }
                )
            }
        }
    }
}
