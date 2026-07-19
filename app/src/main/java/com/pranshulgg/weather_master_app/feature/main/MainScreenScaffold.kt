package com.pranshulgg.weather_master_app.feature.main

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.SYSTEM_BAR_ALPHA
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark
import com.pranshulgg.weather_master_app.feature.main.components.CreditsBottomSection
import com.pranshulgg.weather_master_app.feature.main.components.FroggyContainer
import com.pranshulgg.weather_master_app.feature.main.components.MainSearchBar
import com.pranshulgg.weather_master_app.feature.main.ui.BackgroundGradient
import com.pranshulgg.weather_master_app.feature.main.ui.CurrentWeatherCard
import com.pranshulgg.weather_master_app.feature.main.ui.weatheranimations.WeatherAnimations
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.WeatherBlocks
import com.pranshulgg.weather_master_app.feature.shared.ui.DailyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.HourlyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.SummaryCard

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun MainScreenScaffold(
    navController: NavController,
    drawerState: DrawerState,
    uiState: MainScreenWeatherUiState,
    onRefresh: () -> Unit,
    onEditLocation: () -> Unit,
    context: Context,
    isTabletLike: Boolean,
) {
    val airQuality = remember(uiState.airQuality) { uiState.airQuality }
    val preferences = LocalAppPrefs.current
    val pullToRefreshState = rememberPullToRefreshState()
    val weather = remember(uiState.weather) { uiState.weather }

    val units = uiState.weatherUnits
    val scrollState = rememberScrollState()

    val isFroggyLayout = preferences.isFroggyLayout
    val isShowWeatherAnimations = preferences.isShowWeatherAnimations
    val isWeatherBasedTheme = preferences.isWeatherBasedTheme
    val isShowSummary = preferences.isShowSummary

    val isAnimationVisible by remember {
        derivedStateOf {
            scrollState.value < 30
        }
    }

    val isScrolled by remember {
        derivedStateOf {
            scrollState.value > 100
        }
    }

    val view = LocalView.current
    val isDark = isThemeDark()
    val containerColor = if (isWeatherBasedTheme) Color.Black else MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface

    if (isShowWeatherAnimations.not() && isWeatherBasedTheme.not()) DisposableEffect(isScrolled, isDark, isWeatherBasedTheme) {
        val activity = view.context as ComponentActivity
        val statusColor = (if (isScrolled) surfaceColor else containerColor).toArgb()

        fun style(color: Int) = SystemBarStyle.auto(
            darkScrim = color,
            detectDarkMode = {
                isDark
            },
            lightScrim = color,
        )

        activity.enableEdgeToEdge(
            navigationBarStyle = style(containerColor.toArgb()),
            statusBarStyle = style(statusColor),
        )

        onDispose {
            activity.enableEdgeToEdge(
                navigationBarStyle = style(containerColor.toArgb()),
                statusBarStyle = style(containerColor.toArgb()),
            )
        }
    }

    Scaffold(
        containerColor = if (isWeatherBasedTheme) Color.Black else MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box {
            if (isWeatherBasedTheme) {
                BackgroundGradient(
                    isScrolled = isScrolled,
                    weather = weather,
                )
            }

            if (isShowWeatherAnimations) {
                AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = isAnimationVisible,
                ) {
                    weather?.let {
                        WeatherAnimations(
                            isFroggyLayout = isFroggyLayout,
                            weather = weather,
                        )
                    }
                }
            }

            PullToRefreshBox(
                indicator = {
                    LoadingIndicator(
                        modifier = Modifier
                            .align(
                                alignment = Alignment.TopCenter,
                            )
                            .padding(
                                top = paddingValues.calculateTopPadding() + 64.dp,
                            )
                            .zIndex(
                                zIndex = 99999.00f,
                            ),
                        isRefreshing = uiState.isLoading,
                        state = pullToRefreshState,
                    )
                },
                isRefreshing = uiState.isLoading,
                onRefresh = {
                    onRefresh()
                },
                state = pullToRefreshState,
            ) {
                AnimatedContent(
                    targetState = weather,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { weather ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(
                                    insets = WindowInsets.systemBars.union(
                                        insets = WindowInsets.displayCutout,
                                    ).only(
                                        sides = WindowInsetsSides.Right,
                                    )
                                )
                                .verticalScroll(
                                    state = scrollState,
                                )
                        ) {
                            if (isShowWeatherAnimations || isWeatherBasedTheme) {
                                MainSearchBar(
                                    activeLocation = uiState.activeLocation,
                                    drawerState = drawerState,
                                    isFroggyLayout = isFroggyLayout,
                                    isTabletLike = isTabletLike,
                                    navController = navController,
                                    paddingValues = paddingValues,
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier.height(
                                        height = paddingValues.calculateTopPadding() + 64.dp,
                                    ),
                                )
                            }

                            if (weather != null) {
                                CurrentWeatherCard(
                                    context = context,
                                    isFroggyLayout = isFroggyLayout,
                                    units = units,
                                    weather = weather,
                                )

                                if (isFroggyLayout) {
                                    FroggyContainer(
                                        weather = weather,
                                    )
                                }

                                Gap(
                                    vertical = 8.dp,
                                )

                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp,
                                    ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    if (isShowSummary) {
                                        SummaryCard(
                                            context = context,
                                            units = units,
                                            weather = weather,
                                        )

                                        Gap (
                                            vertical = 16.dp,
                                        )
                                    }

                                    HourlyCard(
                                        units = units,
                                        weather = weather,
                                    )

                                    Gap (
                                        vertical = 16.dp,
                                    )

                                    DailyCard(
                                        navController = navController,
                                        units = units,
                                        weather = weather,
                                    )

                                    Gap (
                                        vertical = 8.dp,
                                    )

                                    WeatherBlocks(
                                        airQuality = airQuality,
                                        blocks = uiState.blocks,
                                        context = context,
                                        navController = navController,
                                        units = units,
                                        weather = weather,
                                    )

                                    CreditsBottomSection(
                                        onClick = onEditLocation,
                                        weather = weather,
                                    )
                                }
                            }
                        }

                        if (isShowWeatherAnimations.not() && isWeatherBasedTheme.not()) {
                            MainSearchBar(
                                activeLocation = uiState.activeLocation,
                                drawerState = drawerState,
                                isFroggyLayout = isFroggyLayout,
                                isTabletLike = isTabletLike,
                                navController = navController,
                                paddingValues = paddingValues,
                                scrollState = scrollState,
                            )
                        }
                    }
                }
            }

            if (isShowWeatherAnimations.not() && isWeatherBasedTheme.not()) {
                Box(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.BottomCenter,
                        )
                        .background(
                            color = containerColor.copy(
                                alpha = SYSTEM_BAR_ALPHA,
                            ),
                        )
                        .fillMaxWidth()
                        .windowInsetsBottomHeight(
                            insets = WindowInsets.navigationBars,
                        ),
                )
            }
        }
    }
}
