package com.heckofanapp.weather.feature.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heckofanapp.weather.core.network.sources.address.nominatim.json.NominatimRepository
import com.heckofanapp.weather.data.provider.devicelocation.DeviceLocation
import com.heckofanapp.weather.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class IntroScreenViewModel @Inject constructor(
    val locationsRepo: LocationsRepository,
    @ApplicationContext private val context: Context,
    private val nominatimRepository: NominatimRepository,
) : ViewModel() {
    fun saveDeviceLocation(
        location: DeviceLocation,
    ) {
        viewModelScope.launch {
            val address = nominatimRepository.getAddress(
                latitude = location.latitude,
                longitude = location.longitude,
            )

            if (address != null && address.city != null) {
                locationsRepo.saveLocation(
                    location = location.toDomain(context).copy(
                        country = address.country,
                        countryCode = address.countryCode,
                        name = address.city,
                    )
                )
            } else {
                locationsRepo.saveLocation(
                    location = location.toDomain(
                        context = context,
                    ),
                )
            }
        }
    }
}
