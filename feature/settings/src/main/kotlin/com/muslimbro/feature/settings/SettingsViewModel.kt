package com.muslimbro.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.domain.model.CalculationMethod
import com.muslimbro.core.domain.model.Madhab
import com.muslimbro.core.domain.model.UserSettings
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
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        settingsRepository.getUserSettings()
            .onEach { settings ->
                _uiState.value = SettingsUiState(settings = settings, isLoading = false)
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
}
