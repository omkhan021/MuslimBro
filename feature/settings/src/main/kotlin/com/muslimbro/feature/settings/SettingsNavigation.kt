package com.muslimbro.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTINGS_ROUTE = "settings"

fun NavGraphBuilder.settingsGraph() {
    composable(SETTINGS_ROUTE) {
        SettingsScreen()
    }
}
