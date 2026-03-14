package com.muslimbro.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.domain.model.CalculationMethod
import com.muslimbro.core.domain.model.Madhab
import com.muslimbro.core.domain.model.UserLocation
import com.muslimbro.core.domain.model.UserSettings
import com.muslimbro.core.domain.repository.LocationRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isLoading: Boolean = false,
    val savedLocation: UserLocation? = null,
    val locationQuery: String = "",
    val locationResults: List<UserLocation> = emptyList(),
    val isSearchingLocation: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        settingsRepository.getUserSettings()
            .onEach { settings ->
                _uiState.value = _uiState.value.copy(settings = settings, isLoading = false)
            }
            .launchIn(viewModelScope)

        locationRepository.getSavedLocation()
            .onEach { location ->
                _uiState.value = _uiState.value.copy(savedLocation = location)
            }
            .launchIn(viewModelScope)
    }

    fun updateCalculationMethod(method: CalculationMethod) {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                _uiState.value.settings.copy(calculationMethod = method)
            )
        }
    }

    fun updateMadhab(madhab: Madhab) {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                _uiState.value.settings.copy(madhab = madhab)
            )
        }
    }

    fun updateDarkMode(isDark: Boolean?) {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                _uiState.value.settings.copy(isDarkMode = isDark)
            )
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                _uiState.value.settings.copy(language = language)
            )
        }
    }

    fun updateDefaultTranslation(edition: String) {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                _uiState.value.settings.copy(defaultTranslationEdition = edition)
            )
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                _uiState.value.settings.copy(notificationsEnabled = enabled)
            )
        }
    }

    fun onLocationQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(locationQuery = query, locationResults = emptyList())
    }

    fun searchLocation() {
        val query = _uiState.value.locationQuery.trim()
        if (query.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchingLocation = true)
            val results = locationRepository.geocodeLocation(query)
            _uiState.value = _uiState.value.copy(
                locationResults = results,
                isSearchingLocation = false
            )
        }
    }

    fun saveLocation(location: UserLocation) {
        viewModelScope.launch {
            locationRepository.saveManualLocation(location)
            _uiState.value = _uiState.value.copy(
                locationQuery = "",
                locationResults = emptyList()
            )
        }
    }

    fun useGpsLocation() {
        viewModelScope.launch {
            locationRepository.clearManualLocation()
            _uiState.value = _uiState.value.copy(
                locationQuery = "",
                locationResults = emptyList()
            )
        }
    }
}
