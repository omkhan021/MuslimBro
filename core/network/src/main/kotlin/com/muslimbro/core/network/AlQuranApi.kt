package com.muslimbro.core.network

import com.muslimbro.core.network.model.ApiResponse
import com.muslimbro.core.network.model.EditionDto
import com.muslimbro.core.network.model.SearchResultDto
import com.muslimbro.core.network.model.SurahDto
import com.muslimbro.core.network.model.SurahVersesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlQuranApi {
    @GET("surah")
    suspend fun getSurahs(): ApiResponse<List<SurahDto>>

    @GET("surah/{number}/{edition}")
    suspend fun getSurahVerses(
        @Path("number") number: Int,
        @Path("edition") edition: String = "quran-uthmani"
    ): ApiResponse<SurahVersesDto>

    @GET("edition")
    suspend fun getEditions(
        @Query("language") language: String? = null,
        @Query("type") type: String? = null
    ): ApiResponse<List<EditionDto>>

    @GET("search/{keyword}/{surah}/{language}")
    suspend fun search(
        @Path("keyword") keyword: String,
        @Path("surah") surah: String = "all",
        @Path("language") language: String = "en"
    ): ApiResponse<SearchResultDto>
}
