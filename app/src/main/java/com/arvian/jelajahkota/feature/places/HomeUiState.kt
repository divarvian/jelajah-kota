package com.arvian.jelajahkota.feature.places

import androidx.compose.runtime.Immutable
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.User

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val unreadNotificationsCount: Int = 2,
    val searchQuery: String = "",
    val cityDestinations: List<CityDestination> = emptyList(),
    val allPlaces: List<Place> = emptyList(),
    val popularPlaces: List<Place> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCategory: String? = null,
    val selectedCity: String? = null
) {
    val selectedCityName: String?
        get() = selectedCity?.let { id ->
            cityDestinations.find { it.id == id }?.name
        }

    val selectedCategoryName: String?
        get() = selectedCategory?.let { id ->
            categories.find { it.id == id }?.name
        }

    val filteredPlaces: List<Place>
        get() {
            var list = if (searchQuery.isNotBlank() || selectedCity != null || selectedCategory != null) {
                allPlaces
            } else {
                popularPlaces
            }

            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim()
                list = list.filter {
                    it.name.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true) ||
                        it.city.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                }
            }

            val cityName = selectedCityName
            if (!cityName.isNullOrBlank()) {
                list = list.filter { it.city.equals(cityName, ignoreCase = true) }
            }

            val categoryName = selectedCategoryName
            if (!categoryName.isNullOrBlank()) {
                list = list.filter { it.category.equals(categoryName, ignoreCase = true) }
            }

            return list
        }
}
