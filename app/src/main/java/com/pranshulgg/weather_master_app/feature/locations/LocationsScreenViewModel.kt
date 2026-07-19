package com.pranshulgg.weather_master_app.feature.locations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsScreenViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherRepositoryProvider: WeatherRepositoryProvider
) : ViewModel() {
    val allLocationsWeather = locationsRepo.getWeatherForAllLocations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 5_000,
        ),
        initialValue = emptyList(),
    )

    fun updateDefaultLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.updateDefaultLocation(id)
        }
    }

    fun updateLocationsOrder(orderedIds: List<String>) {
        viewModelScope.launch {
            locationsRepo.updateLocationsOrder(orderedIds)
        }
    }

    fun startReordering() {
        _uiState.value = _uiState.value.copy(
            isReordering = true,
        )
    }

    fun stopReordering() {
        _uiState.value = _uiState.value.copy(
            isReordering = false,
        )
    }

    private val _uiState = mutableStateOf(LocationsScreenUiState())
    val uiState: State<LocationsScreenUiState> = _uiState

    fun showConfirmationDialog() {
        _uiState.value = _uiState.value.copy(
            isConfirmationDialogOpen = true,
        )
    }

    fun hideConfirmationDialog() {
        _uiState.value = _uiState.value.copy(
            isConfirmationDialogOpen = false,
        )
    }

    fun showChooseDefaultDialog() {
        _uiState.value = _uiState.value.copy(
            isChooseDefaultDialogOpen = true,
        )
    }

    fun hideChooseDefaultDialog() {
        _uiState.value = _uiState.value.copy(
            isChooseDefaultDialogOpen = false,
        )
    }

    fun setLongClickedLocation(location: Location) {
        _uiState.value = _uiState.value.copy(
            longClickedLocation = location,
        )
    }

    fun showBottomSheet(location: Location) {
        setLongClickedLocation(location)
        _uiState.value = _uiState.value.copy(
            isBottomSheetOpen = true,
        )
    }

    fun hideBottomSheet() {
        _uiState.value = _uiState.value.copy(
            isBottomSheetOpen = false,
        )
    }

    fun saveDeviceLocation(
        onSaved: () -> Unit = {},
    ) {
        _uiState.value = _uiState.value.copy(
            isDeviceLocationLoading = true,
        )
        viewModelScope.launch {
            try {
                locationsRepo.saveDeviceLocation()
                onSaved()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    message = AppException.CurrentLocationUnavailable().toMessageRes(),
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    isDeviceLocationLoading = false,
                )
            }
        }
    }

    fun refreshAllLocations(
        onComplete: () -> Unit = {},
    ) {
        if (_uiState.value.isRefreshing) return
        _uiState.value = _uiState.value.copy(
            isRefreshing = true,
        )
        viewModelScope.launch {
            try {
                // Move device-location pin to current position first, so list read below picks up
                // its new coordinates and weather is fetched for where device is now.
                try {
                    locationsRepo.updateDeviceLocationPosition()
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                }

                val locations = locationsRepo.getLocationsOnce()
                locations.forEach { location ->
                    val repo = weatherRepositoryProvider.getRepository(location.source)
                    repo.getWeather(
                        isForceRefresh = true,
                        isManualRefresh = true,
                        location = location,
                    )
                }
                onComplete()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            } finally {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                )
            }
        }
    }
}
