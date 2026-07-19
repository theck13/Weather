package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.air.AirQualityResult
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.pranshulgg.weather_master_app.core.prefs.PreferencesHelper
import com.pranshulgg.weather_master_app.core.prefs.SELECTED_LOCATION_ID_KEY
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.pranshulgg.weather_master_app.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.worker.WeatherUpdateScheduler
import com.pranshulgg.weather_master_app.feature.main.MainScreenWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    appWeatherUnitsRepo: WeatherUnitsRepository,
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val openMeteoAqiRepository: OpenMeteoAqiRepository,
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var _uiState = mutableStateOf(MainScreenWeatherUiState())
    private var weatherJob: Job? = null

    val uiState: State<MainScreenWeatherUiState> = _uiState

    init {
        // Load default on start.
        viewModelScope.launch {
            if (_uiState.value.activeLocation == null && _uiState.value.weather == null && !_uiState.value.isInitialized) {

                val isLocationsEmpty = locationsRepo.isLocationsEmpty()
                if (isLocationsEmpty) {
                    // Locations Empty? not possible, likely a first launch
                    _uiState.value = uiState.value.copy(
                        isInitialized = true,
                    )
                }
                val default = locationsRepo.getDefaultLocation().filterNotNull().first()
                setActiveLocation(default)
            }
            loadBlocks()

            // Move device-location pin to current position on every cold start, then refresh so its
            // title and weather reflect where device is now.  Runs after active location is set to
            // tell whether moved pin is on screen.
            try {
                val moved = locationsRepo.updateDeviceLocationPosition()
                if (moved != null) {
                    if (_uiState.value.activeLocation?.id == moved.id) {
                        // On Screen: Refresh through main flow so title and weather update.
                        _uiState.value = _uiState.value.copy(
                            activeLocation = moved,
                        )
                        getWeather(
                            location = moved,
                            source = moved.source,
                        )
                    } else {
                        // Off Screen: Refresh its stored weather for new position.
                        repo.getRepository(moved.source).getWeather(
                            isForceRefresh = true,
                            isManualRefresh = true,
                            location = moved,
                        )
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }
        }

        // KEEP TRACK OF ALL LOCATIONS
        locationsRepo.getLocations().distinctUntilChanged()
            .onEach { locations ->
                val previous = _uiState.value.locations

                if (previous.isNotEmpty()) {
                    val newLocation = locations.firstOrNull { new ->
                        previous.none { it.id == new.id }
                    }

                    newLocation?.let {
                        if (_uiState.value.isLoading.not()) {
                            setActiveLocation(it)
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    locations = locations,
                )
            }
            .launchIn(viewModelScope)

        // Keep track of app units.
        appWeatherUnitsRepo.getUnits().distinctUntilChanged().onEach {
            _uiState.value = _uiState.value.copy(
                weatherUnits = it,
            )
        }.launchIn(viewModelScope)
    }

    fun getWeather(
        isForceRefresh: Boolean = false,
        isManualRefresh: Boolean = false,
        location: Location,
        source: WeatherSource,
    ) {
        setLoading(
            isLoading = true,
        )
        weatherJob?.cancel()
        val startTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(
            isError = false,
        )

        weatherJob = viewModelScope.launch {
            // For device-location pin, move it to current position first and fetch weather for
            // where device is now (rather than stale passed-in location).
            var locationNow = location

            if (location.isDeviceLocation && isManualRefresh) {
                try {
                    handleDeviceLocation()?.let { moved ->
                        locationNow = moved
                        _uiState.value = _uiState.value.copy(
                            activeLocation = moved,
                        )
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                }
            }

            // Run separately.
            if (_uiState.value.isError.not()) {
                launch {
                    handleAirQuality(
                        isManualRefresh = isManualRefresh,
                        location = locationNow,
                    )
                }
            }

            handleWeatherData(
                isForceRefresh = isForceRefresh,
                isManualRefresh = isManualRefresh,
                location = locationNow,
                source = source,
            )

            val elapsed = System.currentTimeMillis() - startTime
            val minLoadingTime = 1000L // 1s

            // Prevents loader flicker when responses return too quickly
            if (elapsed < minLoadingTime) {
                delay((minLoadingTime - elapsed).milliseconds)
            }

            setLoading(
                isLoading = false,
            )
        }
    }

    fun deleteLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.deleteLocation(id)

            if (_uiState.value.activeLocation?.id == id) {
                setActiveLocation(_uiState.value.locations.first { it.isDefault })
            }
        }
    }

    fun restoreLocation(location: Location) {
        viewModelScope.launch {
            locationsRepo.saveLocation(location)
        }
    }

    /**
     * Promotes [newDefault] to the default location, then deletes the current default [deleteId].
     * Run sequentially in one coroutine so a default always exists before the delete lands,
     * which also lets us point the active location at the new default explicitly rather than
     * reading the not-yet-updated locations state.
     */
    fun replaceDefaultAndDelete(
        deleteId: String,
        newDefault: Location,
    ) {
        viewModelScope.launch {
            locationsRepo.updateDefaultLocation(newDefault.id)
            locationsRepo.deleteLocation(deleteId)

            if (_uiState.value.activeLocation?.id == deleteId) {
                setActiveLocation(
                    location = newDefault.copy(
                        isDefault = true,
                    ),
                )
            }
        }
    }

    /**
     * Reads active location's weather from cache (e.g. after a bulk refresh writes new data to database).
     * Reuses [getWeather] with default flags to get newly-written cache rather than issuing another network request.
     */
    fun reloadActiveLocation() {
        val active = _uiState.value.activeLocation ?: return
        // Prefer freshest copy of active location (e.g. device pin whose coordinates were just
        // moved) so reload weather for current position, not a stale snapshot.
        val fresh = _uiState.value.locations.firstOrNull { it.id == active.id } ?: active

        if (fresh != active) {
            _uiState.value = _uiState.value.copy(
                activeLocation = fresh,
            )
        }
        getWeather(
            location = fresh,
            source = fresh.source,
        )
    }

    fun setLoading(
        isLoading: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading,
        )
    }

    fun setActiveLocation(
        location: Location,
    ) {
        _uiState.value = _uiState.value.copy(
            activeLocation = location,
        )
        PreferencesHelper.setString(SELECTED_LOCATION_ID_KEY, location.id)
        getWeather(
            location = location,
            source = location.source,
        )
    }

    fun updateSourceForLocation(
        location: Location,
        source: WeatherSource,
    ) {
        val updatedLocation = location.copy(
            source = source,
        )

        viewModelScope.launch {
            locationsRepo.updateSourceForLocation(location.id, source)
            val allowForceRefresh = location.source != source

            if (allowForceRefresh) {
                weatherDataReconcilerRepository.cleanUpStaleData(
                    locationId = location.id,
                    previousSource = location.source,
                )
            }
            _uiState.value = _uiState.value.copy(
                activeLocation = updatedLocation,
            )
            getWeather(
                isForceRefresh = allowForceRefresh,
                location = updatedLocation,
                source = source,
            )
        }
    }

    fun saveBlocks(
        isDaily: Boolean = false,
        items: List<WeatherBlock>,
    ) {
        _uiState.value = _uiState.value.copy(
            blocks = items,
        )

        viewModelScope.launch {
            weatherBlocksRepository.saveBlocks(items.map {
                WeatherBlock(
                    id = it.id,
                    isDaily = isDaily,
                    isHidden = false,
                    position = it.position,
                    type = it.type,
                )
            }, isDaily)
        }
    }

    suspend fun loadBlocks() {
        val loadedBlocks = weatherBlocksRepository.loadBlocks()
        _uiState.value = _uiState.value.copy(
            blocks = loadedBlocks,
        )
    }

    private suspend fun handleDeviceLocation(): Location? {
        return locationsRepo.updateDeviceLocationPosition()
    }

    private suspend fun handleWeatherData(
        isForceRefresh: Boolean,
        isManualRefresh: Boolean,
        location: Location,
        source: WeatherSource,
    ) {
        val repo = repo.getRepository(
            source = source,
        )

        when (val result = repo.getWeather(
                isForceRefresh = isForceRefresh,
                isManualRefresh = isManualRefresh,
                location = location,
            )
        ) {
            is WeatherResult.Error -> {
                val appExpectation = result.exception.toAppException()
                SnackbarManager.show(
                    message = appExpectation.toMessageRes(),
                )

                _uiState.value = _uiState.value.copy(
                    isError = true,
                    weather = result.cacheWeather,
                )
            }

            is WeatherResult.RefreshNotAvailable -> {
                SnackbarManager.show(
                    arguments = MANUAL_REFRESH_MINUTES - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - (result.cacheWeather?.current?.lastUpdatedInMilli ?: 0)),
                    message = R.string.weather_refresh_delay,
                )
            }

            is WeatherResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    isInitialized = true,
                    weather = result.weather,
                )
            }
        }

        if (location.isDefault && !_uiState.value.isError && _uiState.value.weather != null) {
            WeatherUpdateScheduler.updateAllWidgets(
                context = context,
                data = _uiState.value.weather!!,
                units = _uiState.value.weatherUnits,
            )
        }
    }

    private suspend fun handleAirQuality(
        isManualRefresh: Boolean,
        location: Location,
    ) {
        when (val result = openMeteoAqiRepository.getAirQuality(
            isManualRefresh = isManualRefresh,
            location = location,
        )) {
            is AirQualityResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    airQuality = result.airquality,
                )
            }

            // Fail silently, just don't show the Air Quality card.
            is AirQualityResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    airQuality = result.cacheAirQuality,
                )
            }
        }
    }
}
