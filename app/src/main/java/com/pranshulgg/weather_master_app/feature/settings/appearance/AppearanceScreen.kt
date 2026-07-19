package com.pranshulgg.weather_master_app.feature.settings.appearance

import android.os.Build
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
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.navigation.NavigationRoutes
import com.pranshulgg.weather_master_app.feature.settings.appearance.components.ColorPickerItem

@Composable
fun AppearanceScreen(
    navController: NavController,
) {
    val isAndroid12Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val preferences = LocalAppPrefs.current

    val appFontOptions = listOf(
        DialogOption(
            label = "Google Sans Flex",
            value = "google_sans_flex",
        ),
        DialogOption(
            label = stringResource(R.string.setting_system_font),
            value = "system",
        ),
    )
    val appThemeOptions = listOf(
        DialogOption(
            label = stringResource(R.string.setting_dark_theme),
            value = "Dark",
        ),
        DialogOption(
            label = stringResource(R.string.setting_light_theme),
            value = "Light",
        ),
        DialogOption(
            label = stringResource(R.string.setting_system_theme),
            value = "System",
        ),
    )
    val timeFormatOptions = listOf(
        DialogOption(
            label = stringResource(R.string.setting_time_format_12),
            value = "12",
        ),
        DialogOption(
            label = stringResource(R.string.setting_time_format_24),
            value = "24",
        ),
    )

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.setting_appearance),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingSection(
                title = stringResource(R.string.setting_app_looks),
                tiles = listOf(
                    SettingTile.SwitchTile(
                        description = stringResource(R.string.setting_custom_color_secondary),
                        checked = preferences.isCustomTheme,
                        enabled = !preferences.isDynamicTheme,
                        leading = {
                            if (preferences.isCustomTheme) {
                                ColorPickerItem()
                            } else {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_brush_24,
                                )
                            }
                        },
                        onCheckedChange = { checked ->
                            preferences.setIsCustomTheme(checked)

                            if (checked.not()) {
                                preferences.setCustomThemeColor("#33b5e5")
                            }
                        },
                        title = stringResource(R.string.setting_custom_color),
                    ),
                    SettingTile.SwitchTile(
                        checked = preferences.isDynamicTheme,
                        description = stringResource(R.string.setting_dynamic_colors_secondary),
                        enabled = isAndroid12Plus && !preferences.isCustomTheme,
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_photo_24,
                            )
                        },
                        onCheckedChange = { checked ->
                            preferences.setIsDynamicColor(checked)
                        },
                        title = stringResource(R.string.setting_dynamic_colors),
                    ),
                    SettingTile.DialogOptionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_font_download_24,
                            )
                        },
                        onOptionSelected = {
                            if (it == "google_sans_flex") {
                                preferences.setIsGoogleSansFlex(true)
                            } else {
                                preferences.setIsGoogleSansFlex(false)
                            }
//                            recreate(activity)
                        },
                        options = appFontOptions,
                        selectedOption = if (preferences.isGoogleSansFlex) "google_sans_flex" else "system",
                        title = stringResource(R.string.setting_app_font),
                    ),
                    SettingTile.DialogOptionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_palette_24,
                            )
                        },
                        onOptionSelected = {
                            preferences.setAppTheme(it)
                        },
                        options = appThemeOptions,
                        selectedOption = preferences.appTheme,
                        title = stringResource(R.string.setting_app_theme),
                    ),
                    SettingTile.DialogOptionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_schedule_48,
                            )
                        },
                        onOptionSelected = {
                            preferences.setTimeFormat(it)
                        },
                        options = timeFormatOptions,
                        selectedOption = preferences.timeFormat,
                        title = stringResource(R.string.setting_time_format),
                    ),
                ),
            )

            SettingSection(
                title = "Weather",
                tiles = listOf(
                    SettingTile.SwitchTile(
                        checked = preferences.isWeatherBasedTheme,
                        description = stringResource(R.string.setting_weather_based_theme_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_gradient_24,
                            )
                        },
                        onCheckedChange = { checked ->
                            preferences.setIsWeatherBasedTheme(checked)
                        },
                        title = stringResource(R.string.setting_weather_based_theme),
                    ),
                    SettingTile.SwitchTile(
                        checked = preferences.isShowWeatherAnimations,
                        description = stringResource(R.string.setting_weather_animations_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_animation_24,
                            )
                        },
                        onCheckedChange = { checked ->
                            preferences.setIsShowWeatherAnimations(checked)
                        },
                        title = stringResource(R.string.setting_weather_animations),
                    ),
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_units_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_tune_24,
                            )
                        },
                        onClick = {
                            navController.navigate(NavigationRoutes.UNITS)
                        },
                        title = stringResource(R.string.setting_units),
                    ),
                ),
            )

            SettingSection(
                title = "Layout",
                tiles = listOf(
                    SettingTile.SwitchTile(
                        checked = preferences.isFroggyLayout,
                        description = stringResource(R.string.setting_froggy_layout_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_froggy_monochrome_24,
                            )
                        },
                        onCheckedChange = { checked ->
                            preferences.setIsFroggyLayout(checked)
                        },
                        title = stringResource(R.string.setting_froggy_layout),
                    ),
                    SettingTile.SwitchTile(
                        checked = preferences.isShowSummary,
                        description = stringResource(R.string.setting_day_summary_secondary),
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_article_24,
                            )
                        },
                        onCheckedChange = { checked ->
                            preferences.setIsShowSummary(checked)
                        },
                        title = stringResource(R.string.setting_day_summary),
                    ),
                ),
            )

            Gap(
                vertical = 10.dp,
            )
        }
    }
}
