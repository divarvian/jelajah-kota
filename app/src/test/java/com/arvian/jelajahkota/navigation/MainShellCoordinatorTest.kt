package com.arvian.jelajahkota.navigation

import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MainShellCoordinatorTest {

    @Test
    fun homeMalangToExploreAndBackToHomeTab_neverGetsStuck() {
        val coordinator = MainShellCoordinator()
        assertEquals(JelajahKotaTab.BERANDA, coordinator.selectedTab.value)
        assertNull(coordinator.pendingExploreFilter.value)

        // 1. User clicks "Malang" on Beranda
        coordinator.openExploreWithFilter(city = "Malang")
        assertEquals(JelajahKotaTab.EKSPLORASI, coordinator.selectedTab.value)
        val filter = coordinator.pendingExploreFilter.value
        assertNotNull(filter)
        assertEquals("Malang", filter?.city)
        assertNull(filter?.category)
        assertEquals("", filter?.query)

        // MainShell consumes pending filter
        coordinator.consumeExploreFilter(filter!!.requestId)
        assertNull(coordinator.pendingExploreFilter.value)

        // 2. User taps "Beranda" in BottomNavBar -> MUST switch immediately
        coordinator.selectTab(JelajahKotaTab.BERANDA)
        assertEquals(JelajahKotaTab.BERANDA, coordinator.selectedTab.value)

        // 3. User taps "Eksplorasi" again -> switches back cleanly
        coordinator.selectTab(JelajahKotaTab.EKSPLORASI)
        assertEquals(JelajahKotaTab.EKSPLORASI, coordinator.selectedTab.value)
    }

    @Test
    fun openExploreWithFilter_normalizesSemuaAndBlankFilters() {
        val coordinator = MainShellCoordinator()
        coordinator.openExploreWithFilter(query = "Bromo", city = "Semua", category = "   ")

        val request = coordinator.pendingExploreFilter.value
        assertNotNull(request)
        assertEquals("Bromo", request?.query)
        assertNull(request?.city)
        assertNull(request?.category)
    }

    @Test
    fun consumeExploreFilter_ignoresMismatchedRequestId() {
        val coordinator = MainShellCoordinator()
        coordinator.openExploreWithFilter(city = "Malang")
        val firstId = coordinator.pendingExploreFilter.value!!.requestId

        coordinator.openExploreWithFilter(category = "Wisata Alam")
        val secondId = coordinator.pendingExploreFilter.value!!.requestId

        coordinator.consumeExploreFilter(firstId)
        assertEquals("Wisata Alam", coordinator.pendingExploreFilter.value?.category)

        coordinator.consumeExploreFilter(secondId)
        assertNull(coordinator.pendingExploreFilter.value)
    }

    @Test
    fun selectAllFiveTabsAndResetToHome_worksPredictably() {
        val coordinator = MainShellCoordinator()
        JelajahKotaTab.entries.forEach { tab ->
            coordinator.selectTab(tab)
            assertEquals(tab, coordinator.selectedTab.value)
        }

        coordinator.openExploreWithFilter(city = "Malang")
        coordinator.resetToHome()
        assertEquals(JelajahKotaTab.BERANDA, coordinator.selectedTab.value)
        assertNull(coordinator.pendingExploreFilter.value)
    }
}
