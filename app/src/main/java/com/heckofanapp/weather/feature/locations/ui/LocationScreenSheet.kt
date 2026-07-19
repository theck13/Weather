package com.heckofanapp.weather.feature.locations.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.ui.components.ActionBottomSheet
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.feature.locations.LocationsScreenViewModel
import com.heckofanapp.weather.feature.locations.components.LocationScreenSheetContent

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun LocationScreenSheet(
    locations: List<Location>,
    sheetState: SheetState,
    viewModel: LocationsScreenViewModel,
) {
    val uiState = viewModel.uiState.value

    if (uiState.isBottomSheetOpen) {
        ActionBottomSheet(
            onCancel = viewModel::hideBottomSheet,
            onConfirm = {},
            sheetState = sheetState,
            showActions = false,
        ) { hide ->
            LocationScreenSheetContent(
                locationName = uiState.longClickedLocation?.name ?: stringResource(R.string.title_location_actions),
                onDelete = {
                    hide()

                    val location = uiState.longClickedLocation
                    when {
                        location == null -> SnackbarManager.show(
                            message = R.string.error_delete_location,
                        )
                        // Deleting the default requires promoting another location first; only
                        // possible when there is somewhere else to move the default to.
                        location.isDefault && locations.any { it.id != location.id } ->
                            viewModel.showChooseDefaultDialog()
                        location.isDefault -> SnackbarManager.show(
                            message = R.string.error_delete_default_location,
                        )
                        else -> viewModel.showConfirmationDialog()
                    }
                },
                onSetAsDefault = {
                    hide()

                    uiState.longClickedLocation?.let { location ->
                        if (location.isDefault.not()) {
                            viewModel.updateDefaultLocation(location.id)
                        }
                    } ?: run {
                        SnackbarManager.show(
                            message = R.string.error_set_default_location,
                        )
                    }
                },
                onReorderLocations = {
                    hide()
                    viewModel.startReordering()
                },
            )
        }
    }
}
