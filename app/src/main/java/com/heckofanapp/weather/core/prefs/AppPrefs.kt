package com.heckofanapp.weather.core.prefs

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.heckofanapp.weather.DEFAULT_APP_THEME
import com.heckofanapp.weather.DEFAULT_BACKGROUND_UPDATES_ENABLED
import com.heckofanapp.weather.DEFAULT_BACKGROUND_UPDATES_INTERVAL
import com.heckofanapp.weather.DEFAULT_CUSTOM_THEME_COLOR
import com.heckofanapp.weather.DEFAULT_IS_CUSTOM_THEME
import com.heckofanapp.weather.DEFAULT_IS_DYNAMIC_THEME
import com.heckofanapp.weather.DEFAULT_IS_FROGGY_LAYOUT
import com.heckofanapp.weather.DEFAULT_IS_GOOGLE_SANS_FLEX
import com.heckofanapp.weather.DEFAULT_IS_SHOW_SUMMARY
import com.heckofanapp.weather.DEFAULT_IS_SHOW_WEATHER_ANIMATIONS
import com.heckofanapp.weather.DEFAULT_IS_WEATHER_BASED_THEME
import com.heckofanapp.weather.DEFAULT_SEARCH_SOURCE
import com.heckofanapp.weather.DEFAULT_THEME_VARIANT
import com.heckofanapp.weather.DEFAULT_TIME_FORMAT
import com.heckofanapp.weather.KEY_APP_THEME
import com.heckofanapp.weather.KEY_BACKGROUND_UPDATES_ENABLED
import com.heckofanapp.weather.KEY_BACKGROUND_UPDATES_INTERVAL
import com.heckofanapp.weather.KEY_CUSTOM_THEME_COLOR
import com.heckofanapp.weather.KEY_IS_CUSTOM_THEME
import com.heckofanapp.weather.KEY_IS_DYNAMIC_THEME
import com.heckofanapp.weather.KEY_IS_FROGGY_LAYOUT
import com.heckofanapp.weather.KEY_IS_GOOGLE_SANS_FLEX
import com.heckofanapp.weather.KEY_IS_SHOW_SUMMARY
import com.heckofanapp.weather.KEY_IS_SHOW_WEATHER_ANIMATIONS
import com.heckofanapp.weather.KEY_IS_WEATHER_BASED_THEME
import com.heckofanapp.weather.KEY_SEARCH_SOURCE
import com.heckofanapp.weather.KEY_THEME_VARIANT_TYPE
import com.heckofanapp.weather.KEY_TIME_FORMAT
import com.heckofanapp.weather.core.model.sources.SearchSource
import com.heckofanapp.weather.core.ui.theme.ThemeVariant

object AppPrefs {
    private val _appTheme = mutableStateOf(DEFAULT_APP_THEME)
    private val _backgroundUpdatesEnabled = mutableStateOf(DEFAULT_BACKGROUND_UPDATES_ENABLED)
    private val _backgroundUpdatesInterval = mutableIntStateOf(DEFAULT_BACKGROUND_UPDATES_INTERVAL)
    private val _customThemeColor = mutableStateOf(DEFAULT_CUSTOM_THEME_COLOR)
    private val _isCustomTheme = mutableStateOf(DEFAULT_IS_CUSTOM_THEME)
    private val _isDynamicTheme = mutableStateOf(DEFAULT_IS_DYNAMIC_THEME)
    private val _isFroggyLayout = mutableStateOf(DEFAULT_IS_FROGGY_LAYOUT)
    private val _isGoogleSansFlex = mutableStateOf(DEFAULT_IS_GOOGLE_SANS_FLEX)
    private val _isShowSummary = mutableStateOf(DEFAULT_IS_SHOW_SUMMARY)
    private val _isShowWeatherAnimations = mutableStateOf(DEFAULT_IS_SHOW_WEATHER_ANIMATIONS)
    private val _isWeatherBasedTheme = mutableStateOf(DEFAULT_IS_WEATHER_BASED_THEME)
    private val _searchSource = mutableStateOf(DEFAULT_SEARCH_SOURCE)
    private val _themeVariantType = mutableStateOf(DEFAULT_THEME_VARIANT)
    private val _timeFormat = mutableStateOf(DEFAULT_TIME_FORMAT)

    fun initPrefs(context: Context) {
        PreferencesHelper.init(context)

        _appTheme.value = PreferencesHelper.getString(KEY_APP_THEME) ?: DEFAULT_APP_THEME
        _backgroundUpdatesEnabled.value = PreferencesHelper.getBool(KEY_BACKGROUND_UPDATES_ENABLED) ?: DEFAULT_BACKGROUND_UPDATES_ENABLED
        _backgroundUpdatesInterval.intValue = PreferencesHelper.getInt(KEY_BACKGROUND_UPDATES_INTERVAL) ?: DEFAULT_BACKGROUND_UPDATES_INTERVAL
        _customThemeColor.value = PreferencesHelper.getString(KEY_CUSTOM_THEME_COLOR) ?: DEFAULT_CUSTOM_THEME_COLOR
        _isCustomTheme.value = PreferencesHelper.getBool(KEY_IS_CUSTOM_THEME) ?: DEFAULT_IS_CUSTOM_THEME
        _isDynamicTheme.value = PreferencesHelper.getBool(KEY_IS_DYNAMIC_THEME) ?: DEFAULT_IS_DYNAMIC_THEME
        _isFroggyLayout.value = PreferencesHelper.getBool(KEY_IS_FROGGY_LAYOUT) ?: DEFAULT_IS_FROGGY_LAYOUT
        _isGoogleSansFlex.value = PreferencesHelper.getBool(KEY_IS_GOOGLE_SANS_FLEX) ?: DEFAULT_IS_GOOGLE_SANS_FLEX
        _isWeatherBasedTheme.value = PreferencesHelper.getBool(KEY_IS_WEATHER_BASED_THEME) ?: DEFAULT_IS_WEATHER_BASED_THEME
        _isShowSummary.value = PreferencesHelper.getBool(KEY_IS_SHOW_SUMMARY) ?: DEFAULT_IS_SHOW_SUMMARY
        _isShowWeatherAnimations.value = PreferencesHelper.getBool(KEY_IS_SHOW_WEATHER_ANIMATIONS) ?: DEFAULT_IS_SHOW_WEATHER_ANIMATIONS
        _searchSource.value = PreferencesHelper.getString(KEY_SEARCH_SOURCE)
            ?.let { runCatching { SearchSource.valueOf(it) }.getOrNull() }
            ?: DEFAULT_SEARCH_SOURCE
        _themeVariantType.value = PreferencesHelper.getString(KEY_THEME_VARIANT_TYPE)
            ?.let { runCatching { ThemeVariant.valueOf(it) }.getOrNull() }
            ?: DEFAULT_THEME_VARIANT
        _timeFormat.value = PreferencesHelper.getString(KEY_TIME_FORMAT) ?: DEFAULT_TIME_FORMAT
    }

    @Composable
    fun state(): AppPrefsState = AppPrefsState(
        appTheme = _appTheme.value,
        backgroundUpdatesEnabled = _backgroundUpdatesEnabled.value,
        backgroundUpdatesInterval = _backgroundUpdatesInterval.intValue,
        customThemeColor = _customThemeColor.value,
        isCustomTheme = _isCustomTheme.value,
        isDynamicTheme = _isDynamicTheme.value,
        isFroggyLayout = _isFroggyLayout.value,
        isGoogleSansFlex = _isGoogleSansFlex.value,
        isShowSummary = _isShowSummary.value,
        isShowWeatherAnimations = _isShowWeatherAnimations.value,
        isWeatherBasedTheme = _isWeatherBasedTheme.value,
        searchSource = _searchSource.value,
        themeVariant = _themeVariantType.value,
        timeFormat = _timeFormat.value,
        setAppTheme = {
            _appTheme.value = it
            PreferencesHelper.setString(KEY_APP_THEME, it)
        },
        setBackgroundUpdates = {
            _backgroundUpdatesEnabled.value = it
            PreferencesHelper.setBool(KEY_BACKGROUND_UPDATES_ENABLED, it)
        },
        setBackgroundUpdatesInterval = {
            _backgroundUpdatesInterval.intValue = it
            PreferencesHelper.setInt(KEY_BACKGROUND_UPDATES_INTERVAL, it)
        },
        setCustomThemeColor = {
            _customThemeColor.value = it
            PreferencesHelper.setString(KEY_CUSTOM_THEME_COLOR, it)
        },
        setIsCustomTheme = {
            _isCustomTheme.value = it
            PreferencesHelper.setBool(KEY_IS_CUSTOM_THEME, it)
        },
        setIsDynamicColor = {
            _isDynamicTheme.value = it
            PreferencesHelper.setBool(KEY_IS_DYNAMIC_THEME, it)
        },
        setIsFroggyLayout = {
            _isFroggyLayout.value = it
            PreferencesHelper.setBool(KEY_IS_FROGGY_LAYOUT, it)
        },
        setIsGoogleSansFlex = {
            _isGoogleSansFlex.value = it
            PreferencesHelper.setBool(KEY_IS_GOOGLE_SANS_FLEX, it)
        },
        setIsShowSummary = {
            _isShowSummary.value = it
            PreferencesHelper.setBool(KEY_IS_SHOW_SUMMARY, it)
        },
        setIsShowWeatherAnimations = {
            _isShowWeatherAnimations.value = it
            PreferencesHelper.setBool(KEY_IS_SHOW_WEATHER_ANIMATIONS, it)
        },
        setIsWeatherBasedTheme = {
            _isWeatherBasedTheme.value = it
            PreferencesHelper.setBool(KEY_IS_WEATHER_BASED_THEME, it)
        },
        setSearchSource = {
            _searchSource.value = it
            PreferencesHelper.setString(KEY_SEARCH_SOURCE, it.name)
        },
        setThemeVariantType = {
            _themeVariantType.value = it
            PreferencesHelper.setString(KEY_THEME_VARIANT_TYPE, it.name)
        },
        setTimeFormat = {
            _timeFormat.value = it
            PreferencesHelper.setString(KEY_TIME_FORMAT, it)
        },
    )
}
