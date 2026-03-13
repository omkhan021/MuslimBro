package com.muslimbro.core.domain.usecase

import com.muslimbro.core.common.AppResult
import com.muslimbro.core.domain.model.SearchResult
import com.muslimbro.core.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchQuranUseCase(
    private val quranRepository: QuranRepository
) {
    operator fun invoke(query: String, surahNumber: Int? = null): Flow<AppResult<List<SearchResult>>> {
        if (query.length < 2) return flowOf(AppResult.Success(emptyList()))
        return quranRepository.searchVerses(query.trim(), surahNumber)
    }
}
