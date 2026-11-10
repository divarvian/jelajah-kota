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

class HomeViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                placesRepository.getUserProfile(),
                placesRepository.getCityDestinations(),
                placesRepository.getPlaces(),
                placesRepository.getPopularPlaces(),
                placesRepository.getCategories()
            ) { user, cities, places, popular, categories ->
                HomeUiState(
                    isLoading = false,
                    userProfile = user,
                    cityDestinations = cities,
                    allPlaces = places,
                    popularPlaces = popular,
                    categories = categories
                )
            }.collect { state ->
                _uiState.update { current ->
                    state.copy(
                        searchQuery = current.searchQuery,
                        selectedCategory = current.selectedCategory,
                        selectedCity = current.selectedCity,
                        selectedNavTab = current.selectedNavTab
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCitySelected(cityId: String) {
        _uiState.update { current ->
            current.copy(selectedCity = if (current.selectedCity == cityId) null else cityId)
        }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { current ->
            current.copy(selectedCategory = if (current.selectedCategory == categoryId) null else categoryId)
        }
    }

    fun onNavTabSelected(tab: HomeNavTab) {
        _uiState.update { it.copy(selectedNavTab = tab) }
    }

    class Factory(private val placesRepository: PlacesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(placesRepository) as T
        }
    }
}
