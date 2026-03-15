package com.muslimbro.feature.masnoon

import androidx.lifecycle.ViewModel
import com.muslimbro.feature.masnoon.data.DuaRepository
import com.muslimbro.feature.masnoon.model.DuaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MasnoonViewModel @Inject constructor() : ViewModel() {
    val categories: List<DuaCategory> = DuaRepository.categories
}
