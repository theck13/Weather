package com.pranshulgg.weather_master_app.core.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pranshulgg.weather_master_app.feature.blocks.screens.air.AirScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.celestial.CelestialScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.humidity.HumidityScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.RainScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.SnowScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.pressure.PressureScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.ultraviolet.UltravioletScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.visibility.VisibilityScreen
import com.pranshulgg.weather_master_app.feature.blocks.screens.wind.WindScreen
import com.pranshulgg.weather_master_app.feature.daily.DailyScreen
import com.pranshulgg.weather_master_app.feature.main.MainScreen
import com.pranshulgg.weather_master_app.feature.search.SearchScreen
import com.pranshulgg.weather_master_app.feature.settings.SettingsScreen
import com.pranshulgg.weather_master_app.feature.settings.about.AboutScreen
import com.pranshulgg.weather_master_app.feature.settings.about.LegalLicenseScreen
import com.pranshulgg.weather_master_app.feature.settings.about.PrivacyPolicyScreen
import com.pranshulgg.weather_master_app.feature.settings.about.TermsConditionsScreen
import com.pranshulgg.weather_master_app.feature.settings.appearance.AppearanceScreen
import com.pranshulgg.weather_master_app.feature.settings.background.BackgroundUpdatesScreen
import com.pranshulgg.weather_master_app.feature.settings.language.LanguageScreen
import com.pranshulgg.weather_master_app.feature.settings.sources.WeatherSourcesScreen
import com.pranshulgg.weather_master_app.feature.settings.units.UnitsScreen

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun AppNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        NavHost(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surfaceContainer,
            ),
            enterTransition = {
                NavigationTransitions.enter()
             },
            exitTransition = {
                NavigationTransitions.exit()
            },
            navController = navController,
            popEnterTransition = {
                NavigationTransitions.popEnter()
             },
            popExitTransition = {
                NavigationTransitions.popExit()
            },
            startDestination = "root",
        ) {
            navigation(
                route = "root",
                startDestination = NavigationRoutes.MAIN,
            ) {
                composable(
                    arguments = listOf(
                        navArgument("index") {
                            type = NavType.IntType
                            defaultValue = 0
                        },
                        navArgument("locationId") {
                            type = NavType.StringType
                        },
                        navArgument("block") {
                            type = NavType.StringType
                        }
                    ),
                    route = "{block}/{index}/{locationId}",
                ) { backStackEntry ->
                    val block = backStackEntry.arguments?.getString("block")
                    val index = backStackEntry.arguments?.getInt("index") ?: 0
                    val locationId = backStackEntry.arguments?.getString("locationId")!!

                    when (block) {
                        NavigationRoutes.AIR -> AirScreen(index, locationId, navController)
                        NavigationRoutes.CELESTIAL -> CelestialScreen(index, locationId, navController)
                        NavigationRoutes.HUMIDITY -> HumidityScreen(index, locationId, navController)
                        NavigationRoutes.PRESSURE -> PressureScreen(index, locationId, navController)
                        NavigationRoutes.RAIN -> RainScreen(index, locationId, navController)
                        NavigationRoutes.SNOW -> SnowScreen(index, locationId, navController)
                        NavigationRoutes.ULTRAVIOLET -> UltravioletScreen(index, locationId, navController)
                        NavigationRoutes.VISIBILITY -> VisibilityScreen(index, locationId, navController)
                        NavigationRoutes.WIND -> WindScreen(index, locationId, navController)
                    }
                }

                composable(
                    arguments = listOf(
                        navArgument("index") {
                            type = NavType.IntType
                            defaultValue = 0
                        },
                        navArgument("locationId") {
                            type = NavType.StringType
                        }
                    ),
                    route = "${NavigationRoutes.DAILY}/{index}/{locationId}",
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt("index") ?: 0
                    val locationId = backStackEntry.arguments?.getString("locationId")

                    DailyScreen(index, locationId!!, navController)
                }

                composable(
                    route = NavigationRoutes.ABOUT,
                ) {
                    AboutScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.APPEARANCE,
                ) {
                    AppearanceScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.BACKGROUND_UPDATES,
                ) {
                    BackgroundUpdatesScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.LANGUAGE,
                ) {
                    LanguageScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.LICENSE,
                ) {
                    LegalLicenseScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.MAIN,
                ) {
                    MainScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.PRIVACY_POLICY,
                ) {
                    PrivacyPolicyScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.SEARCH,
                ) {
                    SearchScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.SETTINGS,
                ) {
                    SettingsScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.SOURCES,
                ) {
                    WeatherSourcesScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.TERMS_CONDITIONS,
                ) {
                    TermsConditionsScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = NavigationRoutes.UNITS,
                ) {
                    UnitsScreen(
                        navController = navController,
                    )
                }
            }
        }

        SnackbarHost(
            modifier = Modifier
                .align(
                    alignment = Alignment.BottomCenter,
                )
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                ),
            hostState = snackbarHostState,
        )
    }
}
