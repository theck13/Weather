package com.heckofanapp.weather.core.ui.theme

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat.getInsetsController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.setSystemBarStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

@Composable
private fun ColorScheme.animated(): ColorScheme {
    val spec = tween<Color>(
        durationMillis = 400,
    )
    return copy(
        background              = animateColorAsState(background, spec).value,
        error                   = animateColorAsState(error, spec).value,
        errorContainer          = animateColorAsState(errorContainer, spec).value,
        inverseOnSurface        = animateColorAsState(inverseOnSurface, spec).value,
        inversePrimary          = animateColorAsState(inversePrimary, spec).value,
        inverseSurface          = animateColorAsState(inverseSurface, spec).value,
        onBackground            = animateColorAsState(onBackground, spec).value,
        onError                 = animateColorAsState(onError, spec).value,
        onErrorContainer        = animateColorAsState(onErrorContainer, spec).value,
        onPrimary               = animateColorAsState(onPrimary, spec).value,
        onPrimaryContainer      = animateColorAsState(onPrimaryContainer, spec).value,
        onSecondary             = animateColorAsState(onSecondary, spec).value,
        onSecondaryContainer    = animateColorAsState(onSecondaryContainer, spec).value,
        onSurface               = animateColorAsState(onSurface, spec).value,
        onSurfaceVariant        = animateColorAsState(onSurfaceVariant, spec).value,
        onTertiary              = animateColorAsState(onTertiary, spec).value,
        onTertiaryContainer     = animateColorAsState(onTertiaryContainer, spec).value,
        outline                 = animateColorAsState(outline, spec).value,
        outlineVariant          = animateColorAsState(outlineVariant, spec).value,
        primary                 = animateColorAsState(primary, spec).value,
        primaryContainer        = animateColorAsState(primaryContainer, spec).value,
        secondary               = animateColorAsState(secondary, spec).value,
        secondaryContainer      = animateColorAsState(secondaryContainer, spec).value,
        surface                 = animateColorAsState(surface, spec).value,
        surfaceBright           = animateColorAsState(surfaceBright, spec).value,
        surfaceContainer        = animateColorAsState(surfaceContainer, spec).value,
        surfaceContainerHigh    = animateColorAsState(surfaceContainerHigh, spec).value,
        surfaceContainerHighest = animateColorAsState(surfaceContainerHighest, spec).value,
        surfaceContainerLow     = animateColorAsState(surfaceContainerLow, spec).value,
        surfaceContainerLowest  = animateColorAsState(surfaceContainerLowest, spec).value,
        surfaceDim              = animateColorAsState(surfaceDim, spec).value,
        surfaceVariant          = animateColorAsState(surfaceVariant, spec).value,
        tertiary                = animateColorAsState(tertiary, spec).value,
        tertiaryContainer       = animateColorAsState(tertiaryContainer, spec).value,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun Theme(
    dynamicTheme: Boolean = false,
    isDark: Boolean = isSystemInDarkTheme(),
    seedColor: Color = Color.Green,
    themeVariant: ThemeVariant,
    applySystemUi: Boolean = true,
    content: @Composable () -> Unit,
) {
    val preferences = LocalAppPrefs.current
    val view = LocalView.current

    var color =
        when {
            dynamicTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            else -> {
                rememberDynamicColorScheme(
                    seedColor = seedColor,
                    isDark = isDark,
                    specVersion = ColorSpec.SpecVersion.SPEC_2025,
                    style = themeVariant.toPaletteStyle(),
                )
            }
        }
    color = color.copy(
        background = colorResource(
            if (isDark) R.color.background_dark else R.color.background_light,
        ),
    )

    if (view.isInEditMode.not()) {
        if (applySystemUi) {
            val background = if (preferences.isWeatherBasedTheme) Color.Black.toArgb() else null

            DisposableEffect(
                isDark,
                background,
            ) {
                val activity = view.context as ComponentActivity

                val fromNavigationColor = activity.window.navigationBarColor
                val fromStatusColor = activity.window.statusBarColor

                activity.setSystemBarStyle(
                    background = background,
                    isDark = isDark,
                )

                val toNavigationColor = activity.window.navigationBarColor
                val toStatusColor = activity.window.statusBarColor

                val animator = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    fromNavigationColor,
                    toNavigationColor,
                ).apply {
                    duration = 400
                    addUpdateListener { anim ->
                        val color = anim.animatedValue as Int
                        @Suppress("DEPRECATION")
                        activity.window.navigationBarColor = color
                        @Suppress("DEPRECATION")
                        activity.window.statusBarColor = color
                    }
                    start()
                }

                activity.window?.let { window ->
                    getInsetsController(window, view).let { controller ->
                        controller.isAppearanceLightNavigationBars = isDark.not()
                        controller.isAppearanceLightStatusBars = isDark.not()
                    }
                }

                onDispose { animator.cancel() }
            }
        }
    }

    MaterialExpressiveTheme(
        colorScheme = color.animated(),
        content = content,
        motionScheme = MotionScheme.expressive(),
        typography = getAppTypography(
            useGoogleSans = preferences.isGoogleSansFlex,
        ),
    )
}

@Composable
fun isThemeDark(): Boolean {
    val preferences = LocalAppPrefs.current
    val isDark = when (preferences.appTheme) {
        "Dark" -> true
        "Light" -> false
        "System" -> isSystemInDarkTheme()
        else -> isSystemInDarkTheme()
    }

    return isDark
}
