package com.arvian.jelajahkota.feature.places

import androidx.compose.runtime.Immutable
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.UserProfile

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val searchQuery: String = "",
    val cityDestinations: List<CityDestination> = emptyList(),
    val popularPlaces: List<Place> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCategory: String? = null,
    val selectedCity: String? = null,
    val selectedNavTab: HomeNavTab = HomeNavTab.BERANDA
)

enum class HomeNavTab {
    BERANDA,
    EKSPLORASI,
    JADWALKAN,
    FAVORIT,
    PROFILE
}
