package com.arvian.jelajahkota.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(viewModel) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToMain -> onNavigateToMain()
                is OnboardingNavigationEvent.NavigateToSignIn -> onNavigateToSignIn()
                is OnboardingNavigationEvent.NavigateToSignUp -> onNavigateToSignUp()
            }
        }
    }

    OnboardingScreen(
        onSkipClick = viewModel::onSkipClicked,
        onSignInClick = viewModel::onSignInClicked,
        onSignUpClick = viewModel::onSignUpClicked,
        onPageSelected = viewModel::onPageChanged,
        modifier = modifier
    )
}
