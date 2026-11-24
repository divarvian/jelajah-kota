package com.arvian.jelajahkota.feature.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.common.location.LocationProvider
import com.arvian.jelajahkota.core.common.location.NoOpLocationProvider
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val placesRepository: PlacesRepository,
    private val profileRepository: ProfileRepository,
    private val locationProvider: LocationProvider = NoOpLocationProvider(),
    private val preferencesRepository: UserPreferencesRepository? = null
) : ViewModel() {

    private val initialCities = placesRepository.getCachedCityDestinations()
    private val initialPlaces = placesRepository.getCachedPlaces()
    private val initialCategories = placesRepository.getCachedCategories()

    private val _uiState = MutableStateFlow(
        HomeUiState(
            isLoading = initialCities.isEmpty() && initialPlaces.isEmpty(),
            allPlaces = initialPlaces,
            cityDestinations = initialCities,
            categories = initialCategories,
            userLocation = locationProvider.userLocation.value
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeLocationPreference()
    }

    private fun observeLocationPreference() {
        val prefs = preferencesRepository ?: return
        viewModelScope.launch {
            prefs.observeAppSettings()
                .map { it.locationEnabled }
                .collect { enabled ->
                    if (enabled && locationProvider.hasLocationPermission()) {
                        locationProvider.refreshLocation()
                    } else if (!enabled) {
                        locationProvider.clearLocation()
                    }
                }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val locationEnabledFlow = preferencesRepository?.observeAppSettings()?.map { it.locationEnabled }
                ?: flowOf(true)

            combine(
                profileRepository.getProfile(),
                placesRepository.getCityDestinations(),
                placesRepository.getPlaces(),
                placesRepository.getCategories(),
                combine(locationProvider.userLocation, locationEnabledFlow) { coords, enabled ->
                    if (enabled) coords else null
                }
            ) { user, cities, places, categories, coords ->
                HomeDataSnapshot(
                    user = user,
                    cities = cities,
                    places = places,
                    categories = categories,
                    userLocation = coords
                )
            }.collect { snapshot ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        user = snapshot.user,
                        unreadNotificationsCount = 0,
                        allPlaces = snapshot.places,
                        cityDestinations = snapshot.cities,
                        categories = snapshot.categories,
                        userLocation = snapshot.userLocation
                    )
                }
            }
        }
    }

    fun onCitySelected(cityName: String?) {
        _uiState.update { current ->
            val next = if (cityName.isNullOrBlank() || current.selectedCityName.equals(cityName, ignoreCase = true)) {
                null
            } else {
                cityName
            }
            current.copy(selectedCityName = next)
        }
    }

    fun onCategorySelected(categoryName: String?) {
        _uiState.update { current ->
            val next = if (
                categoryName.isNullOrBlank() ||
                categoryName == "Semua" ||
                current.selectedCategoryName.equals(categoryName, ignoreCase = true)
            ) {
                null
            } else {
                categoryName
            }
            current.copy(selectedCategoryName = next)
        }
    }

    fun onClearFilters() {
        _uiState.update { it.copy(selectedCityName = null, selectedCategoryName = null) }
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

    private data class HomeDataSnapshot(
        val user: User?,
        val cities: List<CityDestination>,
        val places: List<Place>,
        val categories: List<CategoryItem>,
        val userLocation: UserCoordinates?
    )

    class Factory(
        private val placesRepository: PlacesRepository,
        private val profileRepository: ProfileRepository,
        private val locationProvider: LocationProvider = NoOpLocationProvider(),
        private val preferencesRepository: UserPreferencesRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                placesRepository = placesRepository,
                profileRepository = profileRepository,
                locationProvider = locationProvider,
                preferencesRepository = preferencesRepository
            ) as T
        }
    }
}
