package com.arvian.jelajahkota.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaBottomNavBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.AppSettings
import com.arvian.jelajahkota.di.AppContainer
import kotlinx.coroutines.launch

@Composable
fun JelajahKotaAppRoot(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val appSettings by container.preferencesRepository
        .observeAppSettings()
        .collectAsStateWithLifecycle(initialValue = AppSettings())

    val isOnboardingCompleted by container.preferencesRepository
        .isOnboardingCompleted()
        .collectAsStateWithLifecycle(initialValue = null)

    // Lock startDestination once on cold start so updating onboarding completion
    // never recreates or resets an active NavHost.
    var initialStartDestination by remember { mutableStateOf<Any?>(null) }
    LaunchedEffect(isOnboardingCompleted) {
        if (initialStartDestination == null && isOnboardingCompleted != null) {
            initialStartDestination = if (isOnboardingCompleted == true) {
                MainDestination
            } else {
                SplashDestination
            }
        }
    }

    JelajahKotaTheme(darkTheme = appSettings.darkModeEnabled) {
        val startDestination = initialStartDestination
        if (startDestination == null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandBluePrimary)
            }
            return@JelajahKotaTheme
        }

        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val isTopLevelDestination = currentDestination.isTopLevel()

        val showMessage: (String) -> Unit = { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }

        val currentTab = when {
            currentDestination?.hasRoute<MainDestination>() == true -> JelajahKotaTab.BERANDA
            currentDestination?.hasRoute<ExploreDestination>() == true -> JelajahKotaTab.EKSPLORASI
            currentDestination?.hasRoute<PlansListDestination>() == true -> JelajahKotaTab.JADWALKAN
            currentDestination?.hasRoute<FavoritesDestination>() == true -> JelajahKotaTab.FAVORIT
            currentDestination?.hasRoute<ProfileDestination>() == true -> JelajahKotaTab.PROFILE
            else -> JelajahKotaTab.BERANDA
        }

        BackHandler(enabled = isTopLevelDestination && currentDestination?.hasRoute<MainDestination>() != true) {
            navController.navigateTopLevelTab(MainDestination)
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(White)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize(),
                enterTransition = {
                    val targetTop = targetState.destination.isTopLevel()
                    val initialTop = initialState.destination.isTopLevel()
                    if (targetTop && initialTop) {
                        fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> (fullWidth * 0.3f).toInt() },
                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(durationMillis = 220))
                    }
                },
                exitTransition = {
                    val targetTop = targetState.destination.isTopLevel()
                    val initialTop = initialState.destination.isTopLevel()
                    if (targetTop && initialTop) {
                        fadeOut(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() },
                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(durationMillis = 200))
                    }
                },
                popEnterTransition = {
                    val targetTop = targetState.destination.isTopLevel()
                    val initialTop = initialState.destination.isTopLevel()
                    if (targetTop && initialTop) {
                        fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() },
                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(durationMillis = 220))
                    }
                },
                popExitTransition = {
                    val targetTop = targetState.destination.isTopLevel()
                    val initialTop = initialState.destination.isTopLevel()
                    if (targetTop && initialTop) {
                        fadeOut(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> (fullWidth * 0.3f).toInt() },
                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(durationMillis = 200))
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
                placesNavGraph(
                    navController = navController,
                    container = container,
                    showMessage = showMessage
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
                favoritesNavGraph(
                    navController = navController,
                    container = container
                )
                profileNavGraph(
                    navController = navController,
                    container = container,
                    showMessage = showMessage
                )
            }

            AnimatedVisibility(
                visible = isTopLevelDestination,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(150)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(120)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                JelajahKotaBottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        val destination: Any = when (tab) {
                            JelajahKotaTab.BERANDA -> MainDestination
                            JelajahKotaTab.EKSPLORASI -> ExploreDestination()
                            JelajahKotaTab.JADWALKAN -> PlansListDestination
                            JelajahKotaTab.FAVORIT -> FavoritesDestination
                            JelajahKotaTab.PROFILE -> ProfileDestination
                        }
                        navController.navigateTopLevelTab(destination)
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = if (isTopLevelDestination) 76.dp else 16.dp)
            )
        }
    }
}

internal fun NavController.navigateTopLevelTab(destination: Any, restore: Boolean = true) {
    navigate(destination) {
        popUpTo(MainDestination) {
            saveState = restore
        }
        launchSingleTop = true
        restoreState = restore
    }
}
