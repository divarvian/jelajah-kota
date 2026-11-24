package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.onboarding.OnboardingRoute
import com.arvian.jelajahkota.feature.onboarding.OnboardingViewModel
import com.arvian.jelajahkota.feature.onboarding.SplashScreen

internal fun NavGraphBuilder.onboardingNavGraph(
    navController: NavController,
    container: AppContainer
) {
    composable<SplashDestination> {
        SplashScreen(
            onSplashFinished = {
                navController.navigate(OnboardingDestination) {
                    popUpTo(SplashDestination) { inclusive = true }
                }
            }
        )
    }

    composable<OnboardingDestination> {
        val onboardingViewModel: OnboardingViewModel = viewModel(
            factory = OnboardingViewModel.Factory(container.preferencesRepository)
        )
        OnboardingRoute(
            viewModel = onboardingViewModel,
            onNavigateToMain = {
                container.mainShellCoordinator.resetToHome()
                navController.navigate(MainShellDestination) {
                    popUpTo(OnboardingDestination) { inclusive = true }
                }
            },
            onNavigateToSignIn = {
                // Pop Onboarding from backstack so SignIn never navigates back to Onboarding.
                // Onboarding remains incomplete until user authenticates or continues as Guest.
                navController.navigate(SignInDestination) {
                    popUpTo(OnboardingDestination) { inclusive = true }
                }
            },
            onNavigateToSignUp = {
                navController.navigate(SignUpDestination) {
                    popUpTo(OnboardingDestination) { inclusive = true }
                }
            }
        )
    }
}
