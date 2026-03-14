package com.muslimbro.core.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
     * Seeds Quran data from the asset file into the freshly-created Room DB.
     * Uses direct SQLiteDatabase access + compiled statements inside a transaction
     * to avoid ATTACH DATABASE issues with Room's connection pooling.
     */
    private fun seedQuranData(context: Context, db: SupportSQLiteDatabase) {
        val tempFile = File(context.cacheDir, "quran_seed_tmp.db")
        var sourceDb: SQLiteDatabase? = null
        try {
            context.assets.open("quran_data.db").use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            sourceDb = SQLiteDatabase.openDatabase(
                tempFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY
            )
            db.beginTransaction()
            try {
                seedSurahs(sourceDb, db)
                seedVerses(sourceDb, db)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            // Rebuild FTS index from the populated verses table
            db.execSQL("INSERT INTO verses_fts(verses_fts) VALUES('rebuild')")
        } catch (_: Exception) {
            // Seed failure is non-fatal — Quran screen will show "no data" error
        } finally {
            sourceDb?.close()
            tempFile.delete()
        }
    }

    private fun seedSurahs(sourceDb: SQLiteDatabase, db: SupportSQLiteDatabase) {
        val cursor = sourceDb.rawQuery(
            "SELECT number, name, nameArabic, nameTranslation, revelationType, versesCount FROM surahs ORDER BY number",
            null
        )
        val stmt = db.compileStatement(
            "INSERT OR IGNORE INTO surahs (number, name, nameArabic, nameTranslation, revelationType, versesCount) VALUES (?, ?, ?, ?, ?, ?)"
        )
        cursor.use {
            while (it.moveToNext()) {
                stmt.bindLong(1, it.getLong(0))
                stmt.bindString(2, it.getString(1))
                stmt.bindString(3, it.getString(2))
                stmt.bindString(4, it.getString(3))
                stmt.bindString(5, it.getString(4))
                stmt.bindLong(6, it.getLong(5))
                stmt.executeInsert()
            }
        }
        stmt.close()
    }

    private fun seedVerses(sourceDb: SQLiteDatabase, db: SupportSQLiteDatabase) {
        val cursor = sourceDb.rawQuery(
            "SELECT id, surahNumber, verseNumber, textUthmani, textSimple, juz, hizb, sajda FROM verses ORDER BY id",
            null
        )
        val stmt = db.compileStatement(
            "INSERT OR IGNORE INTO verses (id, surahNumber, verseNumber, textUthmani, textSimple, juz, hizb, sajda) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        )
        cursor.use {
            while (it.moveToNext()) {
                stmt.bindLong(1, it.getLong(0))
                stmt.bindLong(2, it.getLong(1))
                stmt.bindLong(3, it.getLong(2))
                stmt.bindString(4, it.getString(3))
                stmt.bindString(5, it.getString(4))
                stmt.bindLong(6, it.getLong(5))
                stmt.bindLong(7, it.getLong(6))
                stmt.bindLong(8, it.getLong(7))
                stmt.executeInsert()
            }
        }
        stmt.close()
    }

    @Provides
    fun providePrayerTimesDao(db: MuslimBroDatabase) = db.prayerTimesDao()

    @Provides
    fun provideQuranDao(db: MuslimBroDatabase) = db.quranDao()

    @Provides
    fun provideSearchDao(db: MuslimBroDatabase) = db.searchDao()
}
