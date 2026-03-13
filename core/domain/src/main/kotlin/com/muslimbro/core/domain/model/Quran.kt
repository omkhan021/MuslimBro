package com.muslimbro.core.domain.model

data class Surah(
    val number: Int,
    val name: String,
    val nameArabic: String,
    val nameTranslation: String,
    val revelationType: RevelationType,
    val versesCount: Int,
    val audioUrl: String? = null
)

data class Verse(
    val id: Long,
    val surahNumber: Int,
    val verseNumber: Int,
    val textUthmani: String,
    val textSimple: String,
    val juz: Int,
    val hizb: Int,
    val sajda: Boolean = false
)

data class Translation(
    val id: Long,
    val verseId: Long,
    val surahNumber: Int,
    val verseNumber: Int,
    val text: String,
    val language: String,
    val edition: String
)

data class Word(
    val id: Long,
    val verseId: Long,
    val position: Int,
    val text: String,
    val audioUrl: String?,
    val audioOffset: Long?
)

data class Reciter(
    val id: Int,
    val name: String,
    val style: String?,
    val urlIdentifier: String,
    val isFajrSpecial: Boolean = false
)

data class Bookmark(
    val id: Long,
    val surahNumber: Int,
    val verseNumber: Int,
    val note: String?,
    val createdAt: Long
)

data class SearchResult(
    val verse: Verse,
    val translation: Translation?,
    val surahName: String
)

enum class RevelationType {
    MECCAN, MEDINAN;

    fun displayName() = name.lowercase().replaceFirstChar { it.uppercase() }
}
