package com.muslimbro.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val SETTINGS_ROUTE = "settings"
private const val SETTINGS_LIST_ROUTE = "settings_list"
private const val ABOUT_ROUTE = "about"

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation(startDestination = SETTINGS_LIST_ROUTE, route = SETTINGS_ROUTE) {
        composable(SETTINGS_LIST_ROUTE) {
            SettingsScreen(onAboutClick = { navController.navigate(ABOUT_ROUTE) })
        }
        composable(ABOUT_ROUTE) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
