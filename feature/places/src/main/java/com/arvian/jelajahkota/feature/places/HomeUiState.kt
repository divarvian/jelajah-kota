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
    val allPlaces: List<Place> = emptyList(),
    val popularPlaces: List<Place> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCategory: String? = null,
    val selectedCity: String? = null,
    val selectedNavTab: HomeNavTab = HomeNavTab.BERANDA
) {
    val filteredPlaces: List<Place>
        get() {
            var list = if (searchQuery.isNotBlank() || selectedCity != null || selectedCategory != null) {
                allPlaces
            } else {
                popularPlaces
            }

            if (searchQuery.isNotBlank()) {
                list = list.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.location.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
                }
            }

            if (!selectedCity.isNullOrBlank()) {
                list = list.filter {
                    when (selectedCity) {
                        "malang" -> it.city.equals("Malang", ignoreCase = true)
                        "jogja" -> it.city.equals("Yogyakarta", ignoreCase = true)
                        "solo" -> it.city.equals("Solo", ignoreCase = true)
                        "bdg" -> it.city.equals("Bandung", ignoreCase = true)
                        "sby" -> it.city.equals("Surabaya", ignoreCase = true)
                        "jkt" -> it.city.equals("Jakarta", ignoreCase = true)
                        else -> true
                    }
                }
            }

            if (!selectedCategory.isNullOrBlank()) {
                list = list.filter {
                    when (selectedCategory) {
                        "cat_city" -> it.category.equals("City", ignoreCase = true)
                        "cat_heritage" -> it.category.equals("Heritage", ignoreCase = true)
                        "cat_mountain" -> it.category.equals("Mountain", ignoreCase = true)
                        "cat_nature" -> it.category.equals("Nature", ignoreCase = true)
                        else -> true
                    }
                }
            }

            return list
        }
}

enum class HomeNavTab {
    BERANDA,
    EKSPLORASI,
    JADWALKAN,
    FAVORIT,
    PROFILE
}
