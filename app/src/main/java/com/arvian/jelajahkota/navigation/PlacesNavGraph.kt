package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.places.PlaceDetailRoute
import com.arvian.jelajahkota.feature.places.PlaceDetailViewModel

internal fun NavGraphBuilder.placesNavGraph(
    navController: NavController,
    container: AppContainer
) {
    composable<PlaceDetailDestination> { backStackEntry ->
        val destination: PlaceDetailDestination = backStackEntry.toRoute()
        val detailViewModel: PlaceDetailViewModel = viewModel(
            factory = PlaceDetailViewModel.Factory(
                placesRepository = container.placesRepository,
                favoritesRepository = container.favoritesRepository,
                reviewsRepository = container.reviewsRepository,
                authRepository = container.authRepository,
                placeId = destination.placeId,
                locationProvider = container.locationProvider
            )
        )
        PlaceDetailRoute(
            viewModel = detailViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToSchedule = {
                navController.navigate(DatePickerDestination(placeId = destination.placeId))
            },
            onNavigateToReviews = { id ->
                navController.navigate(ReviewsDestination(placeId = id))
            },
            onNavigateToGallery = { id ->
                navController.navigate(PlaceGalleryDestination(placeId = id))
            },
            onNavigateToSignIn = {
                navController.navigate(SignInDestination)
            }
        )
    }
}
