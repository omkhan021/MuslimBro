package com.muslimbro.feature.quran.surahlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.Surah
import com.muslimbro.core.domain.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class SurahListUiState(
    val isLoading: Boolean = false,
    val surahs: List<Surah> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SurahListViewModel @Inject constructor(
    private val quranRepository: QuranRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurahListUiState(isLoading = true))
    val uiState: StateFlow<SurahListUiState> = _uiState.asStateFlow()

    init {
        quranRepository.getSurahs()
            .onEach { result ->
                when (result) {
                    is AppResult.Loading -> _uiState.value = SurahListUiState(isLoading = true)
                    is AppResult.Success -> _uiState.value = SurahListUiState(surahs = result.data)
                    is AppResult.Error -> _uiState.value = SurahListUiState(
                        error = result.message ?: result.exception.message
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
