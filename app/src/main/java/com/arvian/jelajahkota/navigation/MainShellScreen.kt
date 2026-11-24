package com.arvian.jelajahkota.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaBottomNavBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.favorites.FavoritesRoute
import com.arvian.jelajahkota.feature.favorites.FavoritesViewModel
import com.arvian.jelajahkota.feature.places.ExploreRoute
import com.arvian.jelajahkota.feature.places.ExploreViewModel
import com.arvian.jelajahkota.feature.places.HomeRoute
import com.arvian.jelajahkota.feature.places.HomeViewModel
import com.arvian.jelajahkota.feature.plans.PlansListRoute
import com.arvian.jelajahkota.feature.plans.PlansViewModel
import com.arvian.jelajahkota.feature.profile.ProfileRoute
import com.arvian.jelajahkota.feature.profile.ProfileViewModel

/**
 * Unified top-level shell hosting the 5 bottom navigation tabs inside a single Material 3
 * [Scaffold]. Keeping the [JelajahKotaBottomNavBar] physically attached to this destination
 * guarantees seamless Android 16 Predictive Back transitions when returning from fullscreen
 * detail routes, preserves ViewModel & scroll state across all 5 tabs with zero loading-spinner
 * flicker, and eliminates NavHost popUpTo/saveState tab-trapping bugs.
 */
@Composable
internal fun MainShellScreen(
    container: AppContainer,
    navController: NavController,
    showMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coordinator = container.mainShellCoordinator
    val selectedTab by coordinator.selectedTab.collectAsStateWithLifecycle()
    val pendingExploreFilter by coordinator.pendingExploreFilter.collectAsStateWithLifecycle()
    val saveableStateHolder = rememberSaveableStateHolder()

    val homeFactory = remember(container) {
        HomeViewModel.Factory(
            placesRepository = container.placesRepository,
            profileRepository = container.profileRepository,
            locationProvider = container.locationProvider,
            preferencesRepository = container.preferencesRepository
        )
    }
    val exploreFactory = remember(container) {
        ExploreViewModel.Factory(
            placesRepository = container.placesRepository,
            locationProvider = container.locationProvider,
            preferencesRepository = container.preferencesRepository
        )
    }
    val plansFactory = remember(container) {
        PlansViewModel.Factory(
            plansRepository = container.plansRepository,
            placesRepository = container.placesRepository,
            authRepository = container.authRepository
        )
    }
    val favoritesFactory = remember(container) {
        FavoritesViewModel.Factory(
            favoritesRepository = container.favoritesRepository,
            authRepository = container.authRepository
        )
    }
    val profileFactory = remember(container) {
        ProfileViewModel.Factory(
            profileRepository = container.profileRepository,
            authRepository = container.authRepository,
            preferencesRepository = container.preferencesRepository,
            favoritesRepository = container.favoritesRepository,
            plansRepository = container.plansRepository,
            reviewsRepository = container.reviewsRepository,
            locationProvider = container.locationProvider
        )
    }

    // Scope all 5 tab ViewModels to the MainShell NavBackStackEntry so switching tabs
    // never destroys or recreates state and never triggers frame-0 spinner flashes.
    val homeViewModel: HomeViewModel = viewModel(factory = homeFactory)
    val exploreViewModel: ExploreViewModel = viewModel(factory = exploreFactory)
    val plansListViewModel: PlansViewModel = viewModel(factory = plansFactory)
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = favoritesFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = profileFactory)

    LaunchedEffect(pendingExploreFilter) {
        val request = pendingExploreFilter ?: return@LaunchedEffect
        exploreViewModel.applyExternalFilter(
            query = request.query,
            city = request.city,
            category = request.category
        )
        coordinator.consumeExploreFilter(request.requestId)
    }

    // Isolated so STARTED -> RESUMED lifecycle transitions during Back animations
    // never trigger a full recomposition of Scaffold, BottomNavBar, or the active tab.
    MainShellBackHandler(
        selectedTab = selectedTab,
        onReturnToHome = { coordinator.selectTab(JelajahKotaTab.BERANDA) }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            JelajahKotaBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = coordinator::selectTab
            )
        }
    ) { innerPadding ->
        val bottomBarPadding = innerPadding.calculateBottomPadding()

        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                (fadeIn(
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing)
                )).using(null)
            },
            label = "MainShellTabTransition"
        ) { tab ->
            saveableStateHolder.SaveableStateProvider(key = tab) {
                when (tab) {
                    JelajahKotaTab.BERANDA -> HomeRoute(
                        viewModel = homeViewModel,
                        bottomContentPadding = bottomBarPadding,
                        onNavigateToPlaceDetail = { placeId ->
                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                        },
                        onNavigateToNotifications = {
                            showMessage("Belum ada notifikasi baru saat ini.")
                        },
                        onNavigateToProfile = {
                            coordinator.selectTab(JelajahKotaTab.PROFILE)
                        },
                        onNavigateToExplore = { query, city, category ->
                            exploreViewModel.applyExternalFilter(
                                query = query,
                                city = city,
                                category = category
                            )
                            coordinator.selectTab(JelajahKotaTab.EKSPLORASI)
                        }
                    )

                    JelajahKotaTab.EKSPLORASI -> ExploreRoute(
                        viewModel = exploreViewModel,
                        bottomContentPadding = bottomBarPadding,
                        onShowMessage = showMessage,
                        onNavigateToPlaceDetail = { placeId ->
                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                        }
                    )

                    JelajahKotaTab.JADWALKAN -> PlansListRoute(
                        viewModel = plansListViewModel,
                        bottomContentPadding = bottomBarPadding,
                        onNavigateToPlaceDetail = { placeId ->
                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                        },
                        onNavigateToExplore = {
                            exploreViewModel.applyExternalFilter()
                            coordinator.selectTab(JelajahKotaTab.EKSPLORASI)
                        },
                        onNavigateToSignIn = {
                            navController.navigate(SignInDestination)
                        }
                    )

                    JelajahKotaTab.FAVORIT -> FavoritesRoute(
                        viewModel = favoritesViewModel,
                        bottomContentPadding = bottomBarPadding,
                        onNavigateToPlaceDetail = { placeId ->
                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                        },
                        onNavigateToExplore = {
                            exploreViewModel.applyExternalFilter()
                            coordinator.selectTab(JelajahKotaTab.EKSPLORASI)
                        },
                        onNavigateToSignIn = {
                            navController.navigate(SignInDestination)
                        }
                    )

                    JelajahKotaTab.PROFILE -> ProfileRoute(
                        viewModel = profileViewModel,
                        bottomContentPadding = bottomBarPadding,
                        onNavigateToSignIn = {
                            navController.navigate(SignInDestination)
                        },
                        onNavigateToEditProfile = {
                            navController.navigate(EditProfileDestination)
                        },
                        onNavigateToAvatarUpload = {
                            navController.navigate(AvatarUploadDestination)
                        },
                        onNavigateToSettings = {
                            navController.navigate(SettingsDestination)
                        },
                        onNavigateToFavorites = {
                            coordinator.selectTab(JelajahKotaTab.FAVORIT)
                        },
                        onNavigateToPlans = {
                            coordinator.selectTab(JelajahKotaTab.JADWALKAN)
                        },
                        onNavigateToMyReviews = {
                            navController.navigate(MyReviewsDestination)
                        },
                        onSignedOut = {
                            coordinator.resetToHome()
                            navController.navigate(SignInDestination) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainShellBackHandler(
    selectedTab: JelajahKotaTab,
    onReturnToHome: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    BackHandler(
        enabled = lifecycleState.isAtLeast(Lifecycle.State.RESUMED) &&
            selectedTab != JelajahKotaTab.BERANDA,
        onBack = onReturnToHome
    )
}
