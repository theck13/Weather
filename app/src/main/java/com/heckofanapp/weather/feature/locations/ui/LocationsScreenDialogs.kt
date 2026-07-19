package com.heckofanapp.weather.feature.locations.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.ui.components.DialogBasic
import com.heckofanapp.weather.core.ui.components.TextAlertDialog
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.feature.locations.LocationsScreenViewModel
import com.heckofanapp.weather.feature.shared.WeatherViewModel

@Composable
fun LocationScreenConfirmationDialog(
    weatherViewModel: WeatherViewModel,
    viewModel: LocationsScreenViewModel,
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.value

    TextAlertDialog(
        message = stringResource(R.string.message_delete_location),
        onConfirm = {
            uiState.longClickedLocation?.let { location ->
                weatherViewModel.deleteLocation(location.id)

                viewModel.hideConfirmationDialog()
                SnackbarManager.show(
                    action = R.string.action_undo,
                    duration = SnackbarDuration.Long,
                    message = context.getString(
                        R.string.message_deleted_location,
                        location.name,
                    ),
                    onAction = {
                        weatherViewModel.restoreLocation(location)
                    },
                )
            } ?: run {
                SnackbarManager.show(
                    message = R.string.error_delete_location,
                )
            }
        },
        onDismiss = viewModel::hideConfirmationDialog,
        show = uiState.isConfirmationDialogOpen,
        title = stringResource(R.string.action_delete_location),
    )
}

@Composable
fun LocationScreenChooseDefaultDialog(
    locations: List<Location>,
    viewModel: LocationsScreenViewModel,
    weatherViewModel: WeatherViewModel,
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.value
    val locationToDelete = uiState.longClickedLocation

    val candidates = remember(locations, locationToDelete?.id) {
        locations.filter { it.id != locationToDelete?.id }
    }

    // Reset the selection each time the dialog is opened.
    var selectedId by remember(uiState.isChooseDefaultDialogOpen) {
        mutableStateOf<String?>(null)
    }

    DialogBasic(
        isConfirmButtonDisabled = selectedId == null,
        onConfirm = {
            val newDefault = candidates.firstOrNull { it.id == selectedId }

            if (locationToDelete != null && newDefault != null) {
                weatherViewModel.replaceDefaultAndDelete(
                    deleteId = locationToDelete.id,
                    newDefault = newDefault,
                )

                SnackbarManager.show(
                    action = R.string.action_undo,
                    duration = SnackbarDuration.Long,
                    message = context.getString(
                        R.string.message_deleted_location,
                        locationToDelete.name,
                    ),
                    onAction = {
                        // Restore as a normal location to avoid ending up with two defaults.
                        weatherViewModel.restoreLocation(
                            locationToDelete.copy(isDefault = false),
                        )
                    },
                )
            }
        },
        onDismiss = viewModel::hideChooseDefaultDialog,
        show = uiState.isChooseDefaultDialogOpen,
        textConfirm = stringResource(R.string.action_set_default),
        textDismiss = stringResource(R.string.action_cancel),
        textTitle = stringResource(R.string.title_choose_default_location),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    max = 320.dp,
                )
                .verticalScroll(
                    state = rememberScrollState(),
                ),
        ) {
            Text(
                modifier = Modifier.padding(
                    end = 24.dp,
                    start = 24.dp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(
                    R.string.message_choose_default_location,
                    locationToDelete?.name ?: stringResource(R.string.message_choose_default_location_fallback),
                ),
            )

            Spacer(
                modifier = Modifier.width(
                    width = 16.dp,
                ),
            )

            candidates.forEach { location ->
                Row(
                    modifier = Modifier
                        .clickable {
                            selectedId = location.id
                        }
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        onClick = {
                            selectedId = location.id
                        },
                        selected = location.id == selectedId,
                    )

                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        text = location.name,
                    )
                }
            }
        }
    }
}
