package com.arvian.jelajahkota.navigation

import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

data class ExploreFilterRequest(
    val query: String = "",
    val city: String? = null,
    val category: String? = null,
    val requestId: Long = 0L
)

/**
 * Coordinates active tab selection and cross-tab Explore filter handoffs for [MainShellScreen].
 * Keeps top-level tab switching purely reactive so tabs can never get trapped in NavHost
 * popUpTo/saveState conflicts.
 */
class MainShellCoordinator(
    initialTab: JelajahKotaTab = JelajahKotaTab.BERANDA
) {
    private val requestCounter = AtomicLong(0L)

    private val _selectedTab = MutableStateFlow(initialTab)
    val selectedTab: StateFlow<JelajahKotaTab> = _selectedTab.asStateFlow()

    private val _pendingExploreFilter = MutableStateFlow<ExploreFilterRequest?>(null)
    val pendingExploreFilter: StateFlow<ExploreFilterRequest?> = _pendingExploreFilter.asStateFlow()

    fun selectTab(tab: JelajahKotaTab) {
        _selectedTab.value = tab
    }

    fun openExploreWithFilter(
        query: String = "",
        city: String? = null,
        category: String? = null
    ) {
        val nextId = requestCounter.incrementAndGet()
        _pendingExploreFilter.value = ExploreFilterRequest(
            query = query,
            city = city?.takeIf { it.isNotBlank() && it != "Semua" },
            category = category?.takeIf { it.isNotBlank() && it != "Semua" },
            requestId = nextId
        )
        _selectedTab.value = JelajahKotaTab.EKSPLORASI
    }

    fun consumeExploreFilter(requestId: Long) {
        _pendingExploreFilter.update { current ->
            if (current?.requestId == requestId) null else current
        }
    }

    fun resetToHome() {
        _pendingExploreFilter.value = null
        _selectedTab.value = JelajahKotaTab.BERANDA
    }
}
