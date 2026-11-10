package com.arvian.jelajahkota.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}
