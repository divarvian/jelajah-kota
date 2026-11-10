package com.arvian.jelajahkota.feature.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState(isLoading = true))
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            placesRepository.getPlaces().collect { places ->
                _uiState.update {
                    it.copy(
                        allPlaces = places,
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
        private val placesRepository: PlacesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                return ExploreViewModel(placesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
