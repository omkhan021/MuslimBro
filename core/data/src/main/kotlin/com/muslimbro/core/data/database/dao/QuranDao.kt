package com.muslimbro.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muslimbro.core.data.database.entity.BookmarkEntity
import com.muslimbro.core.data.database.entity.ReciterEntity
import com.muslimbro.core.data.database.entity.SurahEntity
import com.muslimbro.core.data.database.entity.TranslationTextEntity
import com.muslimbro.core.data.database.entity.VerseEntity
import com.muslimbro.core.data.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    // Surahs
    @Query("SELECT * FROM surahs ORDER BY number")
    fun getSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :number")
    fun getSurah(number: Int): Flow<SurahEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    // Verses
    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber")
    fun getVerses(surahNumber: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE surahNumber = :surah AND verseNumber = :verse LIMIT 1")
    fun getVerse(surah: Int, verse: Int): Flow<VerseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    // Words
    @Query("SELECT * FROM words WHERE verseId = :verseId ORDER BY position")
    fun getWords(verseId: Long): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    // Translations
    @Query("""
        SELECT * FROM translation_texts
        WHERE surahNumber = :surah AND edition = :edition
        ORDER BY verseNumber
    """)
    fun getTranslation(surah: Int, edition: String): Flow<List<TranslationTextEntity>>

    @Query("SELECT COUNT(*) FROM translation_texts WHERE surahNumber = :surah AND edition = :edition")
    suspend fun getTranslationCount(surah: Int, edition: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslationTexts(texts: List<TranslationTextEntity>)

    // Reciters
    @Query("SELECT * FROM reciters ORDER BY name")
    fun getReciters(): Flow<List<ReciterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReciters(reciters: List<ReciterEntity>)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: Long)

    @Query("SELECT * FROM bookmarks WHERE surahNumber = :surah AND verseNumber = :verse LIMIT 1")
    suspend fun getBookmark(surah: Int, verse: Int): BookmarkEntity?
}
