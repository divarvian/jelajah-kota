package com.arvian.jelajahkota.feature.places

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class PlaceDetailUiState(
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val latestReview: Review? = null,
    val reviewsCount: Int = 0,
    val isDescriptionExpanded: Boolean = false,
    val isLoading: Boolean = true
) {
    val galleryImages: List<Int>
        get() {
            val p = place ?: return emptyList()
            return if (p.galleryImageResIds.isNotEmpty()) {
                p.galleryImageResIds.distinct()
            } else {
                listOf(p.imageResId)
            }
        }
}

class PlaceDetailViewModel(
    private val placesRepository: PlacesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val reviewsRepository: ReviewsRepository,
    private val placeId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceDetailUiState(isLoading = true))
    val uiState: StateFlow<PlaceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            combine(
                placesRepository.getPlaceById(placeId),
                favoritesRepository.isFavorite(placeId),
                reviewsRepository.getReviewsForPlace(placeId)
            ) { place, isFavorite, reviews ->
                Triple(place, isFavorite, reviews)
            }.collect { (place, isFavorite, reviews) ->
                _uiState.update { current ->
                    current.copy(
                        place = place,
                        isFavorite = isFavorite,
                        latestReview = reviews.firstOrNull(),
                        reviewsCount = if (reviews.isNotEmpty()) reviews.size else (place?.reviewCount ?: 0),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(placeId)
        }
    }

    fun toggleDescriptionExpanded() {
        _uiState.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
    }

    class Factory(
        private val placesRepository: PlacesRepository,
        private val favoritesRepository: FavoritesRepository,
        private val reviewsRepository: ReviewsRepository,
        private val placeId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaceDetailViewModel::class.java)) {
                return PlaceDetailViewModel(
                    placesRepository = placesRepository,
                    favoritesRepository = favoritesRepository,
                    reviewsRepository = reviewsRepository,
                    placeId = placeId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
