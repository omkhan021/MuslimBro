package com.muslimbro.feature.masnoon

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.muslimbro.feature.masnoon.data.DuaRepository
import com.muslimbro.feature.masnoon.model.DuaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val categoryId: Int = savedStateHandle["categoryId"] ?: 0
    val category: DuaCategory? = DuaRepository.getCategoryById(categoryId)
}
