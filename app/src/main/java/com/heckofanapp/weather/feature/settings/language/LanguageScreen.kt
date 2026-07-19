package com.heckofanapp.weather.feature.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.prefs.PreferencesHelper
import com.heckofanapp.weather.core.ui.components.AvatarCheck
import com.heckofanapp.weather.core.ui.components.AvatarMonogram
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.utils.locale.getAppLocalLocales
import com.heckofanapp.weather.core.utils.locale.getCurrentAppLocale

@Composable
fun LanguageScreen(
    navController: NavController,
) {
    val currentAppLocale =
        remember {
            mutableStateOf(
                getCurrentAppLocale().toLanguageTag()
            )
        }
    val isSystemLanguage = PreferencesHelper.getBool("isSystemLanguage") ?: true
    val languageList = getAppLocalLocales()
    val uriHandler = LocalUriHandler.current

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.setting_language),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState()
                )
                .padding(
                    paddingValues = paddingValues,
                )
                .padding(
                    bottom = 10.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = 10.dp,
            ),
        ) {
            SettingSection(
                tiles = languageList.map {
                    val selected = if (isSystemLanguage) it.value == "sys" else it.value == currentAppLocale.value

                    SettingTile.ActionTile(
                        colorDesc =
                            if (selected) {
                                MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                    0.80f
                                )
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        description = it.nativeName,
                        leading = {
                            if (selected) {
                                AvatarCheck(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                )
                            } else {
                                AvatarMonogram(
                                    text = it.code,
                                )
                            }
                        },
                        onClick = {
                            currentAppLocale.value = it.value

                            if (it.value == "sys") {
                                setSystemLanguage()
                            } else {
                                setLanguage(it.value)
                            }
                        },
                        selected = selected,
                        title = it.name,
                    )
                },
            )

            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        description = stringResource(R.string.setting_translate_app_secondary),
                        onClick = {
                            uriHandler.openUri("https://crowdin.com/project/weathermaster")
                        },
                        title = stringResource(R.string.setting_translate_app),
                        trailing = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_open_in_new_24,
                            )
                        },
                    ),
                ),
            )
        }
    }
}

private fun setLanguage(
    languageCode: String,
) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    PreferencesHelper.setBool("isSystemLanguage", false)
}

private fun setSystemLanguage() {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    PreferencesHelper.setBool("isSystemLanguage", true)
}
