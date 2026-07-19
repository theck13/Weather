package com.heckofanapp.weather.widgets.card

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.heckofanapp.weather.Activity
import com.heckofanapp.weather.R
import com.heckofanapp.weather.widgets.WeatherWidgetStateDefinition
import com.heckofanapp.weather.widgets.WeatherWidgetStateJson
import com.heckofanapp.weather.widgets.card.ui.variants.WidgetCardLarge
import com.heckofanapp.weather.widgets.card.ui.variants.WidgetCardMedium
import com.heckofanapp.weather.widgets.card.ui.variants.WidgetCardSmall
import com.heckofanapp.weather.widgets.model.WidgetVariant
import com.heckofanapp.weather.widgets.model.WidgetWeather
import com.heckofanapp.weather.widgets.params.WidgetSizePoints
import com.heckofanapp.weather.widgets.ui.ReloadButton
import com.heckofanapp.weather.widgets.ui.colors.WidgetColors
import com.heckofanapp.weather.widgets.ui.colors.WidgetTheme
import kotlinx.serialization.json.Json

class WidgetCard : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        sizes = WidgetSizePoints.SIZES,
    )
    override val stateDefinition =
        WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val widgetColors = WidgetColors()
            val widgetState = currentState<WeatherWidgetStateJson>()
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            if (state != null) {
                val config = widgetState.config
                val modifier = when (config.widgetTheme) {
                    WidgetTheme.TRANSPARENT -> GlanceModifier.fillMaxSize()
                    else -> GlanceModifier
                        .appWidgetBackgroundShape(
                            theme = config.widgetTheme,
                            widgetColors = widgetColors,
                        )
                        .fillMaxSize()
                }

                Box(
                    modifier = modifier.clickable(
                        onClick = actionStartActivity<Activity>(),
                    )
                ) {
                    when (config.variant) {
                        WidgetVariant.LARGE -> WidgetCardLarge(
                            config = config,
                            state = state,
                        )

                        WidgetVariant.MEDIUM -> WidgetCardMedium(
                            config = config,
                            state = state,
                        )

                        WidgetVariant.SMALL -> WidgetCardSmall(
                            config = config,
                            state = state,
                        )
                    }
                }
            } else {
                ReloadButton()
            }
        }
    }
}

@Composable
private fun GlanceModifier.appWidgetBackgroundShape(
    theme: WidgetTheme,
    widgetColors: WidgetColors,
): GlanceModifier {
    val color = widgetColors.getBackgroundColor(
        widgetTheme = theme,
    ) ?: GlanceTheme.colors.widgetBackground

    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .background(
                colorProvider = color,
            )
            .cornerRadius(
                radius = 16.dp,
            )
    } else {
        this.background(
            colorFilter = ColorFilter.tint(
                colorProvider = color,
            ),
            imageProvider = ImageProvider(R.drawable.il_widget_background),
        )
    }
}
