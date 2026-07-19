package com.heckofanapp.weather.feature.settings.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.core.model.weather.toName
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.ui.components.tiles.DialogOption
import com.heckofanapp.weather.feature.settings.SettingsScreenViewModel

@Composable
fun UnitsScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val units by viewModel.weatherUnits.collectAsStateWithLifecycle()
    val currentUnits = units

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(navController)
        },
        title = stringResource(R.string.setting_units),
    ) { paddingValues ->
        if (currentUnits != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = rememberScrollState(),
                    )
                    .padding(
                        paddingValues = paddingValues,
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 10.dp,
                ),
            ) {
                SettingSection(
                    tiles = listOf(
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_straighten_24,
                                )
                            },
                            onOptionSelected = {
                                viewModel.updateDistanceUnit(
                                    distanceUnit = DistanceUnit.valueOf(it),
                                )
                            },
                            options = listOf(
                                DialogOption(
                                    label = DistanceUnit.KM.toName(
                                        context = context,
                                    ),
                                    value = DistanceUnit.KM.toString(),
                                ),
                                DialogOption(
                                    label = DistanceUnit.M.toName(
                                        context = context
                                    ),
                                    value = DistanceUnit.M.toString(),
                                ),
                                DialogOption(
                                    label = DistanceUnit.MI.toName(
                                        context = context
                                    ),
                                    value = DistanceUnit.MI.toString(),
                                ),
                            ),
                            selectedOption = currentUnits.distance.toString(),
                            title = stringResource(R.string.setting_distance_unit),
                        ),
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_water_drop_24,
                                )
                            },
                            onOptionSelected = {
                                viewModel.updatePrecipitationUnit(
                                    precipitationUnit = PrecipitationUnit.valueOf(it),
                                )
                            },
                            options = listOf(
                                DialogOption(
                                    label = PrecipitationUnit.CM.toName(
                                        context = context,
                                    ),
                                    value = PrecipitationUnit.CM.toString(),
                                ),
                                DialogOption(
                                    label = PrecipitationUnit.IN.toName(
                                        context = context,
                                    ),
                                    value = PrecipitationUnit.IN.toString(),
                                ),
                                DialogOption(
                                    label = PrecipitationUnit.MM.toName(
                                        context = context,
                                    ),
                                    value = PrecipitationUnit.MM.toString(),
                                )
                            ),
                            selectedOption = currentUnits.precipitation.toString(),
                            title = stringResource(R.string.setting_precipitation_unit),
                        ),
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_compress_24,
                                )
                            },
                            onOptionSelected = {
                                viewModel.updatePressureUnit(
                                    PressureUnit.valueOf(it)
                                )
                            },
                            options = listOf(
                                DialogOption(
                                    label = PressureUnit.HPA.toName(
                                        context = context,
                                    ),
                                    value = PressureUnit.HPA.toString(),
                                ),
                                DialogOption(
                                    label = PressureUnit.INHG.toName(
                                        context = context,
                                    ),
                                    value = PressureUnit.INHG.toString(),
                                ),
                                DialogOption(
                                    label = PressureUnit.MMHG.toName(
                                        context = context,
                                    ),
                                    value = PressureUnit.MMHG.toString(),
                                ),
                            ),
                            selectedOption = currentUnits.pressure.toString(),
                            title = stringResource(R.string.setting_pressure_unit),
                        ),
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_air_24,
                                )
                            },
                            onOptionSelected = {
                                viewModel.updateWindUnit(
                                    WindUnit.valueOf(
                                        value = it,
                                    )
                                )
                            },
                            options = listOf(
                                DialogOption(
                                    label = WindUnit.BFT.toName(
                                        context = context
                                    ),
                                    value = WindUnit.BFT.toString(),
                                ),
                                DialogOption(
                                    label = WindUnit.KT.toName(
                                        context = context,
                                    ),
                                    value = WindUnit.KT.toString(),
                                ),
                                DialogOption(
                                    label = WindUnit.KPH.toName(
                                        context = context
                                    ),
                                    value = WindUnit.KPH.toString(),
                                ),
                                DialogOption(
                                    label = WindUnit.MPS.toName(
                                        context = context
                                    ),
                                    value = WindUnit.MPS.toString(),
                                ),
                                DialogOption(
                                    label = WindUnit.MPH.toName(
                                        context = context
                                    ),
                                    value = WindUnit.MPH.toString(),
                                ),
                            ),
                            selectedOption = currentUnits.speed.toString(),
                            title = stringResource(R.string.setting_wind_speed_unit),
                        ),
                        SettingTile.DialogOptionTile(
                            leading = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_device_thermostat_24,
                                )
                            },
                            onOptionSelected = {
                                viewModel.updateTemperatureUnit(
                                    TemperatureUnit.valueOf(
                                        value = it.uppercase(),
                                    )
                                )
                            },
                            options = listOf(
                                DialogOption(
                                    label = TemperatureUnit.CELSIUS.toName(
                                        context = context
                                    ),
                                    value = TemperatureUnit.CELSIUS.toString(),
                                ),
                                DialogOption(
                                    label = TemperatureUnit.FAHRENHEIT.toName(
                                        context = context
                                    ),
                                    value = TemperatureUnit.FAHRENHEIT.toString(),
                                ),
                            ),
                            selectedOption = currentUnits.temperature.toString(),
                            title = stringResource(R.string.setting_temperature_unit),
                        ),
                    )
                )
            }
        }
    }
}
