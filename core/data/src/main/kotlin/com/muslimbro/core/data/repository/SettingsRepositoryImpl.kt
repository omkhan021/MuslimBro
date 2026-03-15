package com.muslimbro.core.data.repository

import com.muslimbro.core.data.datastore.SettingsDataStore
import com.muslimbro.core.domain.model.Prayer
import com.muslimbro.core.domain.model.PrayerNotificationSettings
import com.muslimbro.core.domain.model.UserSettings
import com.muslimbro.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun getUserSettings(): Flow<UserSettings> = settingsDataStore.userSettings

    override suspend fun updateSettings(settings: UserSettings) {
        settingsDataStore.updateCalculationMethod(settings.calculationMethod)
        settingsDataStore.updateMadhab(settings.madhab)
        settingsDataStore.updateLanguage(settings.language)
        settingsDataStore.updateDarkMode(settings.isDarkMode)
        settingsDataStore.updateDefaultTranslation(settings.defaultTranslationEdition)
        settingsDataStore.updateNotificationsEnabled(settings.notificationsEnabled)
    }

    override suspend fun updatePrayerNotification(prayer: Prayer, settings: PrayerNotificationSettings) {
        settingsDataStore.updatePrayerNotificationEnabled(prayer, settings.enabled)
    }
}
