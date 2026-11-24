package com.arvian.jelajahkota.feature.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val placesRepository: PlacesRepository,
    initialQuery: String = "",
    initialCity: String? = null,
    initialCategory: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ExploreUiState(
            searchQuery = initialQuery,
            selectedCity = initialCity?.takeIf { it.isNotBlank() && it != "Semua" },
            selectedCategory = initialCategory?.takeIf { it.isNotBlank() && it != "Semua" },
            isLoading = true
        )
    )
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                placesRepository.getPlaces(),
                placesRepository.getCityDestinations(),
                placesRepository.getCategories()
            ) { places, cities, categories ->
                val cityNames = (cities.map { it.name } + places.map { it.city })
                    .distinct()
                val categoryNames = (categories.map { it.name } + places.map { it.category })
                    .filter { it.isNotBlank() }
                    .distinct()

                Triple(places, listOf("Semua") + cityNames, listOf("Semua") + categoryNames)
            }.collect { (places, cityOptions, categoryOptions) ->
                _uiState.update { current ->
                    current.copy(
                        allPlaces = places,
                        availableCities = cityOptions,
                        availableCategories = categoryOptions,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCitySelected(city: String?) {
        _uiState.update {
            val nextCity = if (it.selectedCity == city || city == "Semua") null else city
            it.copy(selectedCity = nextCity)
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update {
            val nextCat = if (it.selectedCategory == category || category == "Semua") null else category
            it.copy(selectedCategory = nextCat)
        }
    }

    fun onSortOptionSelected(sortOption: ExploreSortOption) {
        _uiState.update { it.copy(selectedSortOption = sortOption) }
    }

    fun onToggleView(isGrid: Boolean) {
        _uiState.update { it.copy(isGridView = isGrid) }
    }

    fun onResetFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCity = null,
                selectedCategory = null,
                selectedSortOption = ExploreSortOption.POPULAR
            )
        }
    }

    class Factory(
        private val placesRepository: PlacesRepository,
        private val initialQuery: String = "",
        private val initialCity: String? = null,
        private val initialCategory: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                return ExploreViewModel(
                    placesRepository = placesRepository,
                    initialQuery = initialQuery,
                    initialCity = initialCity,
                    initialCategory = initialCategory
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
