package com.muslimbro.feature.quran.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.Surah
import com.muslimbro.core.domain.model.Translation
import com.muslimbro.core.domain.model.Verse
import com.muslimbro.core.domain.repository.QuranRepository
import com.muslimbro.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuranReaderUiState(
    val isLoading: Boolean = false,
    val surah: Surah? = null,
    val verses: List<Verse> = emptyList(),
    val translations: List<Translation> = emptyList(),
    val highlightedWordIndex: Int = -1,
    val selectedVerseForSheet: Verse? = null,
    val error: String? = null
)

@HiltViewModel
class QuranReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quranRepository: QuranRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val surahNumber: Int = checkNotNull(savedStateHandle["surahNumber"])

    private val _uiState = MutableStateFlow(QuranReaderUiState(isLoading = true))
    val uiState: StateFlow<QuranReaderUiState> = _uiState.asStateFlow()

    init {
        loadSurah()
    }

    private fun loadSurah() {
        viewModelScope.launch {
            val settings = settingsRepository.getUserSettings()
            settings.onEach { userSettings ->
                combine(
                    quranRepository.getSurah(surahNumber),
                    quranRepository.getVerses(surahNumber),
                    quranRepository.getTranslation(surahNumber, userSettings.defaultTranslationEdition)
                ) { surahResult, versesResult, translationsResult ->
                    when {
                        surahResult is AppResult.Success && versesResult is AppResult.Success -> {
                            _uiState.value = QuranReaderUiState(
                                surah = surahResult.data,
                                verses = versesResult.data,
                                translations = (translationsResult as? AppResult.Success)?.data ?: emptyList()
                            )
                            // Fetch translation if not cached
                            if ((translationsResult as? AppResult.Success)?.data.isNullOrEmpty()) {
                                fetchTranslation(userSettings.defaultTranslationEdition)
                            }
                        }
                        surahResult is AppResult.Loading || versesResult is AppResult.Loading -> {
                            _uiState.value = QuranReaderUiState(isLoading = true)
                        }
                        else -> {
                            val error = (surahResult as? AppResult.Error)?.message
                                ?: (versesResult as? AppResult.Error)?.message
                                ?: "Failed to load surah"
                            _uiState.value = QuranReaderUiState(error = error)
                        }
                    }
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)
        }
    }

    private fun fetchTranslation(edition: String) {
        viewModelScope.launch {
            quranRepository.fetchTranslation(surahNumber, edition)
        }
    }

    fun setHighlightedWord(wordIndex: Int) {
        _uiState.value = _uiState.value.copy(highlightedWordIndex = wordIndex)
    }

    fun selectVerseForSheet(verse: Verse?) {
        _uiState.value = _uiState.value.copy(selectedVerseForSheet = verse)
    }

    fun bookmarkVerse(verse: Verse) {
        viewModelScope.launch {
            quranRepository.addBookmark(verse.surahNumber, verse.verseNumber, null)
        }
    }
}
