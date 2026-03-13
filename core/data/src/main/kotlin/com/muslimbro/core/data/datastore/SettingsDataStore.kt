package com.muslimbro.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.muslimbro.core.domain.model.CalculationMethod
import com.muslimbro.core.domain.model.Madhab
import com.muslimbro.core.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val CALCULATION_METHOD = stringPreferencesKey("calculation_method")
        val MADHAB = stringPreferencesKey("madhab")
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_MODE = stringPreferencesKey("dark_mode") // "light", "dark", "system"
        val DEFAULT_TRANSLATION = stringPreferencesKey("default_translation")
        val DEFAULT_RECITER_ID = intPreferencesKey("default_reciter_id")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SAVED_LATITUDE = stringPreferencesKey("saved_latitude")
        val SAVED_LONGITUDE = stringPreferencesKey("saved_longitude")
        val SAVED_CITY = stringPreferencesKey("saved_city")
        val USE_MANUAL_LOCATION = booleanPreferencesKey("use_manual_location")
        val FONT_SIZE_QURAN = intPreferencesKey("font_size_quran")
    }

    val userSettings: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            calculationMethod = prefs[CALCULATION_METHOD]
                ?.let { runCatching { CalculationMethod.valueOf(it) }.getOrNull() }
                ?: CalculationMethod.MUSLIM_WORLD_LEAGUE,
            madhab = prefs[MADHAB]
                ?.let { runCatching { Madhab.valueOf(it) }.getOrNull() }
                ?: Madhab.SHAFI,
            language = prefs[LANGUAGE] ?: "en",
            isDarkMode = when (prefs[DARK_MODE]) {
                "dark" -> true
                "light" -> false
                else -> null
            },
            defaultTranslationEdition = prefs[DEFAULT_TRANSLATION] ?: "en.asad",
            defaultReciterId = prefs[DEFAULT_RECITER_ID] ?: 1,
            notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true
        )
    }

    suspend fun updateCalculationMethod(method: CalculationMethod) {
        dataStore.edit { it[CALCULATION_METHOD] = method.name }
    }

    suspend fun updateMadhab(madhab: Madhab) {
        dataStore.edit { it[MADHAB] = madhab.name }
    }

    suspend fun updateLanguage(language: String) {
        dataStore.edit { it[LANGUAGE] = language }
    }

    suspend fun updateDarkMode(darkMode: Boolean?) {
        dataStore.edit {
            it[DARK_MODE] = when (darkMode) {
                true -> "dark"
                false -> "light"
                null -> "system"
            }
        }
    }

    suspend fun updateDefaultTranslation(edition: String) {
        dataStore.edit { it[DEFAULT_TRANSLATION] = edition }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    val savedLocation: Flow<Triple<Double, Double, String>?> = dataStore.data.map { prefs ->
        val lat = prefs[SAVED_LATITUDE]?.toDoubleOrNull()
        val lng = prefs[SAVED_LONGITUDE]?.toDoubleOrNull()
        val city = prefs[SAVED_CITY] ?: ""
        if (lat != null && lng != null) Triple(lat, lng, city) else null
    }

    suspend fun saveLocation(latitude: Double, longitude: Double, cityName: String) {
        dataStore.edit {
            it[SAVED_LATITUDE] = latitude.toString()
            it[SAVED_LONGITUDE] = longitude.toString()
            it[SAVED_CITY] = cityName
            it[USE_MANUAL_LOCATION] = true
        }
    }

    suspend fun clearManualLocation() {
        dataStore.edit {
            it.remove(SAVED_LATITUDE)
            it.remove(SAVED_LONGITUDE)
            it.remove(SAVED_CITY)
            it[USE_MANUAL_LOCATION] = false
        }
    }
}
