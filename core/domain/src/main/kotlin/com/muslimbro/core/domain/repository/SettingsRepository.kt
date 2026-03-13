package com.muslimbro.core.domain.repository

import com.muslimbro.core.domain.model.Prayer
import com.muslimbro.core.domain.model.PrayerNotificationSettings
import com.muslimbro.core.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun updatePrayerNotification(prayer: Prayer, settings: PrayerNotificationSettings)
}
