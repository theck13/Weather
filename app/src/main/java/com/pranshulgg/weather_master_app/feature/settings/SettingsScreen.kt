package com.pranshulgg.weather_master_app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.navigation.NavigationRoutes
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale

@Composable
fun SettingsScreen(
    navController: NavController,
) {
    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(navController)
        },
        title = stringResource(R.string.settings),
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
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_appearance_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_format_paint_24,
                            )
                        },
                        onClick = {
                            navController.navigate(NavigationRoutes.APPEARANCE)
                        },
                        title = stringResource(R.string.setting_appearance),
                    ),
                ),
            )

            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        description = getCurrentAppLocale().displayName,
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_language_24,
                            )
                        },
                        onClick = {
                            navController.navigate(NavigationRoutes.LANGUAGE)
                        },
                        title = stringResource(R.string.setting_language),
                    ),
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_background_updates_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_sync_24,
                            )
                        },
                        onClick = {
                            navController.navigate(NavigationRoutes.BACKGROUND_UPDATES)
                        },
                        title = stringResource(R.string.setting_background_updates),
                    ),
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_weather_sources_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_nest_farsight_weather_24,
                            )
                        },
                        onClick = {
                            navController.navigate(NavigationRoutes.SOURCES)
                        },
                        title = stringResource(R.string.weather_sources),
                    ),
                )
            )
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_about_app_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_info_24,
                            )
                        },
                        onClick = {
                            navController.navigate(NavigationRoutes.ABOUT)
                        },
                        title = stringResource(R.string.setting_about_app),
                    ),
                )
            )

            Gap(
                vertical = 10.dp,
            )
        }
    }
}
