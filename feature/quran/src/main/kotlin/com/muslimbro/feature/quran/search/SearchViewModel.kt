package com.muslimbro.feature.quran.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.SearchResult
import com.muslimbro.core.domain.usecase.SearchQuranUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchQuranUseCase: SearchQuranUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")

    init {
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        _query
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { q -> searchQuranUseCase(q) }
            .onEach { result ->
                when (result) {
                    is AppResult.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is AppResult.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        results = result.data,
                        error = null
                    )
                    is AppResult.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        _query.value = query
    }
}
