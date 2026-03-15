package com.muslimbro.feature.masnoon.model

data class Dua(
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String
)

data class DuaCategory(
    val id: Int,
    val name: String,
    val arabicName: String,
    val emoji: String,
    val duas: List<Dua>
)
