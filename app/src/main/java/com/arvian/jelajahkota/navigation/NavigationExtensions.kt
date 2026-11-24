package com.arvian.jelajahkota.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab

/**
 * Returns true only when there is a real previous destination underneath the current entry.
 * Prevents rendering a Back button on root destinations (such as SignIn after Onboarding/Logout).
 */
internal fun NavController.canNavigateBack(): Boolean = previousBackStackEntry != null

/**
 * Safely pops the current destination only when another entry exists beneath it AND the
 * current entry is RESUMED. Prevents both popping the sole root entry into DESTROYED state
 * and interrupting an in-flight Predictive Back / SeekableTransitionState animation via rapid taps.
 */
internal fun NavController.safePopBackStack(): Boolean {
    val currentEntry = currentBackStackEntry ?: return false
    if (!currentEntry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return false
    return if (previousBackStackEntry != null) popBackStack() else false
}

/**
 * Switches the active top-level tab inside [MainShellDestination] and navigates back to
 * the existing [MainShellDestination] entry if present on the backstack, or launches it
 * cleanly as the new root when coming from Onboarding/Auth.
 */
internal fun NavController.navigateToMainTab(
    coordinator: MainShellCoordinator,
    tab: JelajahKotaTab = JelajahKotaTab.BERANDA,
    exploreFilter: ExploreFilterRequest? = null
) {
    if (exploreFilter != null) {
        coordinator.openExploreWithFilter(
            query = exploreFilter.query,
            city = exploreFilter.city,
            category = exploreFilter.category
        )
    } else {
        coordinator.selectTab(tab)
    }

    val poppedToExistingShell = popBackStack(MainShellDestination, inclusive = false)
    if (!poppedToExistingShell && !currentDestination.isMainShell()) {
        navigate(MainShellDestination) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}
