package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.profile.AvatarUploadRoute
import com.arvian.jelajahkota.feature.profile.EditProfileRoute
import com.arvian.jelajahkota.feature.profile.ProfileRoute
import com.arvian.jelajahkota.feature.profile.ProfileViewModel
import com.arvian.jelajahkota.feature.profile.SettingsRoute

private fun createProfileFactory(container: AppContainer): ProfileViewModel.Factory =
    ProfileViewModel.Factory(
        profileRepository = container.profileRepository,
        authRepository = container.authRepository,
        preferencesRepository = container.preferencesRepository,
        favoritesRepository = container.favoritesRepository,
        plansRepository = container.plansRepository,
        reviewsRepository = container.reviewsRepository
    )

internal fun NavGraphBuilder.profileNavGraph(
    navController: NavController,
    container: AppContainer,
    showMessage: (String) -> Unit
) {
    composable<ProfileDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        ProfileRoute(
            viewModel = profileViewModel,
            onNavigateToEditProfile = { navController.navigate(EditProfileDestination) },
            onNavigateToAvatarUpload = { navController.navigate(AvatarUploadDestination) },
            onNavigateToSettings = { navController.navigate(SettingsDestination) },
            onNavigateToFavorites = { navController.navigateTopLevelTab(FavoritesDestination) },
            onNavigateToPlans = { navController.navigateTopLevelTab(PlansListDestination) },
            onShowMessage = showMessage,
            onSignedOut = {
                navController.navigate(SignInDestination) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    composable<SettingsDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        SettingsRoute(
            viewModel = profileViewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEditProfile = { navController.navigate(EditProfileDestination) },
            onShowMessage = showMessage,
            onSignedOut = {
                navController.navigate(SignInDestination) {
                    popUpTo(0) { inclusive = true }
                }
            },
            isRootTab = false
        )
    }

    composable<EditProfileDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        EditProfileRoute(
            viewModel = profileViewModel,
            onNavigateBack = { navController.popBackStack() },
            onSaved = {
                showMessage("Profil berhasil disimpan!")
                navController.navigate(AvatarUploadDestination)
            }
        )
    }

    composable<AvatarUploadDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        AvatarUploadRoute(
            viewModel = profileViewModel,
            onNavigateBack = { navController.popBackStack() },
            onSkip = {
                navController.navigateTopLevelTab(ProfileDestination)
            },
            onSaved = {
                showMessage("Foto profil berhasil diperbarui!")
                navController.navigateTopLevelTab(ProfileDestination)
            }
        )
    }
}
