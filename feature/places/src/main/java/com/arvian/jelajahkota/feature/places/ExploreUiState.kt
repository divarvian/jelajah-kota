package com.arvian.jelajahkota.feature.places

import com.arvian.jelajahkota.core.domain.model.Place

enum class ExploreSortOption(val label: String) {
    POPULAR("Paling Populer"),
    RATING_DESC("Rating Tertinggi"),
    PRICE_ASC("Harga Termurah"),
    NAME_ASC("Nama (A-Z)")
}

data class ExploreUiState(
    val searchQuery: String = "",
    val selectedCity: String? = null,
    val selectedCategory: String? = null,
    val selectedSortOption: ExploreSortOption = ExploreSortOption.POPULAR,
    val isGridView: Boolean = true,
    val availableCities: List<String> = listOf("Semua", "Malang", "Batu", "Bromo", "Jogja", "Semarang"),
    val availableCategories: List<String> = listOf("Semua", "Wisata Alam", "Budaya & Sejarah", "Kuliner", "Spot Foto", "Edukasi"),
    val allPlaces: List<Place> = emptyList(),
    val isLoading: Boolean = false
) {
    val filteredPlaces: List<Place>
        get() {
            var list = allPlaces

            // Filter search query
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                list = list.filter {
                    it.name.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.city.lowercase().contains(q) ||
                    it.location.lowercase().contains(q)
                }
            }

            // Filter city
            if (selectedCity != null && selectedCity != "Semua") {
                list = list.filter { it.city.equals(selectedCity, ignoreCase = true) }
            }

            // Filter category
            if (selectedCategory != null && selectedCategory != "Semua") {
                list = list.filter { it.category.contains(selectedCategory, ignoreCase = true) }
            }

            // Sort
            list = when (selectedSortOption) {
                ExploreSortOption.POPULAR -> list.sortedByDescending { it.reviewCount }
                ExploreSortOption.RATING_DESC -> list.sortedByDescending { it.rating }
                ExploreSortOption.PRICE_ASC -> list.sortedWith(compareBy {
                    if (it.ticketPrice.contains("Gratis", ignoreCase = true)) 0
                    else it.ticketPrice.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 999999
                })
                ExploreSortOption.NAME_ASC -> list.sortedBy { it.name }
            }

            return list
        }
}
