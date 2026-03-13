package com.muslimbro.core.data.repository

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes as AdhanPrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.data.database.dao.PrayerTimesDao
import com.muslimbro.core.data.database.entity.PrayerTimesEntity
import com.muslimbro.core.domain.model.CalculationMethod
import com.muslimbro.core.domain.model.Madhab
import com.muslimbro.core.domain.model.PrayerTimes
import com.muslimbro.core.domain.model.toAdhanMadhab
import com.muslimbro.core.domain.model.toAdhanParameters
import com.muslimbro.core.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.toJavaInstant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimesRepositoryImpl @Inject constructor(
    private val prayerTimesDao: PrayerTimesDao
) : PrayerTimesRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val zoneId = ZoneId.systemDefault()

    override fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        method: CalculationMethod,
        madhab: Madhab
    ): Flow<AppResult<PrayerTimes>> = flow {
        emit(AppResult.Loading)
        try {
            val dateStr = date.format(dateFormatter)
            val cached = prayerTimesDao.getPrayerTimes(dateStr, latitude, longitude, method.name, madhab.name)
            cached.collect { entity ->
                if (entity != null) {
                    emit(AppResult.Success(entity.toDomain()))
                } else {
                    val calculated = calculatePrayerTimes(latitude, longitude, date, method, madhab)
                    prayerTimesDao.insertPrayerTimes(calculated.toEntity())
                    emit(AppResult.Success(calculated))
                }
            }
        } catch (e: Exception) {
            emit(AppResult.Error(e, "Failed to get prayer times"))
        }
    }

    override suspend fun getPrayerTimesForWeek(
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        method: CalculationMethod,
        madhab: Madhab
    ): List<PrayerTimes> {
        return (0..6).map { offset ->
            val date = startDate.plusDays(offset.toLong())
            calculatePrayerTimes(latitude, longitude, date, method, madhab).also { times ->
                prayerTimesDao.insertPrayerTimes(times.toEntity())
            }
        }
    }

    override suspend fun invalidateCache(date: LocalDate) {
        prayerTimesDao.deletePrayerTimesForDate(date.format(dateFormatter))
    }

    private fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        method: CalculationMethod,
        madhab: Madhab
    ): PrayerTimes {
        val coordinates = Coordinates(latitude, longitude)
        val dc = DateComponents(date.year, date.monthValue, date.dayOfMonth)
        val parameters = method.toAdhanParameters().copy(madhab = madhab.toAdhanMadhab())
        
        val times = AdhanPrayerTimes(coordinates, dc, parameters)

        @OptIn(kotlin.time.ExperimentalTime::class)
        fun kotlin.time.Instant.toJavaLocalTime(): LocalTime =
            toJavaInstant().atZone(zoneId).toLocalTime()

        @OptIn(kotlin.time.ExperimentalTime::class)
        return PrayerTimes(
            date = date,
            fajr = times.fajr.toJavaLocalTime(),
            sunrise = times.sunrise.toJavaLocalTime(),
            dhuhr = times.dhuhr.toJavaLocalTime(),
            asr = times.asr.toJavaLocalTime(),
            maghrib = times.maghrib.toJavaLocalTime(),
            isha = times.isha.toJavaLocalTime(),
            latitude = latitude,
            longitude = longitude,
            calculationMethod = method,
            madhab = madhab
        )
    }

    private fun PrayerTimesEntity.toDomain(): PrayerTimes {
        val dateParsed = LocalDate.parse(date, dateFormatter)
        return PrayerTimes(
            date = dateParsed,
            fajr = LocalTime.parse(fajr),
            sunrise = LocalTime.parse(sunrise),
            dhuhr = LocalTime.parse(dhuhr),
            asr = LocalTime.parse(asr),
            maghrib = LocalTime.parse(maghrib),
            isha = LocalTime.parse(isha),
            latitude = latitude,
            longitude = longitude,
            calculationMethod = CalculationMethod.valueOf(calculationMethod),
            madhab = Madhab.valueOf(madhab)
        )
    }

    private fun PrayerTimes.toEntity(): PrayerTimesEntity = PrayerTimesEntity(
        date = date.format(dateFormatter),
        latitude = latitude,
        longitude = longitude,
        calculationMethod = calculationMethod.name,
        madhab = madhab.name,
        fajr = fajr.toString(),
        sunrise = sunrise.toString(),
        dhuhr = dhuhr.toString(),
        asr = asr.toString(),
        maghrib = maghrib.toString(),
        isha = isha.toString()
    )
}
