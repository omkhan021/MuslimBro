package com.muslimbro.core.domain.repository

import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.CalculationMethod
import com.muslimbro.core.domain.model.Madhab
import com.muslimbro.core.domain.model.PrayerTimes
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PrayerTimesRepository {
    fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        method: CalculationMethod,
        madhab: Madhab
    ): Flow<AppResult<PrayerTimes>>

    suspend fun getPrayerTimesForWeek(
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        method: CalculationMethod,
        madhab: Madhab
    ): List<PrayerTimes>

    suspend fun invalidateCache(date: LocalDate)
}
