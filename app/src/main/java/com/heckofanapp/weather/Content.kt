package com.heckofanapp.weather

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toColorInt
import androidx.navigation.compose.rememberNavController
import com.heckofanapp.weather.core.prefs.AppPrefs
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.navigation.AppNavHost
import com.heckofanapp.weather.core.ui.snackbar.LocalSnackbarHostState
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.core.ui.theme.Theme
import com.heckofanapp.weather.core.ui.theme.isThemeDark
import kotlinx.coroutines.flow.collectLatest

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun Content() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.events.collectLatest { event ->
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                actionLabel = event.action?.let{ context.getString(event.action) },
                duration = event.duration,
                message = event.messageText ?: context.getString(
                    event.messageResource ?: 0,
                    event.arguments,
                ),
                withDismissAction = event.action == null,
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.onAction?.invoke()
            }
        }
    }

    CompositionLocalProvider(
        LocalAppPrefs provides AppPrefs.state(),
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        val preferences = LocalAppPrefs.current

        Theme(
            dynamicTheme = preferences.isDynamicTheme,
            isDark = isThemeDark(),
            seedColor = Color(preferences.customThemeColor.toColorInt()),
            themeVariant = preferences.themeVariant,
        ) {
            AppNavHost(
                navController = navController,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
