package com.heckofanapp.weather.widgets

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.heckofanapp.weather.core.prefs.AppPrefs
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.theme.Theme
import com.heckofanapp.weather.core.ui.theme.ThemeVariant
import com.heckofanapp.weather.core.ui.theme.isThemeDark
import com.heckofanapp.weather.data.worker.widgets.WeatherWidgetUpdater
import com.heckofanapp.weather.widgets.card.WidgetCardReceiver
import com.heckofanapp.weather.widgets.card.ui.WidgetCardConfig
import com.heckofanapp.weather.widgets.daily.WidgetDailyReceiver
import com.heckofanapp.weather.widgets.daily.ui.WidgetDailyConfig
import com.heckofanapp.weather.widgets.hourly.WidgetHourlyReceiver
import com.heckofanapp.weather.widgets.hourly.ui.WidgetHourlyConfig
import com.heckofanapp.weather.widgets.transparent.WidgetTransparentReceiver
import com.heckofanapp.weather.widgets.transparent.ui.WidgetTransparentConfig
import kotlinx.coroutines.launch

class ActivityWidgetConfig : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPrefs.initPrefs(this)
        enableEdgeToEdge()

        val widgetId =
            intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID,
            )

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val provider = AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId)?.provider
        setResult(RESULT_CANCELED)
        val updater = WeatherWidgetUpdater(this)
        val onDone: (WidgetConfig) -> Unit = {
            lifecycleScope.launch {

                updater.saveWidgetConfig(
                    config = it,
                    context = this@ActivityWidgetConfig,
                    widgetId = widgetId,
                )

                setResult(
                    RESULT_OK,
                    Intent().apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            widgetId
                        )
                    }
                )

                finish()
            }
        }

        setContent {
            CompositionLocalProvider(
                LocalAppPrefs provides AppPrefs.state(),
            ) {
                Theme(
                    isDark = isThemeDark(),
                    dynamicTheme = true,
                    themeVariant = ThemeVariant.EXPRESSIVE,
                ) {
                    when (provider?.className) {
                        WidgetCardReceiver::class.java.name -> {
                            WidgetCardConfig(
                                onDone = {
                                    onDone(it)
                                },
                            )
                        }

                        WidgetDailyReceiver::class.java.name -> {
                            WidgetDailyConfig(
                                onDone = {
                                    onDone(it)
                                },
                            )
                        }

                        WidgetHourlyReceiver::class.java.name -> {
                            WidgetHourlyConfig(
                                onDone = {
                                    onDone(it)
                                },
                            )
                        }

                        WidgetTransparentReceiver::class.java.name -> {
                            WidgetTransparentConfig(
                                onDone = {
                                    onDone(it)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}