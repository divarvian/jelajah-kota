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
                navController.navigate(MainDestination) {
                    popUpTo(OnboardingDestination) { inclusive = true }
                }
            },
            onNavigateToSignIn = {
                navController.navigate(SignInDestination)
            },
            onNavigateToSignUp = {
                navController.navigate(SignUpDestination)
            }
        )
    }
}
