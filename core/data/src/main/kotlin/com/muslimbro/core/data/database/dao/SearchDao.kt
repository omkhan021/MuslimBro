package com.muslimbro.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.muslimbro.core.data.database.entity.VerseEntity

@Dao
interface SearchDao {
    @Query("""
        SELECT verses.* FROM verses
        INNER JOIN verses_fts ON verses.id = verses_fts.rowid
        WHERE verses_fts MATCH :query
        LIMIT 50
    """)
    suspend fun searchVersesByArabic(query: String): List<VerseEntity>

    @Query("""
        SELECT verses.* FROM verses
        INNER JOIN translation_texts ON verses.surahNumber = translation_texts.surahNumber
            AND verses.verseNumber = translation_texts.verseNumber
        INNER JOIN translation_fts ON translation_texts.id = translation_fts.rowid
        WHERE translation_fts MATCH :query
        AND translation_texts.edition = :edition
        LIMIT 50
    """)
    suspend fun searchVersesByTranslation(query: String, edition: String): List<VerseEntity>
}
