package com.heckofanapp.weather.feature.shared

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.domain.toAppException
import com.heckofanapp.weather.core.model.domain.toMessageRes
import com.heckofanapp.weather.core.model.domain.weather.WeatherBlock
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.model.weather.WeatherResult
import com.heckofanapp.weather.core.model.weather.air.AirQualityResult
import com.heckofanapp.weather.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.heckofanapp.weather.core.prefs.PreferencesHelper
import com.heckofanapp.weather.core.prefs.SELECTED_LOCATION_ID_KEY
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.heckofanapp.weather.data.provider.WeatherRepositoryProvider
import com.heckofanapp.weather.data.repository.LocationsRepository
import com.heckofanapp.weather.data.repository.WeatherBlocksRepository
import com.heckofanapp.weather.data.repository.WeatherDataReconcilerRepository
import com.heckofanapp.weather.data.repository.WeatherUnitsRepository
import com.heckofanapp.weather.data.worker.WeatherUpdateScheduler
import com.heckofanapp.weather.feature.main.MainScreenWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepository: LocationsRepository,
    appWeatherUnitsRepo: WeatherUnitsRepository,
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val openMeteoAqiRepository: OpenMeteoAqiRepository,
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val activeLocationId = MutableStateFlow<String?>(null)

    private var _uiState = mutableStateOf(MainScreenWeatherUiState())
    private var weatherJob: Job? = null

    val uiState: State<MainScreenWeatherUiState> = _uiState

    init {
        observeActiveLocation()
        observeActiveLocationWeather()

        // Load default on start.
        viewModelScope.launch {
            if (_uiState.value.activeLocation == null && _uiState.value.weather == null && !_uiState.value.isInitialized) {

                val isLocationsEmpty = locationsRepository.isLocationsEmpty()
                if (isLocationsEmpty) {
                    // Locations Empty? not possible, likely a first launch
                    _uiState.value = uiState.value.copy(
                        isInitialized = true,
                    )
                }
                val default = locationsRepository.getDefaultLocation().filterNotNull().first()
                setActiveLocation(default)
            }
            loadBlocks()

            // Move device-location pin to current position on every cold start, then refresh so its
            // title and weather reflect where device is now.  Runs after active location is set to
            // tell whether moved pin is on screen.
            try {
                val moved = locationsRepository.updateDeviceLocationPosition()
                if (moved != null) {
                    if (_uiState.value.activeLocation?.id == moved.id) {
                        // On Screen: Refresh through main flow so title and weather update.
                        updateActiveLocation(
                            location = moved,
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
        locationsRepository.getLocations().distinctUntilChanged()
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
                        updateActiveLocation(
                            location = moved,
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
            locationsRepository.deleteLocation(id)

            if (_uiState.value.activeLocation?.id == id) {
                setActiveLocation(_uiState.value.locations.first { it.isDefault })
            }
        }
    }

    fun restoreLocation(location: Location) {
        viewModelScope.launch {
            locationsRepository.saveLocation(location)
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
            locationsRepository.updateDefaultLocation(newDefault.id)
            locationsRepository.deleteLocation(deleteId)

            if (_uiState.value.activeLocation?.id == deleteId) {
                setActiveLocation(
                    location = newDefault.copy(
                        isDefault = true,
                    ),
                )
            }
        }
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
        updateActiveLocation(
            location = location,
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
            locationsRepository.updateSourceForLocation(location.id, source)
            val allowForceRefresh = location.source != source

            if (allowForceRefresh) {
                weatherDataReconcilerRepository.cleanUpStaleData(
                    locationId = location.id,
                    previousSource = location.source,
                )
            }
            updateActiveLocation(
                location = updatedLocation,
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
        return locationsRepository.updateDeviceLocationPosition()
    }

    /**
     * Sets active location and keeps [activeLocationId] in sync so reactive weather flow
     * switches to newly selected location.
     */
    private fun updateActiveLocation(
        location: Location,
    ) {
        _uiState.value = _uiState.value.copy(
            activeLocation = location,
        )
        activeLocationId.value = location.id
    }

    /**
     * Observes active location's row and drives [MainScreenWeatherUiState.activeLocation] from it
     * so main title reflects in-place changes (rename, moved device pin) that keep the same id.
     * Emissions are skipped while location no longer exists, leaving last known value intact.
     */
    @OptIn(
        ExperimentalCoroutinesApi::class,
    )
    private fun observeActiveLocation() {
        activeLocationId
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { locationId ->
                locationsRepository.observeLocation(locationId)
            }
            .onEach { location ->
                if (location != null) {
                    _uiState.value = _uiState.value.copy(
                        activeLocation = location,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Observes active location's persisted weather and drives [MainScreenWeatherUiState.weather]
     * from it, so main screen reflects every database write (manual, worker, or off-screen refresh)
     * same way Locations list does.  Emissions are skipped while location has no persisted weather
     * yet, leaving initial loading state intact.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeActiveLocationWeather() {
        activeLocationId
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { locationId ->
                locationsRepository.observeWeatherForLocation(locationId)
            }
            .onEach { weather ->
                if (weather != null) {
                    _uiState.value = _uiState.value.copy(
                        weather = weather,
                    )
                }
            }
            .launchIn(viewModelScope)
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

                // Displayed weather is driven by the reactive database flow, which already reflects
                // any cached row, so only the error flag needs setting here.
                _uiState.value = _uiState.value.copy(
                    isError = true,
                )
            }

            is WeatherResult.RefreshNotAvailable -> {
                SnackbarManager.show(
                    arguments = MANUAL_REFRESH_MINUTES - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - result.cacheWeather.current.lastUpdatedInMilli),
                    message = R.string.weather_refresh_delay,
                )
            }

            is WeatherResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    isInitialized = true,
                )

                // Push freshly fetched data to widgets directly; _uiState.weather is updated
                // asynchronously by reactive flow and may not reflect this result yet.
                if (location.isDefault) {
                    WeatherUpdateScheduler.updateAllWidgets(
                        context = context,
                        data = result.weather,
                        units = _uiState.value.weatherUnits,
                    )
                }
            }
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
