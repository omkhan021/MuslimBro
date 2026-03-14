package com.muslimbro.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMuslimBroDatabase(@ApplicationContext context: Context): MuslimBroDatabase =
        Room.databaseBuilder(context, MuslimBroDatabase::class.java, MuslimBroDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    seedQuranData(context, db)
                }
            })
            .build()

    /**
     * Seeds Quran data from the asset DB into the Room-created DB.
     * Room creates the DB with the correct schema first (via onCreate), then we ATTACH
     * the asset file and bulk-copy the data. This avoids schema hash mismatches that
     * occur when using createFromAsset with a non-Room-generated asset file.
     */
    private fun seedQuranData(context: Context, db: SupportSQLiteDatabase) {
        val tempFile = File(context.cacheDir, "quran_seed_tmp.db")
        try {
            context.assets.open("quran_data.db").use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            db.execSQL("ATTACH DATABASE '${tempFile.absolutePath}' AS quran_source")
            db.execSQL(
                "INSERT OR IGNORE INTO surahs " +
                    "(number, name, nameArabic, nameTranslation, revelationType, versesCount) " +
                    "SELECT number, name, nameArabic, nameTranslation, revelationType, versesCount " +
                    "FROM quran_source.surahs"
            )
            db.execSQL(
                "INSERT OR IGNORE INTO verses " +
                    "(id, surahNumber, verseNumber, textUthmani, textSimple, juz, hizb, sajda) " +
                    "SELECT id, surahNumber, verseNumber, textUthmani, textSimple, juz, hizb, sajda " +
                    "FROM quran_source.verses"
            )
            // Rebuild FTS index from the now-populated verses content table
            db.execSQL("INSERT INTO verses_fts(verses_fts) VALUES('rebuild')")
            try {
                db.execSQL(
                    "INSERT OR IGNORE INTO words " +
                        "(id, verseId, position, text, audioUrl, audioOffset) " +
                        "SELECT id, verseId, position, text, audioUrl, audioOffset " +
                        "FROM quran_source.words"
                )
            } catch (_: Exception) {
                // words table may not exist in asset — non-fatal
            }
            db.execSQL("DETACH DATABASE quran_source")
        } catch (e: Exception) {
            // Seed failure is non-fatal — Quran screen will show "no data" error
        } finally {
            tempFile.delete()
        }
    }

    @Provides
    fun providePrayerTimesDao(db: MuslimBroDatabase) = db.prayerTimesDao()

    @Provides
    fun provideQuranDao(db: MuslimBroDatabase) = db.quranDao()

    @Provides
    fun provideSearchDao(db: MuslimBroDatabase) = db.searchDao()
}
