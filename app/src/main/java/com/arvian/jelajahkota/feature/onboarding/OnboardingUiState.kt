package com.arvian.jelajahkota.feature.onboarding

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isOnboardingCompleted: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface OnboardingNavigationEvent {
    data object NavigateToMain : OnboardingNavigationEvent
    data object NavigateToSignIn : OnboardingNavigationEvent
    data object NavigateToSignUp : OnboardingNavigationEvent
}
