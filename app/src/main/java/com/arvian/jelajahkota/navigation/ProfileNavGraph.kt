package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.profile.AvatarUploadRoute
import com.arvian.jelajahkota.feature.profile.EditProfileRoute
import com.arvian.jelajahkota.feature.profile.ProfileViewModel
import com.arvian.jelajahkota.feature.profile.SettingsRoute

private fun createProfileFactory(container: AppContainer): ProfileViewModel.Factory =
    ProfileViewModel.Factory(
        profileRepository = container.profileRepository,
        authRepository = container.authRepository,
        preferencesRepository = container.preferencesRepository,
        favoritesRepository = container.favoritesRepository,
        plansRepository = container.plansRepository,
        reviewsRepository = container.reviewsRepository,
        locationProvider = container.locationProvider
    )

internal fun NavGraphBuilder.profileNavGraph(
    navController: NavController,
    container: AppContainer,
    showMessage: (String) -> Unit
) {
    composable<SettingsDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        SettingsRoute(
            viewModel = profileViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onShowMessage = showMessage
        )
    }

    composable<EditProfileDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        EditProfileRoute(
            viewModel = profileViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onSaved = {
                showMessage("Profil berhasil disimpan!")
                navController.safePopBackStack()
            }
        )
    }

    composable<AvatarUploadDestination> {
        val profileViewModel: ProfileViewModel = viewModel(factory = createProfileFactory(container))
        AvatarUploadRoute(
            viewModel = profileViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onSaved = {
                showMessage("Foto profil berhasil diperbarui!")
                navController.safePopBackStack()
            }
        )
    }
}
