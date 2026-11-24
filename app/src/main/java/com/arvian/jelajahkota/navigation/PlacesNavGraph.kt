package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.places.ExploreRoute
import com.arvian.jelajahkota.feature.places.ExploreViewModel
import com.arvian.jelajahkota.feature.places.HomeRoute
import com.arvian.jelajahkota.feature.places.HomeViewModel
import com.arvian.jelajahkota.feature.places.PlaceDetailRoute
import com.arvian.jelajahkota.feature.places.PlaceDetailViewModel

internal fun NavGraphBuilder.placesNavGraph(
    navController: NavController,
    container: AppContainer,
    showMessage: (String) -> Unit
) {
    composable<MainDestination> {
        val homeViewModel: HomeViewModel = viewModel(
            factory = HomeViewModel.Factory(
                placesRepository = container.placesRepository,
                profileRepository = container.profileRepository
            )
        )
        HomeRoute(
            viewModel = homeViewModel,
            onNavigateToPlaceDetail = { placeId ->
                navController.navigate(PlaceDetailDestination(placeId = placeId))
            },
            onNavigateToNotifications = {
                showMessage("Belum ada notifikasi baru saat ini.")
            },
            onNavigateToSettings = {
                navController.navigate(SettingsDestination)
            },
            onNavigateToExplore = { query, city, category ->
                navController.navigateTopLevelTab(
                    destination = ExploreDestination(
                        initialQuery = query,
                        initialCity = city,
                        initialCategory = category
                    ),
                    restore = false
                )
            }
        )
    }

    composable<ExploreDestination> { backStackEntry ->
        val destination: ExploreDestination = backStackEntry.toRoute()
        val exploreViewModel: ExploreViewModel = viewModel(
            factory = ExploreViewModel.Factory(
                placesRepository = container.placesRepository,
                initialQuery = destination.initialQuery,
                initialCity = destination.initialCity,
                initialCategory = destination.initialCategory
            )
        )
        ExploreRoute(
            viewModel = exploreViewModel,
            onNavigateToPlaceDetail = { placeId ->
                navController.navigate(PlaceDetailDestination(placeId = placeId))
            }
        )
    }

    composable<PlaceDetailDestination> { backStackEntry ->
        val destination: PlaceDetailDestination = backStackEntry.toRoute()
        val detailViewModel: PlaceDetailViewModel = viewModel(
            factory = PlaceDetailViewModel.Factory(
                placesRepository = container.placesRepository,
                favoritesRepository = container.favoritesRepository,
                reviewsRepository = container.reviewsRepository,
                placeId = destination.placeId
            )
        )
        PlaceDetailRoute(
            viewModel = detailViewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToSchedule = {
                navController.navigate(DatePickerDestination(placeId = destination.placeId))
            },
            onNavigateToReviews = { id ->
                navController.navigate(ReviewsDestination(placeId = id))
            }
        )
    }
}
