package com.arvian.jelajahkota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaBottomNavBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.feature.auth.EmailSentConfirmationScreen
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
import com.arvian.jelajahkota.feature.favorites.FavoritesRoute
import com.arvian.jelajahkota.feature.favorites.FavoritesViewModel
import com.arvian.jelajahkota.feature.onboarding.OnboardingRoute
import com.arvian.jelajahkota.feature.onboarding.OnboardingViewModel
import com.arvian.jelajahkota.feature.onboarding.SplashScreen
import com.arvian.jelajahkota.feature.places.ExploreRoute
import com.arvian.jelajahkota.feature.places.ExploreViewModel
import com.arvian.jelajahkota.feature.places.HomeRoute
import com.arvian.jelajahkota.feature.places.HomeViewModel
import com.arvian.jelajahkota.feature.plans.DatePickerRoute
import com.arvian.jelajahkota.feature.plans.PlaceDetailRoute
import com.arvian.jelajahkota.feature.plans.PlanConfirmationRoute
import com.arvian.jelajahkota.feature.plans.PlansListRoute
import com.arvian.jelajahkota.feature.plans.PlansViewModel
import com.arvian.jelajahkota.feature.plans.SchedulePlanRoute
import com.arvian.jelajahkota.feature.profile.AvatarUploadRoute
import com.arvian.jelajahkota.feature.profile.EditProfileRoute
import com.arvian.jelajahkota.feature.profile.ProfileRoute
import com.arvian.jelajahkota.feature.profile.ProfileViewModel
import com.arvian.jelajahkota.feature.profile.SettingsRoute
import com.arvian.jelajahkota.feature.reviews.ReviewsRoute
import com.arvian.jelajahkota.feature.reviews.ReviewsViewModel
import com.arvian.jelajahkota.navigation.AvatarUploadDestination
import com.arvian.jelajahkota.navigation.DatePickerDestination
import com.arvian.jelajahkota.navigation.EditProfileDestination
import com.arvian.jelajahkota.navigation.EmailSentDestination
import com.arvian.jelajahkota.navigation.ExploreDestination
import com.arvian.jelajahkota.navigation.FavoritesDestination
import com.arvian.jelajahkota.navigation.ForgotPasswordDestination
import com.arvian.jelajahkota.navigation.MainDestination
import com.arvian.jelajahkota.navigation.OnboardingDestination
import com.arvian.jelajahkota.navigation.OtpVerificationDestination
import com.arvian.jelajahkota.navigation.PasswordUpdatedDestination
import com.arvian.jelajahkota.navigation.PlaceDetailDestination
import com.arvian.jelajahkota.navigation.PlanConfirmationDestination
import com.arvian.jelajahkota.navigation.PlansListDestination
import com.arvian.jelajahkota.navigation.ProfileDestination
import com.arvian.jelajahkota.navigation.ResetPasswordDestination
import com.arvian.jelajahkota.navigation.ReviewsDestination
import com.arvian.jelajahkota.navigation.SchedulePlanDestination
import com.arvian.jelajahkota.navigation.SettingsDestination
import com.arvian.jelajahkota.navigation.SignInDestination
import com.arvian.jelajahkota.navigation.SignUpDestination
import com.arvian.jelajahkota.navigation.SplashDestination
import com.arvian.jelajahkota.navigation.isTopLevel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as JelajahKotaApp
        val preferencesRepository = app.preferencesRepository

        setContent {
            JelajahKotaTheme {
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                // Collect onboarding completion persistence state
                val isOnboardingCompleted by preferencesRepository
                    .isOnboardingCompleted()
                    .collectAsStateWithLifecycle(initialValue = null)

                when (val completed = isOnboardingCompleted) {
                    null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandBluePrimary)
                        }
                    }
                    else -> {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        val isTopLevelDestination = currentDestination.isTopLevel()

                        val currentTab = when {
                            currentDestination?.hasRoute<MainDestination>() == true -> JelajahKotaTab.BERANDA
                            currentDestination?.hasRoute<ExploreDestination>() == true -> JelajahKotaTab.EKSPLORASI
                            currentDestination?.hasRoute<PlansListDestination>() == true -> JelajahKotaTab.JADWALKAN
                            currentDestination?.hasRoute<FavoritesDestination>() == true -> JelajahKotaTab.FAVORIT
                            currentDestination?.hasRoute<ProfileDestination>() == true -> JelajahKotaTab.PROFILE
                            else -> JelajahKotaTab.BERANDA
                        }

                        // Android Standard Back Navigation for Tabs:
                        // Pressing Back on any secondary tab returns to MainDestination before exiting
                        BackHandler(enabled = isTopLevelDestination && currentDestination?.hasRoute<MainDestination>() != true) {
                            navController.navigate(MainDestination) {
                                popUpTo(MainDestination) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                        // Independent Box container decouples NavHost from BottomBar height remeasurements,
                        // eliminating stutter/jank during screen transitions.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(White)
                        ) {
                            val startDestination: Any = if (completed) MainDestination else SplashDestination

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
                                // ----------------------------------------------------
                                // Onboarding & Splash Flow (Figma 415:300, 52:702, 1:5, 1:6)
                                // ----------------------------------------------------
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
                                        factory = OnboardingViewModel.Factory(preferencesRepository)
                                    )
                                    OnboardingRoute(
                                        viewModel = onboardingViewModel,
                                        onNavigateToMain = {
                                            coroutineScope.launch {
                                                preferencesRepository.setOnboardingCompleted(true)
                                            }
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

                                // ----------------------------------------------------
                                // Feature Auth Flow (Figma 1:7, 1:8, 2223:708, 474:446, 474:479, 474:520, 474:379)
                                // ----------------------------------------------------
                                composable<SignInDestination> {
                                    val signInViewModel: SignInViewModel = viewModel(
                                        factory = SignInViewModel.Factory(app.authRepository)
                                    )
                                    SignInRoute(
                                        viewModel = signInViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToSignUp = {
                                            navController.navigate(SignUpDestination) {
                                                popUpTo(SignInDestination) { inclusive = true }
                                            }
                                        },
                                        onNavigateToForgotPassword = { navController.navigate(ForgotPasswordDestination) },
                                        onSignInSuccess = {
                                            coroutineScope.launch {
                                                preferencesRepository.setOnboardingCompleted(true)
                                                snackbarHostState.showSnackbar("Selamat datang kembali!")
                                            }
                                            navController.navigate(MainDestination) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable<SignUpDestination> {
                                    val signUpViewModel: SignUpViewModel = viewModel(
                                        factory = SignUpViewModel.Factory(app.authRepository)
                                    )
                                    SignUpRoute(
                                        viewModel = signUpViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToSignIn = {
                                            navController.navigate(SignInDestination) {
                                                popUpTo(SignUpDestination) { inclusive = true }
                                            }
                                        },
                                        onSignUpSuccess = {
                                            coroutineScope.launch {
                                                preferencesRepository.setOnboardingCompleted(true)
                                                snackbarHostState.showSnackbar("Akun berhasil dibuat!")
                                            }
                                            navController.navigate(MainDestination) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable<ForgotPasswordDestination> {
                                    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(
                                        factory = ForgotPasswordViewModel.Factory(app.authRepository)
                                    )
                                    ForgotPasswordRoute(
                                        viewModel = forgotPasswordViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onLinkSent = { email ->
                                            navController.navigate(EmailSentDestination(email = email))
                                        }
                                    )
                                }

                                composable<EmailSentDestination> { backStackEntry ->
                                    val destination: EmailSentDestination = backStackEntry.toRoute()
                                    EmailSentConfirmationScreen(
                                        email = destination.email,
                                        onNavigateToOtp = {
                                            navController.navigate(OtpVerificationDestination(email = destination.email))
                                        },
                                        onChangeEmail = { navController.popBackStack() },
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }

                                composable<OtpVerificationDestination> { backStackEntry ->
                                    val destination: OtpVerificationDestination = backStackEntry.toRoute()
                                    val otpViewModel: OtpVerificationViewModel = viewModel(
                                        factory = OtpVerificationViewModel.Factory(app.authRepository, destination.email)
                                    )
                                    OtpVerificationRoute(
                                        viewModel = otpViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onVerified = {
                                            navController.navigate(ResetPasswordDestination(email = destination.email))
                                        }
                                    )
                                }

                                composable<ResetPasswordDestination> { backStackEntry ->
                                    val destination: ResetPasswordDestination = backStackEntry.toRoute()
                                    val resetViewModel: ResetPasswordViewModel = viewModel(
                                        factory = ResetPasswordViewModel.Factory(app.authRepository, destination.email)
                                    )
                                    ResetPasswordRoute(
                                        viewModel = resetViewModel,
                                        onNavigateBack = { navController.popBackStack() },
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
                                        },
                                        onNavigateBack = {
                                            navController.navigate(SignInDestination) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // ----------------------------------------------------
                                // Top-Level Shell Destinations (Unified Bottom Navigation)
                                // ----------------------------------------------------

                                // 1. Tab Beranda (Figma 16:252)
                                composable<MainDestination> {
                                    val homeViewModel: HomeViewModel = viewModel(
                                        factory = HomeViewModel.Factory(app.placesRepository)
                                    )
                                    HomeRoute(
                                        viewModel = homeViewModel,
                                        onNavigateToPlaceDetail = { placeId ->
                                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                                        },
                                        onNavigateToNotifications = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Kamu memiliki 5 notifikasi aktivitas baru.")
                                            }
                                        },
                                        onNavigateToSettings = {
                                            navController.navigate(SettingsDestination)
                                        }
                                    )
                                }

                                // 2. Tab Eksplorasi (Dedicated Catalogue Screen)
                                composable<ExploreDestination> {
                                    val exploreViewModel: ExploreViewModel = viewModel(
                                        factory = ExploreViewModel.Factory(app.placesRepository)
                                    )
                                    ExploreRoute(
                                        viewModel = exploreViewModel,
                                        onNavigateToPlaceDetail = { placeId ->
                                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                                        }
                                    )
                                }

                                // 3. Tab Jadwalkan (User Saved Visit Plans List)
                                composable<PlansListDestination> {
                                    val plansListViewModel: PlansViewModel = viewModel(
                                        factory = PlansViewModel.Factory(app.plansRepository, app.placesRepository)
                                    )
                                    PlansListRoute(
                                        viewModel = plansListViewModel,
                                        onNavigateToPlaceDetail = { placeId ->
                                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                                        },
                                        onNavigateToExplore = {
                                            navController.navigate(MainDestination) {
                                                popUpTo(MainDestination) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }

                                // 4. Tab Favorit (Figma 2247:174)
                                composable<FavoritesDestination> {
                                    val favoritesViewModel: FavoritesViewModel = viewModel(
                                        factory = FavoritesViewModel.Factory(app.favoritesRepository)
                                    )
                                    FavoritesRoute(
                                        viewModel = favoritesViewModel,
                                        onNavigateToPlaceDetail = { placeId ->
                                            navController.navigate(PlaceDetailDestination(placeId = placeId))
                                        },
                                        onNavigateToExplore = {
                                            navController.navigate(ExploreDestination) {
                                                popUpTo(MainDestination) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }

                                // 5. Tab Profile (Figma 46:770 & Clean Profile Architecture)
                                composable<ProfileDestination> {
                                    val profileViewModel: ProfileViewModel = viewModel(
                                        factory = ProfileViewModel.Factory(app.profileRepository, app.authRepository)
                                    )
                                    ProfileRoute(
                                        viewModel = profileViewModel,
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
                                            navController.navigate(FavoritesDestination) {
                                                popUpTo(MainDestination) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        onNavigateToPlans = {
                                            navController.navigate(PlansListDestination) {
                                                popUpTo(MainDestination) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        onSignedOut = {
                                            navController.navigate(SignInDestination) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // ----------------------------------------------------
                                // Destination Detail & Multi-Step Planning Flow
                                // ----------------------------------------------------
                                composable<PlaceDetailDestination> { backStackEntry ->
                                    val destination: PlaceDetailDestination = backStackEntry.toRoute()
                                    val plansViewModel: PlansViewModel = viewModel(
                                        factory = PlansViewModel.Factory(app.plansRepository, app.placesRepository, destination.placeId)
                                    )
                                    PlaceDetailRoute(
                                        viewModel = plansViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToSchedule = {
                                            navController.navigate(DatePickerDestination(placeId = destination.placeId))
                                        },
                                        onNavigateToReviews = { id ->
                                            navController.navigate(ReviewsDestination(placeId = id))
                                        }
                                    )
                                }

                                composable<SchedulePlanDestination> { backStackEntry ->
                                    val destination: SchedulePlanDestination = backStackEntry.toRoute()
                                    val plansViewModel: PlansViewModel = viewModel(
                                        factory = PlansViewModel.Factory(app.plansRepository, app.placesRepository, destination.placeId)
                                    )
                                    SchedulePlanRoute(
                                        viewModel = plansViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToDatePicker = {
                                            navController.navigate(DatePickerDestination(placeId = destination.placeId))
                                        },
                                        onNavigateToReviews = {
                                            navController.navigate(ReviewsDestination(placeId = destination.placeId))
                                        }
                                    )
                                }

                                composable<DatePickerDestination> { backStackEntry ->
                                    val destination: DatePickerDestination = backStackEntry.toRoute()
                                    val plansViewModel: PlansViewModel = viewModel(
                                        factory = PlansViewModel.Factory(app.plansRepository, app.placesRepository, destination.placeId)
                                    )
                                    DatePickerRoute(
                                        viewModel = plansViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToConfirmation = {
                                            navController.navigate(PlanConfirmationDestination(placeId = destination.placeId))
                                        }
                                    )
                                }

                                composable<PlanConfirmationDestination> { backStackEntry ->
                                    val destination: PlanConfirmationDestination = backStackEntry.toRoute()
                                    val plansViewModel: PlansViewModel = viewModel(
                                        factory = PlansViewModel.Factory(app.plansRepository, app.placesRepository, destination.placeId)
                                    )
                                    PlanConfirmationRoute(
                                        viewModel = plansViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onPlanConfirmed = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Rencana kunjungan berhasil dijadwalkan!")
                                            }
                                            navController.navigate(PlansListDestination) {
                                                popUpTo(MainDestination) { inclusive = false }
                                            }
                                        }
                                    )
                                }

                                // ----------------------------------------------------
                                // Feature Reviews Flow (Figma 40:720)
                                // ----------------------------------------------------
                                composable<ReviewsDestination> { backStackEntry ->
                                    val destination: ReviewsDestination = backStackEntry.toRoute()
                                    val reviewsViewModel: ReviewsViewModel = viewModel(
                                        factory = ReviewsViewModel.Factory(app.reviewsRepository, destination.placeId)
                                    )
                                    ReviewsRoute(
                                        viewModel = reviewsViewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }

                                // ----------------------------------------------------
                                // Sub-Flow Settings & Profile Edit (Figma 265:1059, 1:10)
                                // ----------------------------------------------------
                                composable<SettingsDestination> {
                                    val profileViewModel: ProfileViewModel = viewModel(
                                        factory = ProfileViewModel.Factory(app.profileRepository, app.authRepository)
                                    )
                                    SettingsRoute(
                                        viewModel = profileViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToEditProfile = {
                                            navController.navigate(EditProfileDestination)
                                        },
                                        onSignedOut = {
                                            navController.navigate(SignInDestination) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        },
                                        isRootTab = false
                                    )
                                }

                                composable<EditProfileDestination> {
                                    val profileViewModel: ProfileViewModel = viewModel(
                                        factory = ProfileViewModel.Factory(app.profileRepository, app.authRepository)
                                    )
                                    EditProfileRoute(
                                        viewModel = profileViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onSaved = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Profil berhasil disimpan!")
                                            }
                                            navController.navigate(AvatarUploadDestination)
                                        }
                                    )
                                }

                                composable<AvatarUploadDestination> {
                                    val profileViewModel: ProfileViewModel = viewModel(
                                        factory = ProfileViewModel.Factory(app.profileRepository, app.authRepository)
                                    )
                                    AvatarUploadRoute(
                                        viewModel = profileViewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onSkip = {
                                            navController.navigate(ProfileDestination) {
                                                popUpTo(ProfileDestination) { inclusive = true }
                                            }
                                        },
                                        onSaved = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Foto profil berhasil diperbarui!")
                                            }
                                            navController.navigate(ProfileDestination) {
                                                popUpTo(ProfileDestination) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }

                            // Unified Bottom Navigation Bar as smooth overlay:
                            // Completely decoupled from NavHost layout, eliminating layout remeasurement spikes
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
                                            JelajahKotaTab.EKSPLORASI -> ExploreDestination
                                            JelajahKotaTab.JADWALKAN -> PlansListDestination
                                            JelajahKotaTab.FAVORIT -> FavoritesDestination
                                            JelajahKotaTab.PROFILE -> ProfileDestination
                                        }
                                        navController.navigate(destination) {
                                            popUpTo(MainDestination) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }

                            // Notification & Feedback SnackbarHost
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = if (isTopLevelDestination) 90.dp else 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}