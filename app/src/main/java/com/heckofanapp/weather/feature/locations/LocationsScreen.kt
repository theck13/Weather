package com.heckofanapp.weather.feature.locations

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.weather.WeatherUnits
import com.heckofanapp.weather.core.ui.components.Symbol
import com.heckofanapp.weather.core.ui.components.Tooltip
import com.heckofanapp.weather.core.ui.navigation.NavigationRoutes
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.data.provider.devicelocation.hasBackgroundLocationPermission
import com.heckofanapp.weather.data.provider.devicelocation.rememberBackgroundLocationPermissionLauncher
import com.heckofanapp.weather.data.provider.devicelocation.rememberLocationPermissionLauncher
import com.heckofanapp.weather.feature.locations.ui.LocationScreenChooseDefaultDialog
import com.heckofanapp.weather.feature.locations.ui.LocationScreenConfirmationDialog
import com.heckofanapp.weather.feature.locations.ui.LocationScreenSheet
import com.heckofanapp.weather.feature.shared.WeatherViewModel
import com.heckofanapp.weather.feature.shared.ui.SharedDialogs
import com.heckofanapp.weather.widgets.hasActiveWidgets

data class LocationsScreenUiState(
    val isConfirmationDialogOpen: Boolean = false,
    val longClickedLocation: Location? = null,
    val isBottomSheetOpen: Boolean = false,
    val isChooseDefaultDialogOpen: Boolean = false,
    val isDeviceLocationLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isReordering: Boolean = false,
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun LocationsScreen(
    activeLocation: Location?,
    isTabletLike: Boolean = false,
    locations: List<Location>,
    navController: NavController,
    onBack: () -> Unit,
    onLocationSelect: (Location) -> Unit,
    units: WeatherUnits,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val viewModel: LocationsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState
    val weatherForLocations by viewModel.allLocationsWeather.collectAsStateWithLifecycle(
        initialValue = emptyList(),
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val weatherViewModel: WeatherViewModel = hiltViewModel()

    var backgroundLocationPermissionInfoDialogOpen by remember { mutableStateOf(false) }
    var locationPermissionInfoDialogOpen by remember { mutableStateOf(false) }
    var hasBackgroundLocation by remember { mutableStateOf(context.hasBackgroundLocationPermission()) }

    // Prompt for background location only after a device location has been added, and only
    // when widgets exist, since background location exists purely to keep widgets updated.
    val promptBackgroundLocationIfNeeded = {
        if (context.hasActiveWidgets() && !context.hasBackgroundLocationPermission()) {
            backgroundLocationPermissionInfoDialogOpen = true
        }
    }

    val requestLocation = rememberLocationPermissionLauncher(
        onDenied = {
            SnackbarManager.show(
                message = R.string.location_permission_required,
            )
        },
        onForegroundGranted = {
            viewModel.saveDeviceLocation(onSaved = promptBackgroundLocationIfNeeded)
        },
    )
    val requestBackgroundLocation = rememberBackgroundLocationPermissionLauncher(
        onContinueWithoutBackground = {
            hasBackgroundLocation = context.hasBackgroundLocationPermission()
        },
        onDenied = {
            hasBackgroundLocation = false
        },
        onGranted = {
            hasBackgroundLocation = true
        },
    )

    val showBackgroundLocationCard =
        hasBackgroundLocation.not() &&
        context.hasActiveWidgets() &&
        locations.any { it.isDeviceLocation }

    BackHandler(
        enabled = uiState.value.isReordering,
    ) {
        viewModel.stopReordering()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            connection = scrollBehavior.nestedScrollConnection,
        ),
        topBar = {
            TopBar(
                isRefreshing = uiState.value.isRefreshing,
                isReordering = uiState.value.isReordering,
                isTabletLike = isTabletLike,
                navController = navController,
                onBack = onBack,
                onDoneReordering = {
                    viewModel.stopReordering()
                },
                onRefresh = {
                    viewModel.refreshAllLocations(
                        onComplete = {
                            weatherViewModel.reloadActiveLocation()
                        },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .then(
                    if (isTabletLike) {
                        Modifier
                            .padding(
                                top = TopAppBarDefaults.TopAppBarExpandedHeight,
                            )
                            .windowInsetsPadding(
                                insets = TopAppBarDefaults.windowInsets.only(
                                    sides = WindowInsetsSides.Left + WindowInsetsSides.Top,
                                )
                            )
                    } else {
                        Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                        )
                    }
                )
                .fillMaxSize(),
        ) {
            LocationsScreenContent(
                activeLocation = activeLocation,
                bottomPadding = paddingValues.calculateBottomPadding(),
                isDeviceLocationLoading =uiState.value.isDeviceLocationLoading,
                isReordering = uiState.value.isReordering,
                locations = locations,
                onAddCurrentLocation = {
                    locationPermissionInfoDialogOpen = true
                },
                onEnableBackgroundLocation = {
                    backgroundLocationPermissionInfoDialogOpen = true
                },
                onLocationSelect = {
                    onLocationSelect(it)
                },
                onLongClick = {
                    viewModel.showBottomSheet(it)
                },
                onReorder = {
                    viewModel.updateLocationsOrder(it)
                },
                showBackgroundLocationCard = showBackgroundLocationCard,
                units = units,
                weatherForLocations = weatherForLocations,
            )
        }
    }

    LocationScreenConfirmationDialog(
        weatherViewModel = weatherViewModel,
        viewModel = viewModel,
    )

    LocationScreenChooseDefaultDialog(
        locations = locations,
        viewModel = viewModel,
        weatherViewModel = weatherViewModel,
    )

    LocationScreenSheet(
        locations = locations,
        sheetState = sheetState,
        viewModel = viewModel,
    )

    SharedDialogs.DeviceBackgroundLocationPermissionInfoDialog(
        onConfirm = {
            backgroundLocationPermissionInfoDialogOpen = false
            requestBackgroundLocation()
        },
        onDismiss = {
            backgroundLocationPermissionInfoDialogOpen = false
        },
        show = backgroundLocationPermissionInfoDialogOpen,
    )

    SharedDialogs.DeviceLocationPermissionInfoDialog(
        onConfirm = {
            requestLocation()
        },
        onDismiss = {
            locationPermissionInfoDialogOpen = false
        },
        show = locationPermissionInfoDialogOpen,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
private fun TopBar(
    isRefreshing: Boolean,
    isReordering: Boolean,
    isTabletLike: Boolean,
    navController: NavController,
    onBack: () -> Unit,
    onDoneReordering: () -> Unit,
    onRefresh: () -> Unit,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
) {
    val shadowElevation by animateDpAsState(
        label = "Locations Top Bar Shadow",
        targetValue = ShadowElevation.level2 * scrollBehavior.state.overlappedFraction,
    )

    TopAppBar(
        actions = {
            // While reordering, the only action is confirming the new order.
            if (isReordering) {
                Tooltip(
                    preferredPosition = TooltipAnchorPosition.Below,
                    spacing = 10.dp,
                    tooltipText = stringResource(R.string.action_done),
                ) {
                    IconButton(
                        onClick = onDoneReordering,
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Symbol(
                            color = MaterialTheme.colorScheme.onSurface,
                            description = stringResource(R.string.action_done),
                            icon = R.drawable.ic_check_24,
                        )
                    }
                }
            } else {
                if (isTabletLike.not()) {
                    Tooltip(
                        preferredPosition = TooltipAnchorPosition.Below,
                        spacing = 10.dp,
                        tooltipText = stringResource(R.string.search),
                    ) {
                        IconButton(
                            onClick = {
                                onBack()
                                navController.navigate(
                                    route = NavigationRoutes.SEARCH,
                                )
                            },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Symbol(
                                color = MaterialTheme.colorScheme.onSurface,
                                description = stringResource(R.string.search),
                                icon = R.drawable.ic_search_24,
                            )
                        }
                    }
                }

                Tooltip(
                    preferredPosition = TooltipAnchorPosition.Below,
                    spacing = 10.dp,
                    tooltipText = stringResource(R.string.refresh),
                ) {
                    IconButton(
                        enabled = isRefreshing.not(),
                        onClick = onRefresh,
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(
                                    size = 16.dp,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Symbol(
                                color = MaterialTheme.colorScheme.onSurface,
                                description = stringResource(R.string.refresh_locations),
                                icon = R.drawable.ic_refresh_24,
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier
            .drawWithContent {
                clipRect(
                    bottom = size.height + 16.dp.toPx(),
                ) {
                    this@drawWithContent.drawContent()
                }
            }
            .shadow(
                elevation = shadowElevation,
            ),
        navigationIcon = {
            // Back exits reorder mode first so it never navigates away mid-reorder.
            if (isReordering) {
                Tooltip(
                    preferredPosition = TooltipAnchorPosition.Below,
                    spacing = 10.dp,
                    tooltipText = stringResource(R.string.action_done),
                ) {
                    IconButton(
                        onClick = onDoneReordering,
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Symbol(
                            color = MaterialTheme.colorScheme.onSurface,
                            description = stringResource(R.string.arrow_back),
                            icon = R.drawable.ic_arrow_back_24,
                        )
                    }
                }
            } else if (isTabletLike.not()) {
                Tooltip(
                    preferredPosition = TooltipAnchorPosition.Below,
                    spacing = 10.dp,
                    tooltipText = stringResource(R.string.navigate_back),
                ) {
                    IconButton(
                        onClick = {
                            onBack()
                        },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Symbol(
                            color = MaterialTheme.colorScheme.onSurface,
                            description = stringResource(R.string.arrow_back),
                            icon = R.drawable.ic_arrow_back_24,
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(
                    if (isReordering) R.string.action_reorder_locations else R.string.locations,
                ),
            )
        },
        windowInsets =
            if (isTabletLike) {
                TopAppBarDefaults.windowInsets.only(
                    sides = WindowInsetsSides.Left + WindowInsetsSides.Top,
                )
            } else {
                TopAppBarDefaults.windowInsets
            },
    )
}
