package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.di.AppContainer
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
                placeId = destination.placeId
            )
        )
        ReviewsRoute(
            viewModel = reviewsViewModel,
            onNavigateBack = { navController.popBackStack() },
            onReviewSubmitted = {
                showMessage("Terima kasih! Ulasan Anda telah dipublikasikan.")
            }
        )
    }
}
