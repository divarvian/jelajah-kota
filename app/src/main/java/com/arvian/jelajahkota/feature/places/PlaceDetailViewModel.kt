package com.arvian.jelajahkota.feature.places

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.common.location.LocationProvider
import com.arvian.jelajahkota.core.common.location.NoOpLocationProvider
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Immutable
data class PlaceGalleryMedia(
    val key: String,
    val imageUrl: String? = null,
    val drawableResId: Int = 0
)

@Immutable
data class PlaceDetailUiState(
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val isLoggedIn: Boolean = false,
    val latestReview: Review? = null,
    val reviewsCount: Int = 0,
    val effectiveRating: Double = 0.0,
    val userLocation: UserCoordinates? = null,
    val isDescriptionExpanded: Boolean = false,
    val isLoading: Boolean = true
) {
    val galleryImages: List<Int>
        get() {
            val p = place ?: return emptyList()
            return if (p.galleryImageResIds.isNotEmpty()) {
                p.galleryImageResIds.distinct()
            } else if (p.imageResId != 0) {
                listOf(p.imageResId)
            } else {
                emptyList()
            }
        }

    val galleryMedia: List<PlaceGalleryMedia>
        get() {
            val p = place ?: return emptyList()
            val remoteUrls = p.galleryImageUrls.filter { it.isNotBlank() }.distinct()
            if (remoteUrls.isNotEmpty()) {
                return remoteUrls.mapIndexed { idx, url ->
                    PlaceGalleryMedia(key = "url_${idx}_$url", imageUrl = url, drawableResId = p.imageResId)
                }
            }
            return galleryImages.map { resId ->
                PlaceGalleryMedia(key = "res_$resId", imageUrl = null, drawableResId = resId)
            }
        }

    val distanceLabel: String?
        get() {
            val p = place ?: return null
            val loc = userLocation ?: return null
            return p.distanceKmFrom(loc.latitude, loc.longitude)?.let(Place::formatDistanceLabel)
        }
}

class PlaceDetailViewModel(
    private val placesRepository: PlacesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val reviewsRepository: ReviewsRepository,
    private val authRepository: AuthRepository? = null,
    private val placeId: String,
    private val locationProvider: LocationProvider = NoOpLocationProvider()
) : ViewModel() {

    private val initialPlace = placesRepository.getCachedPlaceById(placeId)

    private val _uiState = MutableStateFlow(
        PlaceDetailUiState(
            place = initialPlace,
            reviewsCount = initialPlace?.reviewCount ?: 0,
            effectiveRating = initialPlace?.rating ?: 0.0,
            userLocation = locationProvider.userLocation.value,
            isLoading = initialPlace == null
        )
    )
    val uiState: StateFlow<PlaceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            val authFlow = authRepository?.getCurrentUser()
            if (authFlow != null) {
                combine(
                    placesRepository.getPlaceById(placeId),
                    favoritesRepository.isFavorite(placeId),
                    reviewsRepository.getReviewsForPlace(placeId),
                    authFlow,
                    locationProvider.userLocation
                ) { place, isFavorite, reviews, user, coords ->
                    DetailSnapshot(place, isFavorite, reviews, isLoggedIn = user != null, userLocation = coords)
                }.collect { snapshot ->
                    applySnapshot(snapshot)
                }
            } else {
                combine(
                    placesRepository.getPlaceById(placeId),
                    favoritesRepository.isFavorite(placeId),
                    reviewsRepository.getReviewsForPlace(placeId),
                    locationProvider.userLocation
                ) { place, isFavorite, reviews, coords ->
                    DetailSnapshot(place, isFavorite, reviews, isLoggedIn = true, userLocation = coords)
                }.collect { snapshot ->
                    applySnapshot(snapshot)
                }
            }
        }
    }

    private fun applySnapshot(snapshot: DetailSnapshot) {
        val liveCount = if (snapshot.reviews.isNotEmpty()) {
            snapshot.reviews.size
        } else {
            snapshot.place?.reviewCount ?: 0
        }
        val liveRating = if (snapshot.reviews.isNotEmpty()) {
            (snapshot.reviews.map { it.rating }.average() * 10.0).roundToInt() / 10.0
        } else {
            snapshot.place?.rating ?: 0.0
        }

        _uiState.update { current ->
            current.copy(
                place = snapshot.place,
                isFavorite = snapshot.isFavorite,
                isLoggedIn = snapshot.isLoggedIn,
                latestReview = snapshot.reviews.firstOrNull(),
                reviewsCount = liveCount,
                effectiveRating = liveRating,
                userLocation = snapshot.userLocation,
                isLoading = false
            )
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

    private data class DetailSnapshot(
        val place: Place?,
        val isFavorite: Boolean,
        val reviews: List<Review>,
        val isLoggedIn: Boolean,
        val userLocation: UserCoordinates?
    )

    class Factory(
        private val placesRepository: PlacesRepository,
        private val favoritesRepository: FavoritesRepository,
        private val reviewsRepository: ReviewsRepository,
        private val authRepository: AuthRepository? = null,
        private val placeId: String,
        private val locationProvider: LocationProvider = NoOpLocationProvider()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaceDetailViewModel::class.java)) {
                return PlaceDetailViewModel(
                    placesRepository = placesRepository,
                    favoritesRepository = favoritesRepository,
                    reviewsRepository = reviewsRepository,
                    authRepository = authRepository,
                    placeId = placeId,
                    locationProvider = locationProvider
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
