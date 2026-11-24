package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.auth.ForgotPasswordRoute
import com.arvian.jelajahkota.feature.auth.ForgotPasswordViewModel
import com.arvian.jelajahkota.feature.auth.OtpVerificationRoute
import com.arvian.jelajahkota.feature.auth.OtpVerificationViewModel
import com.arvian.jelajahkota.feature.auth.PasswordResetSuccessScreen
import com.arvian.jelajahkota.feature.auth.ResetPasswordRoute
import com.arvian.jelajahkota.feature.auth.ResetPasswordViewModel
import com.arvian.jelajahkota.feature.auth.SignInRoute
import com.arvian.jelajahkota.feature.auth.SignInViewModel
import com.arvian.jelajahkota.feature.auth.SignUpRoute
import com.arvian.jelajahkota.feature.auth.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    container: AppContainer,
    coroutineScope: CoroutineScope,
    showMessage: (String) -> Unit
) {
    composable<SignInDestination> {
        val signInViewModel: SignInViewModel = viewModel(
            factory = SignInViewModel.Factory(
                authRepository = container.authRepository,
                preferencesRepository = container.preferencesRepository
            )
        )
        val canNavigateBack = navController.canNavigateBack()

        SignInRoute(
            viewModel = signInViewModel,
            canNavigateBack = canNavigateBack,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToSignUp = {
                navController.navigate(SignUpDestination) {
                    popUpTo(SignInDestination) { inclusive = true }
                }
            },
            onNavigateToForgotPassword = { navController.navigate(ForgotPasswordDestination) },
            onContinueAsGuest = {
                coroutineScope.launch {
                    container.preferencesRepository.setOnboardingCompleted(true)
                }
                container.mainShellCoordinator.resetToHome()
                navController.navigate(MainShellDestination) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSignInSuccess = {
                coroutineScope.launch {
                    container.preferencesRepository.setOnboardingCompleted(true)
                }
                showMessage("Selamat datang kembali!")
                if (navController.canNavigateBack()) {
                    navController.safePopBackStack()
                } else {
                    container.mainShellCoordinator.resetToHome()
                    navController.navigate(MainShellDestination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        )
    }

    composable<SignUpDestination> {
        val signUpViewModel: SignUpViewModel = viewModel(
            factory = SignUpViewModel.Factory(container.authRepository)
        )
        val canNavigateBack = navController.canNavigateBack()

        SignUpRoute(
            viewModel = signUpViewModel,
            canNavigateBack = canNavigateBack,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToSignIn = {
                navController.navigate(SignInDestination) {
                    popUpTo(SignUpDestination) { inclusive = true }
                }
            },
            onContinueAsGuest = {
                coroutineScope.launch {
                    container.preferencesRepository.setOnboardingCompleted(true)
                }
                container.mainShellCoordinator.resetToHome()
                navController.navigate(MainShellDestination) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSignUpSuccess = {
                coroutineScope.launch {
                    container.preferencesRepository.setOnboardingCompleted(true)
                }
                showMessage("Akun berhasil dibuat!")
                if (navController.canNavigateBack()) {
                    navController.safePopBackStack()
                } else {
                    container.mainShellCoordinator.resetToHome()
                    navController.navigate(MainShellDestination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        )
    }

    composable<ForgotPasswordDestination> {
        val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(
            factory = ForgotPasswordViewModel.Factory(container.authRepository)
        )
        ForgotPasswordRoute(
            viewModel = forgotPasswordViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onLinkSent = { email ->
                navController.navigate(OtpVerificationDestination(email = email))
            }
        )
    }

    composable<OtpVerificationDestination> { backStackEntry ->
        val destination: OtpVerificationDestination = backStackEntry.toRoute()
        val otpViewModel: OtpVerificationViewModel = viewModel(
            factory = OtpVerificationViewModel.Factory(container.authRepository, destination.email)
        )
        OtpVerificationRoute(
            viewModel = otpViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onVerified = {
                navController.navigate(ResetPasswordDestination(email = destination.email))
            }
        )
    }

    composable<ResetPasswordDestination> { backStackEntry ->
        val destination: ResetPasswordDestination = backStackEntry.toRoute()
        val resetViewModel: ResetPasswordViewModel = viewModel(
            factory = ResetPasswordViewModel.Factory(container.authRepository, destination.email)
        )
        ResetPasswordRoute(
            viewModel = resetViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onResetSuccess = {
                navController.navigate(PasswordUpdatedDestination) {
                    popUpTo(ForgotPasswordDestination) { inclusive = true }
                }
            }
        )
    }

    composable<PasswordUpdatedDestination> {
        PasswordResetSuccessScreen(
            onNavigateToSignIn = {
                navController.navigate(SignInDestination) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}
