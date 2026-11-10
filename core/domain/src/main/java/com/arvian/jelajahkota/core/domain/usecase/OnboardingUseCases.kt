package com.arvian.jelajahkota.core.domain.usecase

import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetOnboardingStatusUseCase(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isOnboardingCompleted()
}

class CompleteOnboardingUseCase(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted(true)
    }
}
