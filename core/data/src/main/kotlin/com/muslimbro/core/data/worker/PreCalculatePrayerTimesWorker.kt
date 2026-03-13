package com.muslimbro.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.muslimbro.core.domain.repository.LocationRepository
import com.muslimbro.core.domain.repository.PrayerTimesRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import com.muslimbro.core.common.AppResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class PreCalculatePrayerTimesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val prayerTimesRepository: PrayerTimesRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val locationResult = locationRepository.getCurrentLocation().first()
            if (locationResult !is AppResult.Success) {
                return Result.retry()
            }
            val location = locationResult.data
            val settings = settingsRepository.getUserSettings().first()
            val today = LocalDate.now()

            prayerTimesRepository.getPrayerTimesForWeek(
                latitude = location.latitude,
                longitude = location.longitude,
                startDate = today,
                method = settings.calculationMethod,
                madhab = settings.madhab
            )
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "PreCalculatePrayerTimesWorker"
    }
}
