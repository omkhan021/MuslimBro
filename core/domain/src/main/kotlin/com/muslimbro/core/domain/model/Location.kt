package com.muslimbro.core.domain.model

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String? = null,
    val countryName: String? = null,
    val isManual: Boolean = false
)

data class UserSettings(
    val calculationMethod: CalculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
    val madhab: Madhab = Madhab.SHAFI,
    val language: String = "en",
    val isDarkMode: Boolean? = null, // null = system
    val defaultTranslationEdition: String = "en.asad",
    val defaultReciterId: Int = 1,
    val notificationsEnabled: Boolean = true,
    val prayerNotifications: Map<Prayer, PrayerNotificationSettings> = Prayer.entries
        .filter { it.isAlarmable }
        .associateWith { PrayerNotificationSettings() }
)

data class PrayerNotificationSettings(
    val enabled: Boolean = true,
    val adhanEnabled: Boolean = true,
    val offsetMinutes: Int = 0,
    val reciterId: Int? = null
)
