package com.muslimbro.feature.masnoon

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

const val MASNOON_ROUTE = "masnoon"
private const val MASNOON_LIST_ROUTE = "masnoon_list"
private const val DUA_DETAIL_ROUTE = "dua_detail/{categoryId}"

fun NavGraphBuilder.masnoonGraph(navController: NavHostController) {
    navigation(startDestination = MASNOON_LIST_ROUTE, route = MASNOON_ROUTE) {
        composable(MASNOON_LIST_ROUTE) {
            MasnoonScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate("dua_detail/$categoryId")
                }
            )
        }
        composable(
            route = DUA_DETAIL_ROUTE,
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) {
            DuaDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
