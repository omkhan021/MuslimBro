package com.muslimbro.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.muslimbro.core.data.database.dao.PrayerTimesDao
import com.muslimbro.core.data.database.dao.QuranDao
import com.muslimbro.core.data.database.dao.SearchDao
import com.muslimbro.core.data.database.entity.AudioDownloadEntity
import com.muslimbro.core.data.database.entity.BookmarkEntity
import com.muslimbro.core.data.database.entity.PrayerTimesEntity
import com.muslimbro.core.data.database.entity.ReciterEntity
import com.muslimbro.core.data.database.entity.SurahEntity
import com.muslimbro.core.data.database.entity.TranslationEntity
import com.muslimbro.core.data.database.entity.TranslationFtsEntity
import com.muslimbro.core.data.database.entity.TranslationTextEntity
import com.muslimbro.core.data.database.entity.VerseEntity
import com.muslimbro.core.data.database.entity.VerseFtsEntity
import com.muslimbro.core.data.database.entity.WordEntity

@Database(
    entities = [
        PrayerTimesEntity::class,
        SurahEntity::class,
        VerseEntity::class,
        VerseFtsEntity::class,
        WordEntity::class,
        TranslationEntity::class,
        TranslationTextEntity::class,
        TranslationFtsEntity::class,
        ReciterEntity::class,
        AudioDownloadEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MuslimBroDatabase : RoomDatabase() {
    abstract fun prayerTimesDao(): PrayerTimesDao
    abstract fun quranDao(): QuranDao
    abstract fun searchDao(): SearchDao

    companion object {
        const val DATABASE_NAME = "muslim_bro.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Future migrations go here
            }
        }
    }
}
