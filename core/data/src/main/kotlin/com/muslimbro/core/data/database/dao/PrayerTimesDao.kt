package com.muslimbro.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muslimbro.core.data.database.entity.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerTimesDao {
    @Query("""
        SELECT * FROM prayer_times
        WHERE date = :date
        AND abs(latitude - :lat) < 0.01
        AND abs(longitude - :lng) < 0.01
        AND calculationMethod = :method
        AND madhab = :madhab
        LIMIT 1
    """)
    fun getPrayerTimes(
        date: String,
        lat: Double,
        lng: Double,
        method: String,
        madhab: String
    ): Flow<PrayerTimesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(entity: PrayerTimesEntity)

    @Query("DELETE FROM prayer_times WHERE date = :date")
    suspend fun deletePrayerTimesForDate(date: String)

    @Query("DELETE FROM prayer_times WHERE cachedAt < :threshold")
    suspend fun deleteExpiredCache(threshold: Long)

    @Query("SELECT * FROM prayer_times WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getPrayerTimesForRange(startDate: String, endDate: String): List<PrayerTimesEntity>
}
