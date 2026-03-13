package com.muslimbro.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTimesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO format: yyyy-MM-dd
    val latitude: Double,
    val longitude: Double,
    val calculationMethod: String,
    val madhab: String,
    val fajr: String,   // HH:mm
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val name: String,
    val nameArabic: String,
    val nameTranslation: String,
    val revelationType: String,
    val versesCount: Int
)

@Entity(tableName = "verses")
data class VerseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val verseNumber: Int,
    val textUthmani: String,
    val textSimple: String,
    val juz: Int,
    val hizb: Int,
    val sajda: Boolean = false
)

@Fts4(contentEntity = VerseEntity::class)
@Entity(tableName = "verses_fts")
data class VerseFtsEntity(
    val textUthmani: String,
    val textSimple: String
)

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = VerseEntity::class,
        parentColumns = ["id"],
        childColumns = ["verseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("verseId")]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val verseId: Long,
    val position: Int,
    val text: String,
    val audioUrl: String?,
    val audioOffset: Long?
)

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val language: String,
    val edition: String,
    val name: String,
    val fetchedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "translation_texts",
    foreignKeys = [ForeignKey(
        entity = VerseEntity::class,
        parentColumns = ["id"],
        childColumns = ["verseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("verseId"), Index("edition")]
)
data class TranslationTextEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val verseId: Long,
    val surahNumber: Int,
    val verseNumber: Int,
    val text: String,
    val edition: String
)

@Fts4(contentEntity = TranslationTextEntity::class)
@Entity(tableName = "translation_fts")
data class TranslationFtsEntity(
    val text: String
)

@Entity(tableName = "reciters")
data class ReciterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val style: String?,
    val urlIdentifier: String,
    val isFajrSpecial: Boolean = false
)

@Entity(tableName = "audio_downloads")
data class AudioDownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val reciterId: Int,
    val localPath: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val sizeBytes: Long = 0
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val verseNumber: Int,
    val note: String?,
    val createdAt: Long = System.currentTimeMillis()
)
