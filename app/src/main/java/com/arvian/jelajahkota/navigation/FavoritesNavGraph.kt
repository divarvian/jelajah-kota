package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.favorites.FavoritesRoute
import com.arvian.jelajahkota.feature.favorites.FavoritesViewModel

internal fun NavGraphBuilder.favoritesNavGraph(
    navController: NavController,
    container: AppContainer
) {
    composable<FavoritesDestination> {
        val favoritesViewModel: FavoritesViewModel = viewModel(
            factory = FavoritesViewModel.Factory(container.favoritesRepository)
        )
        FavoritesRoute(
            viewModel = favoritesViewModel,
            onNavigateToPlaceDetail = { placeId ->
                navController.navigate(PlaceDetailDestination(placeId = placeId))
            },
            onNavigateToExplore = {
                navController.navigateTopLevelTab(ExploreDestination())
            }
        )
    }
}
