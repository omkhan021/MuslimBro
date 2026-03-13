package com.muslimbro.core.domain.repository

import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.Bookmark
import com.muslimbro.core.domain.model.Reciter
import com.muslimbro.core.domain.model.SearchResult
import com.muslimbro.core.domain.model.Surah
import com.muslimbro.core.domain.model.Translation
import com.muslimbro.core.domain.model.Verse
import com.muslimbro.core.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    fun getSurahs(): Flow<AppResult<List<Surah>>>
    fun getSurah(number: Int): Flow<AppResult<Surah>>
    fun getVerses(surahNumber: Int): Flow<AppResult<List<Verse>>>
    fun getVerse(surahNumber: Int, verseNumber: Int): Flow<AppResult<Verse>>
    fun getTranslation(surahNumber: Int, edition: String): Flow<AppResult<List<Translation>>>
    fun getWords(verseId: Long): Flow<AppResult<List<Word>>>
    fun searchVerses(query: String, surahNumber: Int? = null): Flow<AppResult<List<SearchResult>>>
    fun getBookmarks(): Flow<AppResult<List<Bookmark>>>
    suspend fun addBookmark(surahNumber: Int, verseNumber: Int, note: String?): AppResult<Unit>
    suspend fun removeBookmark(id: Long): AppResult<Unit>
    fun getReciters(): Flow<AppResult<List<Reciter>>>
    suspend fun fetchTranslation(surahNumber: Int, edition: String): AppResult<Unit>
}
