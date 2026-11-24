package com.arvian.jelajahkota.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.feature.plans.DatePickerRoute
import com.arvian.jelajahkota.feature.plans.PlanConfirmationRoute
import com.arvian.jelajahkota.feature.plans.PlansViewModel

internal fun NavGraphBuilder.plansNavGraph(
    navController: NavController,
    container: AppContainer,
    showMessage: (String) -> Unit
) {
    composable<DatePickerDestination> { backStackEntry ->
        val destination: DatePickerDestination = backStackEntry.toRoute()
        val plansViewModel: PlansViewModel = viewModel(
            factory = PlansViewModel.Factory(
                plansRepository = container.plansRepository,
                placesRepository = container.placesRepository,
                placeId = destination.placeId,
                authRepository = container.authRepository
            )
        )
        DatePickerRoute(
            viewModel = plansViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onNavigateToConfirmation = { startEpochDay, endEpochDay ->
                navController.navigate(
                    PlanConfirmationDestination(
                        placeId = destination.placeId,
                        startEpochDay = startEpochDay,
                        endEpochDay = endEpochDay
                    )
                )
            }
        )
    }

    composable<PlanConfirmationDestination> { backStackEntry ->
        val destination: PlanConfirmationDestination = backStackEntry.toRoute()
        val plansViewModel: PlansViewModel = viewModel(
            factory = PlansViewModel.Factory(
                plansRepository = container.plansRepository,
                placesRepository = container.placesRepository,
                placeId = destination.placeId,
                initialStartEpochDay = destination.startEpochDay,
                initialEndEpochDay = destination.endEpochDay,
                authRepository = container.authRepository
            )
        )
        PlanConfirmationRoute(
            viewModel = plansViewModel,
            onNavigateBack = { navController.safePopBackStack() },
            onPlanConfirmed = {
                showMessage("Rencana kunjungan berhasil dijadwalkan!")
                navController.navigateToMainTab(
                    coordinator = container.mainShellCoordinator,
                    tab = JelajahKotaTab.JADWALKAN
                )
            }
        )
    }
}
