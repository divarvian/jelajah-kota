package com.arvian.jelajahkota.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navigationEvents = Channel<OnboardingNavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    fun onPageChanged(newPage: Int) {
        _uiState.update { it.copy(currentPage = newPage) }
    }

    fun onSkipClicked() {
        completeOnboarding(OnboardingNavigationEvent.NavigateToMain)
    }

    fun onSignInClicked() {
        completeOnboarding(OnboardingNavigationEvent.NavigateToSignIn)
    }

    fun onSignUpClicked() {
        completeOnboarding(OnboardingNavigationEvent.NavigateToSignUp)
    }

    private fun completeOnboarding(event: OnboardingNavigationEvent) {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
            _uiState.update { it.copy(isOnboardingCompleted = true) }
            _navigationEvents.send(event)
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
