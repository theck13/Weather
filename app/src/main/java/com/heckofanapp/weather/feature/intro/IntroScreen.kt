package com.heckofanapp.weather.feature.intro

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.Symbol
import com.heckofanapp.weather.core.ui.components.WeatherIconBox
import com.heckofanapp.weather.core.ui.navigation.NavigationRoutes
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.core.utils.ids.UuidGenerator
import com.heckofanapp.weather.data.provider.devicelocation.DeviceLocation
import com.heckofanapp.weather.data.provider.devicelocation.GetDeviceLocation
import com.heckofanapp.weather.data.provider.devicelocation.rememberBackgroundLocationPermissionLauncher
import com.heckofanapp.weather.data.provider.devicelocation.rememberLocationPermissionLauncher
import com.heckofanapp.weather.feature.shared.ui.SharedDialogs
import com.heckofanapp.weather.widgets.hasActiveWidgets
import java.time.ZoneId

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun IntroScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: IntroScreenViewModel = hiltViewModel()

    var backgroundLocationPermissionInfoDialogOpen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var locationPermissionInfoDialogOpen by remember { mutableStateOf(false) }

    val continueWithLocation = {
        isLoading = true
        GetDeviceLocation().getDeviceLocation(
            context = context,
            onTimeout = {
                SnackbarManager.show(
                    message = R.string.current_location_not_found,
                )
                isLoading = false
            },
        ) { location ->
            if (location.latitude == null || location.longitude == null) {
                SnackbarManager.show(
                    message = R.string.current_location_not_found,
                )
                isLoading = false
                return@getDeviceLocation
            }

            viewModel.saveDeviceLocation(location)
        }
    }

    val requestLocation = rememberLocationPermissionLauncher(
        onForegroundGranted = {
            if (context.hasActiveWidgets()) {
                backgroundLocationPermissionInfoDialogOpen = true
            } else {
                continueWithLocation()
            }
        },
        onDenied = {
            SnackbarManager.show(
                message = R.string.location_permission_required,
            )
            isLoading = false
        }
    )

    val requestBackgroundLocation = rememberBackgroundLocationPermissionLauncher(
        onGranted = {
            continueWithLocation()
        },
        onContinueWithoutBackground = {
            continueWithLocation()
        },
        onDenied = {
            SnackbarManager.show(
                message = R.string.location_permission_required,
            )
            backgroundLocationPermissionInfoDialogOpen = true
            isLoading = false
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        val buttonSize = ButtonDefaults.MediumContainerHeight

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val decorativeIconSize = min(
                maxHeight * 0.40f,
                maxWidth * 0.58f,
            )
            val decorativeIconOffsetX = decorativeIconSize * 0.26f
            val decorativeIconOffsetY = maxHeight * 0.02f
            val isLandscape = maxWidth > maxHeight

            Column(
                modifier = Modifier
                    .align(
                        alignment = Alignment.Center,
                    )
                    .padding(
                        paddingValues = paddingValues,
                    )
                    .padding(
                        all = 16.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon()

                Gap(
                    vertical = 24.dp,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.headlineSmall,
                    text = "Get Your Forecast",
                )

                Gap(
                    vertical = 8.dp,
                )

                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    text = "Allow location access to see the forecast of your current location.",
                    textAlign = TextAlign.Center,
                )

                Gap(
                    vertical = 28.dp,
                )

                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth(
                            fraction = 0.70f,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 16.dp,
                        ),
                    ) {
                        ButtonEnableLocation(
                            modifier = Modifier.weight(
                                weight = 1.00f,
                            ),
                            buttonSize = buttonSize,
                            enabled = isLoading.not(),
                            onClick = {
                                locationPermissionInfoDialogOpen = true
                            },
                        )

                        ButtonSearchLocations(
                            modifier = Modifier.weight(
                                weight = 1.00f,
                            ),
                            buttonSize = buttonSize,
                            onClick = {
                                navController.navigate(NavigationRoutes.SEARCH)
                            },
                        )
                    }
                } else {
                    ButtonEnableLocation(
                        modifier = Modifier.fillMaxWidth(
                            fraction = 0.70f,
                        ),
                        buttonSize = buttonSize,
                        enabled = isLoading.not(),
                        onClick = {
                            locationPermissionInfoDialogOpen = true
                        },
                    )

                    Gap(
                        vertical = 16.dp,
                    )

                    ButtonSearchLocations(
                        modifier = Modifier.fillMaxWidth(
                            fraction = 0.70f,
                        ),
                        buttonSize = buttonSize,
                        onClick = {
                            navController.navigate(NavigationRoutes.SEARCH)
                        },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(
                        alignment = Alignment.TopStart,
                    )
                    .alpha(
                        alpha = 0.20f,
                    )
                    .offset(
                        x = -decorativeIconOffsetX,
                        y = -decorativeIconOffsetY,
                    )
                    .rotate(
                        degrees = 60.00f,
                    ),
            ) {
                WeatherIconBox(
                    icon = R.drawable.il_weather_very_hot,
                    size = decorativeIconSize,
                )
            }

            Box(
                modifier = Modifier
                    .align(
                        alignment = Alignment.TopEnd,
                    )
                    .alpha(
                        alpha = 0.10f,
                    )
                    .offset(
                        x = decorativeIconOffsetX,
                        y = -decorativeIconOffsetY,
                    )
                    .rotate(
                        degrees = 60.00f,
                    ),
            ) {
                WeatherIconBox(
                    icon = R.drawable.il_weather_clear_night,
                    size = decorativeIconSize,
                )
            }

            Box(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomStart,
                    )
                    .alpha(
                        alpha = 0.10f,
                    )
                    .offset(
                        x = -decorativeIconOffsetX,
                        y = decorativeIconOffsetY,
                    )
                    .rotate(
                        degrees = 60.00f,
                    ),
            ) {
                WeatherIconBox(
                    icon = R.drawable.il_weather_clear_day,
                    size = decorativeIconSize,
                )
            }

            Box(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomEnd,
                    )
                    .alpha(
                        alpha = 0.10f,
                    )
                    .offset(
                        x = decorativeIconOffsetX,
                        y = decorativeIconOffsetY,
                    )
                    .rotate(
                        degrees = 60.00f,
                    ),
            ) {
                WeatherIconBox(
                    icon = R.drawable.il_weather_very_cold,
                    size = decorativeIconSize,
                )
            }
        }
    }

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
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun ButtonEnableLocation(
    buttonSize: Dp,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.heightIn(
            min = buttonSize,
        ),
        contentPadding = ButtonDefaults.contentPaddingFor(
            buttonHeight = buttonSize,
        ),
        enabled = enabled,
        onClick = onClick,
        shapes = ButtonDefaults.shapes(),
    ) {
        Text(
            style = ButtonDefaults.textStyleFor(
                buttonHeight = buttonSize,
            ),
            text = "Enable Location",
        )
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun ButtonSearchLocations(
    buttonSize: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.heightIn(
            min = buttonSize,
        ),
        contentPadding = ButtonDefaults.contentPaddingFor(
            buttonHeight = buttonSize,
        ),
        onClick = onClick,
        shapes = ButtonDefaults.shapes(),
    ) {
        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = ButtonDefaults.textStyleFor(
                buttonHeight = buttonSize,
            ),
            text = stringResource(R.string.search_empty_state_title),
        )
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun Icon() {
    Surface(
        modifier = Modifier
            .height(
                height = 100.dp,
            )
            .width(
                width = 100.dp,
            ),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialShapes.Cookie9Sided.toShape(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Symbol(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = R.drawable.ic_location_on_24,
                size = 56.dp,
            )
        }
    }
}

fun DeviceLocation.toDomain(
    context: Context,
): Location {
    val formattedLatitude = kotlin.math.round(latitude!! * 100000) / 100000
    val formattedLongitude = kotlin.math.round(longitude!! * 100000) / 100000

    return Location(
        id = UuidGenerator.generateId(),
        name = "$formattedLatitude, $formattedLongitude",
        latitude = formattedLatitude,
        longitude = formattedLongitude,
        country = "",
        timezone = ZoneId.systemDefault().id,
        countryCode = "",
        state = "",
        source = WeatherSource.OPEN,
        isFavorite = false,
        isPinned = false,
        isDefault = false, // Repository can handle it
        isDeviceLocation = true,
    )
}
