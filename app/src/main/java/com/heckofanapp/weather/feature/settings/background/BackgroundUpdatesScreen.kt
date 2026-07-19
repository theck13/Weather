package com.heckofanapp.weather.feature.settings.background

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.ui.components.tiles.DialogOption
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager

@Composable
fun BackgroundUpdatesScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val preferences = LocalAppPrefs.current
    val uriHandler = LocalUriHandler.current
    val viewModel: BackgroundUpdatesViewModel = hiltViewModel()

    var isNotificationPermissionGranted by remember { mutableStateOf(context.isNotificationPermissionGranted()) }

    val intervals = mapOf(
        15 to stringResource(R.string.time_minutes, "15"),
        30 to stringResource(R.string.time_minutes, "30"),
        60 to stringResource(R.string.time_hour, "1"),
        120 to stringResource(R.string.time_hours, "2"),
        240 to stringResource(R.string.time_hours, "4"),
        360 to stringResource(R.string.time_hours, "6"),
        720 to stringResource(R.string.time_hours, "12"),
    )
    val intervalOptions = intervals.map { DialogOption(
        label = it.value,
        value = it.key.toString(),
    ) }
    val requestPermission = rememberNotificationPermissionLauncher(
        onDenied = {
            SnackbarManager.show(
                message = R.string.setting_notification_permission_req,
            )
        },
        onGranted = {
            isNotificationPermissionGranted = true
        },
    )

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(navController)
        },
        title = stringResource(R.string.setting_background_updates),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
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
            AnimatedVisibility(
                visible = isNotificationPermissionGranted.not(),
            ) {
                SettingSection(
                    tiles = listOf(
                        SettingTile.ActionTile(
                            colorDesc = MaterialTheme.colorScheme.onErrorContainer,
                            danger = true,
                            description = stringResource(R.string.setting_notification_permission_secondary),
                            onClick = {
                                requestPermission()
                            },
                            title = stringResource(R.string.setting_notification),
                        ),
                    ),
                )
            }

            SettingSection(
                primarySwitch = true,
                tiles = listOf(
                    SettingTile.SwitchTile(
                        checked = preferences.backgroundUpdatesEnabled,
                        enabled = isNotificationPermissionGranted,
                        onCheckedChange = {
                            preferences.setBackgroundUpdates(it)

                            if (it) {
                                viewModel.scheduleWeatherUpdates(
                                    minutes = preferences.backgroundUpdatesInterval,
                                )
                            } else {
                                viewModel.disableWeatherUpdates()
                            }
                        },
                        title = stringResource(R.string.setting_background_updates),
                    )
                )
            )

            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_disable_battery_opt_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_sync_problem_24,
                            )
                        },
                        onClick = {
                            BatteryOptimizationHelper.requestDisableBatteryOptimization(context)
                        },
                        title = stringResource(R.string.setting_disable_battery_opt_title),
                    ),
                    SettingTile.DialogOptionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_update_24,
                            )
                        },
                        onOptionSelected = {
                            preferences.setBackgroundUpdatesInterval(it.toInt())
                        },
                        options = intervalOptions,
                        selectedOption = preferences.backgroundUpdatesInterval.toString(),
                        title = stringResource(R.string.setting_update_interval),
                    ),
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_dontkillmyapp_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_help_24,
                            )
                        },
                        onClick = {
                            uriHandler.openUri("https://dontkillmyapp.com/")
                        },
                        title = stringResource(R.string.setting_dontkillmyapp_title),
                        trailing = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_open_in_new_24,
                            )
                        },
                    ),
                )
            )

            Gap(
                vertical = 10.dp,
            )
        }
    }
}
