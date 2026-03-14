package com.muslimbro.feature.quran

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.muslimbro.feature.quran.reader.QuranReaderScreen
import com.muslimbro.feature.quran.search.SearchScreen
import com.muslimbro.feature.quran.surahlist.SurahListScreen

const val QURAN_ROUTE = "quran"
const val SURAH_LIST_ROUTE = "surah_list"
const val QURAN_READER_ROUTE = "quran_reader/{surahNumber}"
const val SEARCH_ROUTE = "quran_search"

fun NavGraphBuilder.quranGraph(navController: NavHostController) {
    navigation(startDestination = SURAH_LIST_ROUTE, route = QURAN_ROUTE) {
        composable(SURAH_LIST_ROUTE) {
            SurahListScreen(
                onSurahClick = { surahNumber ->
                    navController.navigate("quran_reader/$surahNumber")
                },
                onSearchClick = {
                    navController.navigate(SEARCH_ROUTE)
                }
            )
        }
        composable(
            route = QURAN_READER_ROUTE,
            arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
        ) {
            QuranReaderScreen(onBack = { navController.popBackStack() })
        }
        composable(SEARCH_ROUTE) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onVerseClick = { surahNumber ->
                    navController.navigate("quran_reader/$surahNumber")
                }
            )
        }
    }
}
