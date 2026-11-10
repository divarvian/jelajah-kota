package com.arvian.jelajahkota.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
