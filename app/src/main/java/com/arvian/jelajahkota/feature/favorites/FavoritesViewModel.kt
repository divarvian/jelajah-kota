package com.arvian.jelajahkota.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoritePlaces: List<Place> = emptyList(),
    val searchQuery: String = "",
    val isLoggedIn: Boolean = true,
    val isLoading: Boolean = false
) {
    val filteredPlaces: List<Place>
        get() = if (searchQuery.isBlank()) {
            favoritePlaces
        } else {
            favoritePlaces.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
            }
        }
}

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFavorite(placeId: String) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(placeId)
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val authFlow = authRepository?.getCurrentUser()
            if (authFlow != null) {
                combine(
                    favoritesRepository.getFavoritePlaces(),
                    authFlow
                ) { list, user ->
                    Pair(if (user != null) list else emptyList(), user != null)
                }.collect { (list, loggedIn) ->
                    _uiState.update {
                        it.copy(favoritePlaces = list, isLoggedIn = loggedIn, isLoading = false)
                    }
                }
            } else {
                favoritesRepository.getFavoritePlaces().collect { list ->
                    _uiState.update {
                        it.copy(favoritePlaces = list, isLoggedIn = true, isLoading = false)
                    }
                }
            }
        }
    }

    class Factory(
        private val favoritesRepository: FavoritesRepository,
        private val authRepository: AuthRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(favoritesRepository, authRepository) as T
        }
    }
}
