package com.arvian.jelajahkota.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.domain.model.AppSettings
import com.arvian.jelajahkota.di.AppContainer
import kotlinx.coroutines.launch

@Composable
fun JelajahKotaAppRoot(
    container: AppContainer,
    appSettings: AppSettings,
    modifier: Modifier = Modifier
) {
    val isOnboardingCompleted by container.preferencesRepository
        .isOnboardingCompleted()
        .collectAsStateWithLifecycle(initialValue = null)

    var initialStartDestination by remember { mutableStateOf<Any?>(null) }
    LaunchedEffect(isOnboardingCompleted) {
        if (initialStartDestination == null && isOnboardingCompleted != null) {
            initialStartDestination = if (isOnboardingCompleted == true) {
                MainShellDestination
            } else {
                SplashDestination
            }
        }
    }

    val startDestination = initialStartDestination ?: return

    JelajahKotaTheme(darkTheme = appSettings.darkModeEnabled) {
        val colorScheme = MaterialTheme.colorScheme
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val showMessage: (String) -> Unit = remember(coroutineScope, snackbarHostState) {
            { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize(),
                sizeTransform = { null },
                enterTransition = {
                    if (isForwardCrossfade(initialState.destination, targetState.destination)) {
                        fadeIn(animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                        )
                    }
                },
                exitTransition = {
                    if (isForwardCrossfade(initialState.destination, targetState.destination)) {
                        fadeOut(animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.22f).toInt() },
                            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                        )
                    }
                },
                popEnterTransition = {
                    if (initialState.destination.isRootAuthOrIntro() || targetState.destination.isRootAuthOrIntro()) {
                        fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.22f).toInt() },
                            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                        )
                    }
                },
                popExitTransition = {
                    if (initialState.destination.isRootAuthOrIntro() || targetState.destination.isRootAuthOrIntro()) {
                        fadeOut(animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                        )
                    }
                },
                // Override Navigation Compose 2.10 default scaleOut() ("shrink to center") so
                // Android 16 gesture & hardware Back always slide out cleanly like native apps.
                predictivePopEnterTransition = { _ ->
                    if (initialState.destination.isRootAuthOrIntro() || targetState.destination.isRootAuthOrIntro()) {
                        fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.22f).toInt() },
                            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                        )
                    }
                },
                predictivePopExitTransition = { _ ->
                    if (initialState.destination.isRootAuthOrIntro() || targetState.destination.isRootAuthOrIntro()) {
                        fadeOut(animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                        )
                    }
                }
            ) {
                onboardingNavGraph(
                    navController = navController,
                    container = container
                )
                authNavGraph(
                    navController = navController,
                    container = container,
                    coroutineScope = coroutineScope,
                    showMessage = showMessage
                )
                composable<MainShellDestination> {
                    MainShellScreen(
                        container = container,
                        navController = navController,
                        showMessage = showMessage
                    )
                }
                placesNavGraph(
                    navController = navController,
                    container = container
                )
                plansNavGraph(
                    navController = navController,
                    container = container,
                    showMessage = showMessage
                )
                reviewsNavGraph(
                    navController = navController,
                    container = container,
                    showMessage = showMessage
                )
                galleryNavGraph(
                    navController = navController,
                    container = container,
                    showMessage = showMessage
                )
                profileNavGraph(
                    navController = navController,
                    container = container,
                    showMessage = showMessage
                )
            }

            // Isolated from NavHost so navigation transitions never recompose the root Box or NavHost.
            AppSnackbarHost(
                navController = navController,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun AppSnackbarHost(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isMainShell = navBackStackEntry?.destination.isMainShell()

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = if (isMainShell) 76.dp else 16.dp)
    )
}

private fun NavDestination.isRootAuthOrIntro(): Boolean =
    hasRoute<SplashDestination>() ||
        hasRoute<OnboardingDestination>() ||
        hasRoute<PasswordUpdatedDestination>()

private fun isForwardCrossfade(initial: NavDestination, target: NavDestination): Boolean {
    if (initial.isRootAuthOrIntro() || target.isRootAuthOrIntro()) return true
    val isAuthSwap =
        (initial.hasRoute<SignInDestination>() && target.hasRoute<SignUpDestination>()) ||
            (initial.hasRoute<SignUpDestination>() && target.hasRoute<SignInDestination>())
    if (isAuthSwap) return true
    return target.hasRoute<MainShellDestination>() &&
        (initial.hasRoute<SignInDestination>() || initial.hasRoute<SignUpDestination>())
}
