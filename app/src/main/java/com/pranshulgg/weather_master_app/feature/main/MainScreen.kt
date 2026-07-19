package com.pranshulgg.weather_master_app.feature.main

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.feature.intro.IntroScreen
import com.pranshulgg.weather_master_app.feature.locations.LocationsScreen
import com.pranshulgg.weather_master_app.feature.main.ui.MainScreenBottomSheets
import com.pranshulgg.weather_master_app.feature.main.ui.NavigationDrawer
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedBottomSheet
import kotlinx.coroutines.launch

data class MainScreenWeatherUiState(
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val activeLocation: Location? = null,
    val locations: List<Location> = emptyList(),
    val weather: Weather? = null,
    val weatherUnits: WeatherUnits = WeatherUnits.getDefault(),
    val blocks: List<WeatherBlock> = WeatherBlock.getDefault(),
    val isInitialized: Boolean = false,
    val airQuality: AirQuality? = null,
)

data class MainScreenUiState(
    val isWeatherSourcesForLocationSheetOpen: Boolean = false,
    val isWeatherSourcesInfoForLocationSheetOpen: Boolean = false,
    val isNewVersionAvailable: Boolean = false,
    val lastestVersionUrl: String = "https://github.com/theck13/Weather/releases/latest"
)

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun MainScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed,
    )
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        enabledValues = setOf(SheetValue.Expanded, SheetValue.Hidden),
        initialValue = SheetValue.Hidden,
    )
    val uriHandler = LocalUriHandler.current
    val viewModel: MainScreenViewModel = hiltViewModel()

    val mainScreenUiState = viewModel.uiState.value
    val weatherViewModel: WeatherViewModel = hiltViewModel()

    val uiState by weatherViewModel.uiState

    val activeLocation = uiState.activeLocation
    val widthDp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    val isTabletLike = widthDp > 600.dp

    val closeDrawer = {
        scope.launch { drawerState.close() }
    }

    if (uiState.locations.isEmpty()) {
        IntroScreen(
            navController = navController,
        )

        return
    }

    BackHandler(
        enabled = drawerState.isOpen,
    ) {
        closeDrawer()
    }

    LaunchedEffect(mainScreenUiState.isNewVersionAvailable) {
        if (mainScreenUiState.isNewVersionAvailable) {
            SnackbarManager.show(
                action = R.string.action_view,
                duration = SnackbarDuration.Indefinite,
                message = R.string.message_new_version_available,
                onAction = {
                    uriHandler.openUri(mainScreenUiState.lastestVersionUrl)
                },
            )

            viewModel.dismissNewVersionSnackbar()
        }
    }

    NavigationDrawer(
        drawerContent = {
            LocationsScreen(
                activeLocation = uiState.activeLocation,
                locations = uiState.locations,
                navController = navController,
                isTabletLike = isTabletLike,
                onBack = {
                    closeDrawer()
                },
                onLocationSelect = {
                    if (activeLocation == it) return@LocationsScreen
                    weatherViewModel.setLoading(true)
                    scope.launch {
                        drawerState.close() // Wait until drawer fully closes.
                        weatherViewModel.setActiveLocation(it)
                    }
                },
                units = uiState.weatherUnits,
            )
        },
        drawerState = drawerState,
        isTabletLike = isTabletLike,
        content = {
            MainScreenScaffold(
                context = context,
                drawerState = drawerState,
                isTabletLike = isTabletLike,
                navController = navController,
                onEditLocation = {
                    viewModel.showWeatherSourcesForLocationSheet(uiState.isLoading)
                },
                onRefresh = {
                    if (activeLocation != null) {
                        weatherViewModel.getWeather(
                            isManualRefresh = true,
                            location = activeLocation,
                            source = activeLocation.source,
                        )
                    }
                },
                uiState = uiState,
            )
        }
    )

    SharedBottomSheet.WeatherSourcesForLocationSheet(
        countryCode = activeLocation?.countryCode,
        isEditing = true,
        onDismiss = viewModel::hideWeatherSourcesForLocationSheet,
        onSave = {
            if (activeLocation != null) {
                weatherViewModel.updateSourceForLocation(activeLocation, it)
            }
        },
        selectedSource = activeLocation?.source ?: WeatherSource.OPEN,
        sheetState = sheetState,
        show = viewModel.uiState.value.isWeatherSourcesForLocationSheetOpen,
    )

    MainScreenBottomSheets.WeatherSourcesInfoForLocationSheet(
        location = activeLocation,
        sheetState = sheetState,
        viewModel = viewModel,
    )
}
