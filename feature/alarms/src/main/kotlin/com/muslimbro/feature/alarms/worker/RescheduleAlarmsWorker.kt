package com.muslimbro.feature.alarms.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.Prayer
import com.muslimbro.core.domain.repository.LocationRepository
import com.muslimbro.core.domain.repository.PrayerTimesRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import com.muslimbro.feature.alarms.scheduler.PrayerAlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val alarmScheduler: PrayerAlarmScheduler,
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

            // Schedule for today and next 2 days
            (0..2).forEach { offset ->
                val date = today.plusDays(offset.toLong())
                val times = prayerTimesRepository.getPrayerTimesForWeek(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    startDate = date,
                    method = settings.calculationMethod,
                    madhab = settings.madhab
                ).firstOrNull() ?: return@forEach

                alarmScheduler.schedulePrayerAlarms(
                    prayerTimes = times,
                    notificationsEnabled = settings.prayerNotifications.mapValues { it.value.enabled }
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "RescheduleAlarmsWorker"
    }
}
