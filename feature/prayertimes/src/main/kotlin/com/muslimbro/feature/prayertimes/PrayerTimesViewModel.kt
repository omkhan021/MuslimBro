package com.muslimbro.feature.prayertimes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.common.toHijriDate
import com.muslimbro.core.domain.model.NextPrayer
import com.muslimbro.core.domain.model.Prayer
import com.muslimbro.core.domain.model.PrayerNotificationSettings
import com.muslimbro.core.domain.model.PrayerTimes
import com.muslimbro.core.domain.repository.LocationRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import com.muslimbro.core.domain.usecase.GetPrayerTimesUseCase
import com.muslimbro.feature.alarms.scheduler.PrayerAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class PrayerTimesUiState(
    val isLoading: Boolean = false,
    val prayerTimes: PrayerTimes? = null,
    val nextPrayer: NextPrayer? = null,
    val hijriDate: com.muslimbro.core.common.HijriDate? = null,
    val locationName: String? = null,
    val error: String? = null,
    val notificationsEnabled: Map<Prayer, Boolean> = Prayer.entries
        .filter { it.isAlarmable }
        .associateWith { true }
)

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
    private val alarmScheduler: PrayerAlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var loadJob: Job? = null

    init {
        locationRepository.getCurrentLocation()
            .onEach { result ->
                if (result is AppResult.Success) {
                    val loc = result.data
                    val name = loc.cityName
                        ?: "%.2f°, %.2f°".format(loc.latitude, loc.longitude)
                    _uiState.value = _uiState.value.copy(locationName = name)
                }
            }
            .launchIn(viewModelScope)

        // Load persisted per-prayer notification settings
        viewModelScope.launch {
            val settings = settingsRepository.getUserSettings().first()
            val notifMap = settings.prayerNotifications.mapValues { it.value.enabled }
            _uiState.value = _uiState.value.copy(notificationsEnabled = notifMap)
        }
    }

    fun loadPrayerTimes(date: LocalDate = LocalDate.now()) {
        loadJob?.cancel()
        loadJob = getPrayerTimesUseCase(date)
            .onEach { result ->
                when (result) {
                    is AppResult.Loading -> _uiState.value = _uiState.value.copy(
                        isLoading = true, error = null
                    )
                    is AppResult.Success -> {
                        val hijri = date.toHijriDate()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            prayerTimes = result.data,
                            hijriDate = hijri,
                            error = null
                        )
                        startCountdown(result.data)
                        alarmScheduler.schedulePrayerAlarms(
                            result.data,
                            _uiState.value.notificationsEnabled
                        )
                    }
                    is AppResult.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: result.exception.message ?: "Unknown error"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startCountdown(prayerTimes: PrayerTimes) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val now = LocalTime.now()
                val nextPrayer = findNextPrayer(prayerTimes, now)
                _uiState.value = _uiState.value.copy(nextPrayer = nextPrayer)
                delay(1000)
            }
        }
    }

    private fun findNextPrayer(times: PrayerTimes, now: LocalTime): NextPrayer? {
        val prayers = listOf(
            Prayer.FAJR to times.fajr,
            Prayer.SUNRISE to times.sunrise,
            Prayer.DHUHR to times.dhuhr,
            Prayer.ASR to times.asr,
            Prayer.MAGHRIB to times.maghrib,
            Prayer.ISHA to times.isha
        )
        val next = prayers.firstOrNull { (_, time) -> time.isAfter(now) }
            ?: prayers.firstOrNull() // wrap around to next day's Fajr
        return next?.let { (prayer, time) ->
            val remaining = if (time.isAfter(now)) {
                ChronoUnit.SECONDS.between(now, time)
            } else {
                ChronoUnit.SECONDS.between(now, time.plusHours(24))
            }
            NextPrayer(prayer, time, remaining)
        }
    }

    fun toggleNotification(prayer: Prayer) {
        val current = _uiState.value.notificationsEnabled.toMutableMap()
        val newEnabled = !(current[prayer] ?: true)
        current[prayer] = newEnabled
        _uiState.value = _uiState.value.copy(notificationsEnabled = current)

        viewModelScope.launch {
            settingsRepository.updatePrayerNotification(
                prayer,
                PrayerNotificationSettings(enabled = newEnabled)
            )
            // Re-schedule alarms reflecting the new toggle state
            _uiState.value.prayerTimes?.let { times ->
                alarmScheduler.schedulePrayerAlarms(times, _uiState.value.notificationsEnabled)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadJob?.cancel()
        countdownJob?.cancel()
    }
}
