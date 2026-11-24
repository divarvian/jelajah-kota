package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.gallery.PlaceGalleryRoute
import com.arvian.jelajahkota.feature.gallery.PlaceGalleryViewModel

internal fun NavGraphBuilder.galleryNavGraph(
    navController: NavController,
    container: AppContainer,
    showMessage: (String) -> Unit
) {
    composable<PlaceGalleryDestination> { backStackEntry ->
        val destination: PlaceGalleryDestination = backStackEntry.toRoute()
        val galleryViewModel: PlaceGalleryViewModel = viewModel(
            factory = PlaceGalleryViewModel.Factory(
                galleryRepository = container.galleryRepository,
                placesRepository = container.placesRepository,
                authRepository = container.authRepository,
                placeId = destination.placeId
            )
        )
        PlaceGalleryRoute(
            viewModel = galleryViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToSignIn = { navController.navigate(SignInDestination) },
            onShowMessage = showMessage
        )
    }
}
