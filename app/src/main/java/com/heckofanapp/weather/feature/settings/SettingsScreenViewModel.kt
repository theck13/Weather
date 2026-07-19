package com.heckofanapp.weather.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heckofanapp.weather.core.model.weather.DistanceUnit
import com.heckofanapp.weather.core.model.weather.PrecipitationUnit
import com.heckofanapp.weather.core.model.weather.PressureUnit
import com.heckofanapp.weather.core.model.weather.TemperatureUnit
import com.heckofanapp.weather.core.model.weather.WindUnit
import com.heckofanapp.weather.data.repository.WeatherUnitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val weatherUnitsRepo: WeatherUnitsRepository,
) : ViewModel() {
    val weatherUnits = weatherUnitsRepo.getUnits().stateIn(
        initialValue = null,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 5000L,
        ),
    )

    fun updateDistanceUnit(
        distanceUnit: DistanceUnit,
    ) {
        viewModelScope.launch {
            weatherUnitsRepo.updateDistanceUnit(
                distanceUnit = distanceUnit,
            )
        }
    }

    fun updatePrecipitationUnit(
        precipitationUnit: PrecipitationUnit,
    ) {
        viewModelScope.launch {
            weatherUnitsRepo.updatePrecipitationUnit(
                precipitationUnit = precipitationUnit,
            )
        }
    }

    fun updatePressureUnit(
        pressureUnit: PressureUnit,
    ) {
        viewModelScope.launch {
            weatherUnitsRepo.updatePressureUnit(
                pressureUnit = pressureUnit,
            )
        }
    }

    fun updateTemperatureUnit(
        temperatureUnit: TemperatureUnit,
    ) {
        viewModelScope.launch {
            weatherUnitsRepo.updateTemperatureUnit(
                temperatureUnit = temperatureUnit,
            )
        }
    }

    fun updateWindUnit(
        windUnit: WindUnit,
    ) {
        viewModelScope.launch {
            weatherUnitsRepo.updateWindUnit(
                windUnit = windUnit,
            )
        }
    }
}
