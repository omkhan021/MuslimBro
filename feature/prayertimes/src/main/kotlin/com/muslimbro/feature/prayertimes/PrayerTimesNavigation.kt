package com.muslimbro.feature.prayertimes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val PRAYER_TIMES_ROUTE = "prayer_times"

fun NavGraphBuilder.prayerTimesGraph() {
    composable(PRAYER_TIMES_ROUTE) {
        PrayerTimesScreen()
    }
}
