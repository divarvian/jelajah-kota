package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun observeAppSettings(): Flow<AppSettings>
    suspend fun setLocationEnabled(enabled: Boolean)
    suspend fun setDarkModeEnabled(enabled: Boolean)
    suspend fun setPushNotificationsEnabled(enabled: Boolean)
    suspend fun setEmailNotificationsEnabled(enabled: Boolean)
}
