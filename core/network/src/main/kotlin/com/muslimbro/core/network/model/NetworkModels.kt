package com.muslimbro.core.network.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val code: Int,
    val status: String,
    val data: T
)

data class SurahDto(
    val number: Int,
    val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("revelationType") val revelationType: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int
)

data class SurahVersesDto(
    val number: Int,
    val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("revelationType") val revelationType: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    val ayahs: List<AyahDto>,
    val edition: EditionDto
)

data class AyahDto(
    val number: Int,
    @SerializedName("numberInSurah") val numberInSurah: Int,
    val text: String,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    @SerializedName("hizbQuarter") val hizbQuarter: Int,
    val sajda: Boolean
)

data class EditionDto(
    val identifier: String,
    val language: String,
    val name: String,
    @SerializedName("englishName") val englishName: String,
    val format: String,
    val type: String,
    val direction: String? = null
)

data class SearchResultDto(
    val count: Int,
    val matches: List<SearchMatchDto>
)

data class SearchMatchDto(
    val number: Int,
    @SerializedName("text") val text: String,
    val edition: EditionDto,
    @SerializedName("surah") val surah: SurahDto,
    @SerializedName("numberInSurah") val numberInSurah: Int
)
