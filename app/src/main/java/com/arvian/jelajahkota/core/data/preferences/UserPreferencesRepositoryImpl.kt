package com.arvian.jelajahkota.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arvian.jelajahkota.core.domain.model.AppSettings
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    companion object {
        val KEY_IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val KEY_LOCATION_ENABLED = booleanPreferencesKey("location_enabled")
        val KEY_DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val KEY_PUSH_NOTIFICATIONS_ENABLED = booleanPreferencesKey("push_notifications_enabled")
        val KEY_EMAIL_NOTIFICATIONS_ENABLED = booleanPreferencesKey("email_notifications_enabled")
        val KEY_REMEMBERED_EMAIL = stringPreferencesKey("remembered_email")
    }

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    override fun isOnboardingCompleted(): Flow<Boolean> =
        safeData.map { preferences ->
            preferences[KEY_IS_ONBOARDING_COMPLETED] ?: false
        }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_IS_ONBOARDING_COMPLETED] = completed
        }
    }

    override fun observeAppSettings(): Flow<AppSettings> =
        safeData.map { preferences ->
            AppSettings(
                locationEnabled = preferences[KEY_LOCATION_ENABLED] ?: true,
                darkModeEnabled = preferences[KEY_DARK_MODE_ENABLED] ?: false,
                pushNotificationsEnabled = preferences[KEY_PUSH_NOTIFICATIONS_ENABLED] ?: true,
                emailNotificationsEnabled = preferences[KEY_EMAIL_NOTIFICATIONS_ENABLED] ?: false
            )
        }

    override suspend fun setLocationEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_LOCATION_ENABLED] = enabled }
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE_ENABLED] = enabled }
    }

    override suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_PUSH_NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setEmailNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_EMAIL_NOTIFICATIONS_ENABLED] = enabled }
    }

    override fun getRememberedEmail(): Flow<String?> =
        safeData.map { preferences ->
            preferences[KEY_REMEMBERED_EMAIL]?.takeIf { it.isNotBlank() }
        }

    override suspend fun setRememberedEmail(email: String?) {
        dataStore.edit { preferences ->
            val clean = email?.trim()
            if (clean.isNullOrBlank()) {
                preferences.remove(KEY_REMEMBERED_EMAIL)
            } else {
                preferences[KEY_REMEMBERED_EMAIL] = clean
            }
        }
    }
}
