package com.muslimbro.core.domain.usecase

import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.PrayerTimes
import com.muslimbro.core.domain.repository.LocationRepository
import com.muslimbro.core.domain.repository.PrayerTimesRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class GetPrayerTimesUseCase(
    private val prayerTimesRepository: PrayerTimesRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<AppResult<PrayerTimes>> {
        return combine(
            locationRepository.getCurrentLocation(),
            settingsRepository.getUserSettings()
        ) { locationResult, settings ->
            Pair(locationResult, settings)
        }.flatMapLatest { (locationResult, settings) ->
            when (locationResult) {
                is AppResult.Success -> prayerTimesRepository.getPrayerTimes(
                    latitude = locationResult.data.latitude,
                    longitude = locationResult.data.longitude,
                    date = date,
                    method = settings.calculationMethod,
                    madhab = settings.madhab
                )
                is AppResult.Error -> flowOf(AppResult.Error(locationResult.exception, "Location unavailable"))
                is AppResult.Loading -> flowOf(AppResult.Loading)
            }
        }
    }
}
