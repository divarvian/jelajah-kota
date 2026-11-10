package com.arvian.jelajahkota.feature.onboarding

import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val completedState = MutableStateFlow(false)

    override fun isOnboardingCompleted(): Flow<Boolean> = completedState

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        completedState.value = completed
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserPreferencesRepository()
        viewModel = OnboardingViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_defaultsToFirstPageAndNotCompleted() = runTest {
        val state = viewModel.uiState.value
        assertEquals(0, state.currentPage)
        assertFalse(state.isOnboardingCompleted)
    }

    @Test
    fun onPageChanged_updatesCurrentPageIndex() = runTest {
        viewModel.onPageChanged(1)
        assertEquals(1, viewModel.uiState.value.currentPage)

        viewModel.onPageChanged(2)
        assertEquals(2, viewModel.uiState.value.currentPage)
    }

    @Test
    fun onSkipClicked_persistsCompletionAndEmitsNavigateToMain() = runTest {
        viewModel.onSkipClicked()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOnboardingCompleted)
        assertTrue(fakeRepository.isOnboardingCompleted().first())

        val event = viewModel.navigationEvents.first()
        assertEquals(OnboardingNavigationEvent.NavigateToMain, event)
    }

    @Test
    fun onSignInClicked_persistsCompletionAndEmitsNavigateToSignIn() = runTest {
        viewModel.onSignInClicked()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOnboardingCompleted)
        assertTrue(fakeRepository.isOnboardingCompleted().first())

        val event = viewModel.navigationEvents.first()
        assertEquals(OnboardingNavigationEvent.NavigateToSignIn, event)
    }

    @Test
    fun onSignUpClicked_persistsCompletionAndEmitsNavigateToSignUp() = runTest {
        viewModel.onSignUpClicked()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOnboardingCompleted)
        assertTrue(fakeRepository.isOnboardingCompleted().first())

        val event = viewModel.navigationEvents.first()
        assertEquals(OnboardingNavigationEvent.NavigateToSignUp, event)
    }
}
