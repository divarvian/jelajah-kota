package com.arvian.jelajahkota.feature.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.common.location.LocationProvider
import com.arvian.jelajahkota.core.common.location.NoOpLocationProvider
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val placesRepository: PlacesRepository,
    initialQuery: String = "",
    initialCity: String? = null,
    initialCategory: String? = null,
    private val locationProvider: LocationProvider = NoOpLocationProvider(),
    private val preferencesRepository: UserPreferencesRepository? = null
) : ViewModel() {

    private val cachedPlaces = placesRepository.getCachedPlaces()
    private val cachedCities = placesRepository.getCachedCityDestinations()
    private val cachedCategories = placesRepository.getCachedCategories()

    private val _uiState = MutableStateFlow(
        ExploreUiState(
            searchQuery = initialQuery,
            selectedCity = initialCity?.takeIf { it.isNotBlank() && it != "Semua" },
            selectedCategory = initialCategory?.takeIf { it.isNotBlank() && it != "Semua" },
            allPlaces = cachedPlaces,
            availableCities = buildCityOptions(cachedCities.map { it.name }, cachedPlaces),
            availableCategories = buildCategoryOptions(cachedCategories.map { it.name }, cachedPlaces),
            userLocation = locationProvider.userLocation.value,
            isLoading = cachedPlaces.isEmpty()
        )
    )
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        viewModelScope.launch {
            if (_uiState.value.allPlaces.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }
            val locationEnabledFlow = preferencesRepository?.observeAppSettings()?.map { it.locationEnabled }
                ?: flowOf(true)

            combine(
                placesRepository.getPlaces(),
                placesRepository.getCityDestinations(),
                placesRepository.getCategories(),
                combine(locationProvider.userLocation, locationEnabledFlow) { coords, enabled ->
                    if (enabled) coords else null
                }
            ) { places, cities, categories, coords ->
                ExploreSnapshot(
                    places = places,
                    cities = buildCityOptions(cities.map { it.name }, places),
                    categories = buildCategoryOptions(categories.map { it.name }, places),
                    userLocation = coords
                )
            }.collect { snapshot ->
                _uiState.update { current ->
                    current.copy(
                        allPlaces = snapshot.places,
                        availableCities = snapshot.cities,
                        availableCategories = snapshot.categories,
                        userLocation = snapshot.userLocation,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun applyExternalFilter(
        query: String = "",
        city: String? = null,
        category: String? = null
    ) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                selectedCity = city?.takeIf { it.isNotBlank() && it != "Semua" },
                selectedCategory = category?.takeIf { it.isNotBlank() && it != "Semua" }
            )
        }
    }

    fun hasLocationPermission(): Boolean = locationProvider.hasLocationPermission()

    fun onLocationPermissionGrantedForNearest() {
        viewModelScope.launch {
            preferencesRepository?.setLocationEnabled(true)
            locationProvider.refreshLocation()
            _uiState.update { it.copy(selectedSortOption = ExploreSortOption.NEAREST) }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                placesRepository.refresh()
                if (locationProvider.hasLocationPermission()) {
                    locationProvider.refreshLocation()
                }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
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
        if (sortOption == ExploreSortOption.NEAREST && locationProvider.hasLocationPermission()) {
            locationProvider.refreshLocation()
        }
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

    private fun buildCityOptions(
        cityNames: List<String>,
        places: List<Place>
    ): List<String> {
        val activePlaceCities = places.map { it.city.lowercase() }.toSet()
        val ordered = if (activePlaceCities.isEmpty()) {
            cityNames
        } else {
            (cityNames.filter { it.lowercase() in activePlaceCities } + places.map { it.city })
        }
        return listOf("Semua") + ordered.filter { it.isNotBlank() }.distinct()
    }

    private fun buildCategoryOptions(
        categoryNames: List<String>,
        places: List<Place>
    ): List<String> {
        val activeCategories = places.map { it.category.lowercase() }.toSet()
        val ordered = if (activeCategories.isEmpty()) {
            categoryNames
        } else {
            (categoryNames.filter { it.lowercase() in activeCategories } + places.map { it.category })
        }
        return listOf("Semua") + ordered.filter { it.isNotBlank() }.distinct()
    }

    private data class ExploreSnapshot(
        val places: List<Place>,
        val cities: List<String>,
        val categories: List<String>,
        val userLocation: UserCoordinates?
    )

    class Factory(
        private val placesRepository: PlacesRepository,
        private val initialQuery: String = "",
        private val initialCity: String? = null,
        private val initialCategory: String? = null,
        private val locationProvider: LocationProvider = NoOpLocationProvider(),
        private val preferencesRepository: UserPreferencesRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                return ExploreViewModel(
                    placesRepository = placesRepository,
                    initialQuery = initialQuery,
                    initialCity = initialCity,
                    initialCategory = initialCategory,
                    locationProvider = locationProvider,
                    preferencesRepository = preferencesRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
