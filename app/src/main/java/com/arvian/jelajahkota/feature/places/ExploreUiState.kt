package com.arvian.jelajahkota.feature.places

import androidx.compose.runtime.Immutable
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.domain.model.Place

enum class ExploreSortOption(val label: String) {
    POPULAR("Paling Populer"),
    NEAREST("Terdekat"),
    RATING_DESC("Rating Tertinggi"),
    PRICE_ASC("Harga Termurah"),
    NAME_ASC("Nama (A-Z)")
}

@Immutable
data class ExploreUiState(
    val searchQuery: String = "",
    val selectedCity: String? = null,
    val selectedCategory: String? = null,
    val selectedSortOption: ExploreSortOption = ExploreSortOption.POPULAR,
    val isGridView: Boolean = true,
    val availableCities: List<String> = listOf("Semua"),
    val availableCategories: List<String> = listOf("Semua"),
    val allPlaces: List<Place> = emptyList(),
    val userLocation: UserCoordinates? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
) {
    val filteredPlaces: List<Place>
        get() {
            var list = allPlaces

            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                list = list.filter {
                    it.name.lowercase().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        it.city.lowercase().contains(q) ||
                        it.location.lowercase().contains(q) ||
                        it.description.lowercase().contains(q)
                }
            }

            if (!selectedCity.isNullOrBlank() && selectedCity != "Semua") {
                list = list.filter { it.city.equals(selectedCity, ignoreCase = true) }
            }

            if (!selectedCategory.isNullOrBlank() && selectedCategory != "Semua") {
                list = list.filter { it.category.equals(selectedCategory, ignoreCase = true) }
            }

            val loc = userLocation
            return when (selectedSortOption) {
                ExploreSortOption.POPULAR -> list.sortedWith(
                    compareByDescending<Place> { it.isPopular }
                        .thenByDescending { it.reviewCount }
                        .thenByDescending { it.rating }
                )
                ExploreSortOption.NEAREST -> if (loc != null) {
                    list.sortedBy { it.distanceKmFrom(loc.latitude, loc.longitude) ?: Double.MAX_VALUE }
                } else {
                    list.sortedByDescending { it.isPopular }
                }
                ExploreSortOption.RATING_DESC -> list.sortedWith(
                    compareByDescending<Place> { it.rating }.thenByDescending { it.reviewCount }
                )
                ExploreSortOption.PRICE_ASC -> list.sortedWith(
                    compareBy<Place> { it.ticketPriceAmount }.thenByDescending { it.rating }
                )
                ExploreSortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
            }
        }
}
