package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.reviews.MyReviewsRoute
import com.arvian.jelajahkota.feature.reviews.MyReviewsViewModel
import com.arvian.jelajahkota.feature.reviews.ReviewsRoute
import com.arvian.jelajahkota.feature.reviews.ReviewsViewModel

internal fun NavGraphBuilder.reviewsNavGraph(
    navController: NavController,
    container: AppContainer,
    showMessage: (String) -> Unit
) {
    composable<ReviewsDestination> { backStackEntry ->
        val destination: ReviewsDestination = backStackEntry.toRoute()
        val reviewsViewModel: ReviewsViewModel = viewModel(
            factory = ReviewsViewModel.Factory(
                reviewsRepository = container.reviewsRepository,
                profileRepository = container.profileRepository,
                placeId = destination.placeId,
                placesRepository = container.placesRepository
            )
        )
        ReviewsRoute(
            viewModel = reviewsViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToSignIn = { navController.navigate(SignInDestination) },
            onShowMessage = showMessage
        )
    }

    composable<MyReviewsDestination> {
        val myReviewsViewModel: MyReviewsViewModel = viewModel(
            factory = MyReviewsViewModel.Factory(
                reviewsRepository = container.reviewsRepository,
                profileRepository = container.profileRepository,
                placesRepository = container.placesRepository
            )
        )
        MyReviewsRoute(
            viewModel = myReviewsViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToPlaceDetail = { placeId ->
                navController.navigate(PlaceDetailDestination(placeId = placeId))
            },
            onNavigateToExplore = {
                navController.navigateToMainTab(
                    coordinator = container.mainShellCoordinator,
                    tab = JelajahKotaTab.EKSPLORASI,
                    exploreFilter = ExploreFilterRequest()
                )
            },
            onShowMessage = showMessage
        )
    }
}
