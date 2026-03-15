package com.muslimbro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.muslimbro.core.ui.theme.MuslimBroTheme
import com.muslimbro.feature.prayertimes.PRAYER_TIMES_ROUTE
import com.muslimbro.feature.prayertimes.prayerTimesGraph
import com.muslimbro.feature.qibla.QIBLA_ROUTE
import com.muslimbro.feature.qibla.qiblaGraph
import com.muslimbro.feature.quran.QURAN_ROUTE
import com.muslimbro.feature.quran.quranGraph
import com.muslimbro.feature.masnoon.MASNOON_ROUTE
import com.muslimbro.feature.masnoon.masnoonGraph
import com.muslimbro.feature.settings.SETTINGS_ROUTE
import com.muslimbro.feature.settings.settingsGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuslimBroTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    MuslimBroNavHost()
                }
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Prayer", Icons.Default.Mosque, PRAYER_TIMES_ROUTE),
    BottomNavItem("Quran", Icons.Default.Book, QURAN_ROUTE),
    BottomNavItem("Du'as", Icons.Default.AutoStories, MASNOON_ROUTE),
    BottomNavItem("Qibla", Icons.Default.LocationOn, QIBLA_ROUTE),
    BottomNavItem("Settings", Icons.Default.Settings, SETTINGS_ROUTE)
)

@Composable
fun MuslimBroNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route ||
                        (item.route == QURAN_ROUTE && currentRoute?.startsWith("surah") == true) ||
                        (item.route == QURAN_ROUTE && currentRoute?.startsWith("quran") == true) ||
                        (item.route == MASNOON_ROUTE && currentRoute?.startsWith("dua_detail") == true) ||
                        (item.route == MASNOON_ROUTE && currentRoute?.startsWith("masnoon") == true)
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = PRAYER_TIMES_ROUTE,
            modifier = Modifier.padding(paddingValues)
        ) {
            prayerTimesGraph()
            quranGraph(navController)
            masnoonGraph(navController)
            qiblaGraph()
            settingsGraph()
        }
    }
}
