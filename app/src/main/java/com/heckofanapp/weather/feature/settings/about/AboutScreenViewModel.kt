package com.heckofanapp.weather.feature.settings.about

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.toAppException
import com.heckofanapp.weather.core.model.domain.toMessageRes
import com.heckofanapp.weather.core.model.weather.WeatherCondition
import com.heckofanapp.weather.core.network.github.GithubRepository
import com.heckofanapp.weather.core.prefs.PreferencesHelper
import com.heckofanapp.weather.core.prefs.SELECTED_LOCATION_ID_KEY
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager
import com.heckofanapp.weather.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutScreenViewModel @Inject constructor(
    private val githubRepository: GithubRepository,
    private val locationsRepository: LocationsRepository,
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var currentCondition by mutableStateOf<WeatherCondition?>(null)
        private set

    init {
        viewModelScope.launch {
            currentCondition = try {
                val selectedId = PreferencesHelper.getString(SELECTED_LOCATION_ID_KEY)
                val locationId = selectedId ?: locationsRepository.getDefaultLocation().filterNotNull().first().id
                locationsRepository.getWeatherForLocation(locationId).current.weatherCondition
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun isNewVersionAvailable(
        currentTag: String,
        onAction: () -> Unit,
    ) {
        loading = true
        val result = try {
            githubRepository.isNewVersionAvailable(
                currentTag = currentTag,
            )
        } catch (e: Exception) {
            val appExpectation = e.toAppException()
            SnackbarManager.show(
                message = appExpectation.toMessageRes(),
            )
            return
        } finally {
            loading = false
        }

        if (result) {
            SnackbarManager.show(
                action = R.string.action_view,
                duration = SnackbarDuration.Indefinite,
                message = R.string.message_new_version_available,
                onAction = {
                    onAction()
                },
            )
        } else {
            SnackbarManager.show(
                message = R.string.message_using_latest_version,
            )
        }
    }
}
