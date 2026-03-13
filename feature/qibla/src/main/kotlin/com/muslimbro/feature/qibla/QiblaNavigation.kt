package com.muslimbro.feature.qibla

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val QIBLA_ROUTE = "qibla"

fun NavGraphBuilder.qiblaGraph() {
    composable(QIBLA_ROUTE) {
        QiblaScreen()
    }
}
