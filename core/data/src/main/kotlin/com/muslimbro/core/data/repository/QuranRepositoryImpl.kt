package com.muslimbro.core.data.repository

import com.muslimbro.core.common.AppResult
import com.muslimbro.core.data.database.dao.QuranDao
import com.muslimbro.core.data.database.dao.SearchDao
import com.muslimbro.core.data.database.entity.BookmarkEntity
import com.muslimbro.core.data.database.entity.ReciterEntity
import com.muslimbro.core.data.database.entity.SurahEntity
import com.muslimbro.core.data.database.entity.TranslationTextEntity
import com.muslimbro.core.data.database.entity.VerseEntity
import com.muslimbro.core.domain.model.Bookmark
import com.muslimbro.core.domain.model.Reciter
import com.muslimbro.core.domain.model.RevelationType
import com.muslimbro.core.domain.model.SearchResult
import com.muslimbro.core.domain.model.Surah
import com.muslimbro.core.domain.model.Translation
import com.muslimbro.core.domain.model.Verse
import com.muslimbro.core.domain.model.Word
import com.muslimbro.core.domain.repository.QuranRepository
import com.muslimbro.core.network.AlQuranApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val quranDao: QuranDao,
    private val searchDao: SearchDao,
    private val alQuranApi: AlQuranApi
) : QuranRepository {

    override fun getSurahs(): Flow<AppResult<List<Surah>>> =
        quranDao.getSurahs()
            .map { entities ->
                if (entities.isNotEmpty()) {
                    AppResult.Success(entities.map { it.toDomain() })
                } else {
                    AppResult.Error(Exception("No surahs in database"), "Database may not be seeded")
                }
            }
            .catch { e -> emit(AppResult.Error(e, "Failed to load surahs")) }

    override fun getSurah(number: Int): Flow<AppResult<Surah>> =
        quranDao.getSurah(number).map { entity ->
            if (entity != null) AppResult.Success(entity.toDomain())
            else AppResult.Error(Exception("Surah $number not found"))
        }

    override fun getVerses(surahNumber: Int): Flow<AppResult<List<Verse>>> =
        quranDao.getVerses(surahNumber).map { entities ->
            AppResult.Success(entities.map { it.toDomain() })
        }

    override fun getVerse(surahNumber: Int, verseNumber: Int): Flow<AppResult<Verse>> =
        quranDao.getVerse(surahNumber, verseNumber).map { entity ->
            if (entity != null) AppResult.Success(entity.toDomain())
            else AppResult.Error(Exception("Verse $surahNumber:$verseNumber not found"))
        }

    override fun getTranslation(surahNumber: Int, edition: String): Flow<AppResult<List<Translation>>> =
        quranDao.getTranslation(surahNumber, edition).map { entities ->
            AppResult.Success(entities.map { it.toDomain() })
        }

    override fun getWords(verseId: Long): Flow<AppResult<List<Word>>> =
        quranDao.getWords(verseId).map { entities ->
            AppResult.Success(entities.map { entity ->
                Word(
                    id = entity.id,
                    verseId = entity.verseId,
                    position = entity.position,
                    text = entity.text,
                    audioUrl = entity.audioUrl,
                    audioOffset = entity.audioOffset
                )
            })
        }

    override fun searchVerses(query: String, surahNumber: Int?): Flow<AppResult<List<SearchResult>>> =
        flow {
            emit(AppResult.Loading)
            try {
                val ftsQuery = "$query*"
                val verses = searchDao.searchVersesByArabic(ftsQuery)
                val results = verses.map { verse ->
                    val surah = quranDao.getSurah(verse.surahNumber)
                    SearchResult(
                        verse = verse.toDomain(),
                        translation = null,
                        surahName = "Surah ${verse.surahNumber}"
                    )
                }
                emit(AppResult.Success(results))
            } catch (e: Exception) {
                emit(AppResult.Error(e))
            }
        }

    override fun getBookmarks(): Flow<AppResult<List<Bookmark>>> =
        quranDao.getBookmarks().map { entities ->
            AppResult.Success(entities.map { it.toDomain() })
        }

    override suspend fun addBookmark(surahNumber: Int, verseNumber: Int, note: String?): AppResult<Unit> {
        return try {
            quranDao.insertBookmark(BookmarkEntity(
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                note = note
            ))
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun removeBookmark(id: Long): AppResult<Unit> {
        return try {
            quranDao.deleteBookmark(id)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override fun getReciters(): Flow<AppResult<List<Reciter>>> =
        quranDao.getReciters().map { entities ->
            if (entities.isNotEmpty()) {
                AppResult.Success(entities.map { it.toDomain() })
            } else {
                AppResult.Success(defaultReciters())
            }
        }

    override suspend fun fetchTranslation(surahNumber: Int, edition: String): AppResult<Unit> {
        return try {
            val existing = quranDao.getTranslationCount(surahNumber, edition)
            if (existing > 0) return AppResult.Success(Unit)

            val response = alQuranApi.getSurahVerses(surahNumber, edition)
            val texts = response.data.ayahs.map { ayah ->
                TranslationTextEntity(
                    surahNumber = surahNumber,
                    verseNumber = ayah.numberInSurah,
                    text = ayah.text,
                    edition = edition,
                    verseId = 0 // Will be resolved by verse number
                )
            }
            quranDao.insertTranslationTexts(texts)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "Failed to fetch translation")
        }
    }

    // Mapping functions
    private fun SurahEntity.toDomain() = Surah(
        number = number,
        name = name,
        nameArabic = nameArabic,
        nameTranslation = nameTranslation,
        revelationType = if (revelationType == "Meccan") RevelationType.MECCAN else RevelationType.MEDINAN,
        versesCount = versesCount
    )

    private fun VerseEntity.toDomain() = Verse(
        id = id,
        surahNumber = surahNumber,
        verseNumber = verseNumber,
        textUthmani = textUthmani,
        textSimple = textSimple,
        juz = juz,
        hizb = hizb,
        sajda = sajda
    )

    private fun TranslationTextEntity.toDomain() = Translation(
        id = id,
        verseId = verseId,
        surahNumber = surahNumber,
        verseNumber = verseNumber,
        text = text,
        language = edition.split(".").firstOrNull() ?: "en",
        edition = edition
    )

    private fun BookmarkEntity.toDomain() = Bookmark(
        id = id,
        surahNumber = surahNumber,
        verseNumber = verseNumber,
        note = note,
        createdAt = createdAt
    )

    private fun ReciterEntity.toDomain() = Reciter(
        id = id,
        name = name,
        style = style,
        urlIdentifier = urlIdentifier,
        isFajrSpecial = isFajrSpecial
    )

    private fun defaultReciters() = listOf(
        Reciter(1, "Mishary Rashid Alafasy", null, "Alafasy_128kbps"),
        Reciter(2, "Abdul Rahman Al-Sudais", null, "Sudais_128kbps"),
        Reciter(3, "Saad Al-Ghamdi", null, "Ghamdi_40kbps"),
        Reciter(4, "Abu Bakr Al-Shatri", null, "Abu_Bakr_Ash-Shaatree_128kbps"),
        Reciter(5, "Nasser Al-Qatami", null, "Nasser_Alqatami_128kbps")
    )
}
