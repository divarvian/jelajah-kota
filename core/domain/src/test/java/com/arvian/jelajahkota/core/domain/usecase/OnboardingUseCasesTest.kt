package com.arvian.jelajahkota.core.domain.usecase

import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeDomainPreferencesRepository : UserPreferencesRepository {
    private val completedState = MutableStateFlow(false)

    override fun isOnboardingCompleted(): Flow<Boolean> = completedState

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        completedState.value = completed
    }
}

class OnboardingUseCasesTest {

    private lateinit var fakeRepository: FakeDomainPreferencesRepository
    private lateinit var getStatusUseCase: GetOnboardingStatusUseCase
    private lateinit var completeUseCase: CompleteOnboardingUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeDomainPreferencesRepository()
        getStatusUseCase = GetOnboardingStatusUseCase(fakeRepository)
        completeUseCase = CompleteOnboardingUseCase(fakeRepository)
    }

    @Test
    fun getStatus_returnsInitialFalse() = runTest {
        assertFalse(getStatusUseCase().first())
    }

    @Test
    fun completeUseCase_updatesStatusToTrue() = runTest {
        completeUseCase()
        assertTrue(getStatusUseCase().first())
    }
}
