package com.arvian.jelajahkota.feature.places

import androidx.compose.runtime.Immutable
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.User

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val user: User? = null,
    val unreadNotificationsCount: Int = 0,
    val allPlaces: List<Place> = emptyList(),
    val cityDestinations: List<CityDestination> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCityName: String? = null,
    val selectedCategoryName: String? = null,
    val userLocation: UserCoordinates? = null
) {
    val activeCities: List<CityDestination>
        get() {
            if (allPlaces.isEmpty()) return cityDestinations
            val activeNames = allPlaces.map { it.city.lowercase() }.toSet()
            return cityDestinations.filter { it.name.lowercase() in activeNames }
        }

    val placeCountByCity: Map<String, Int>
        get() = allPlaces.groupingBy { it.city.lowercase() }.eachCount()

    val filteredPlaces: List<Place>
        get() = allPlaces.filter { place ->
            val matchesCity = selectedCityName.isNullOrBlank() ||
                place.city.equals(selectedCityName, ignoreCase = true)
            val matchesCategory = selectedCategoryName.isNullOrBlank() ||
                place.category.equals(selectedCategoryName, ignoreCase = true)
            matchesCity && matchesCategory
        }

    val popularPlaces: List<Place>
        get() {
            val base = filteredPlaces
            val popular = base.filter { it.isPopular }
            return popular.ifEmpty { base.sortedByDescending { it.rating } }
        }

    val recommendedPlaces: List<Place>
        get() {
            val loc = userLocation
            val sorted = if (loc != null) {
                filteredPlaces.sortedBy { it.distanceKmFrom(loc.latitude, loc.longitude) ?: Double.MAX_VALUE }
            } else {
                filteredPlaces.sortedWith(
                    compareByDescending<Place> { it.isPopular }
                        .thenByDescending { it.rating }
                        .thenBy { it.name.lowercase() }
                )
            }
            return sorted.take(5)
        }

    val hasActiveFilter: Boolean
        get() = !selectedCityName.isNullOrBlank() || !selectedCategoryName.isNullOrBlank()
}
