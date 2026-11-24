package com.arvian.jelajahkota.feature.reviews

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class MyReviewsUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val actionMessage: String? = null
)

class MyReviewsViewModel(
    private val reviewsRepository: ReviewsRepository,
    private val profileRepository: ProfileRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsUiState(isLoading = true))
    val uiState: StateFlow<MyReviewsUiState> = _uiState.asStateFlow()

    init {
        observeMyReviews()
    }

    private fun observeMyReviews() {
        viewModelScope.launch {
            combine(
                profileRepository.getProfile(),
                reviewsRepository.getAllReviews(),
                placesRepository.getPlaces()
            ) { user, allReviews, places ->
                if (user == null) {
                    emptyList()
                } else {
                    val placeNamesById = places.associate { it.id to it.name }
                    allReviews
                        .filter { it.userId == user.id }
                        .map { review ->
                            if (review.placeName.isBlank()) {
                                review.copy(placeName = placeNamesById[review.placeId] ?: "Destinasi Wisata")
                            } else {
                                review
                            }
                        }
                }
            }.collect { myReviews ->
                _uiState.update { it.copy(reviews = myReviews, isLoading = false) }
            }
        }
    }

    fun updateReview(reviewId: String, rating: Int, content: String) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Isi ulasan tidak boleh kosong.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewsRepository.updateReview(
                reviewId = reviewId,
                rating = rating.coerceIn(1, 5).toDouble(),
                content = trimmed
            )
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isSubmitting = false, actionMessage = "Ulasan berhasil diperbarui.")
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = error.message ?: "Gagal memperbarui ulasan.")
                    }
                }
            )
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewsRepository.deleteReview(reviewId)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isSubmitting = false, actionMessage = "Ulasan berhasil dihapus.")
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = error.message ?: "Gagal menghapus ulasan.")
                    }
                }
            )
        }
    }

    fun consumeActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    class Factory(
        private val reviewsRepository: ReviewsRepository,
        private val profileRepository: ProfileRepository,
        private val placesRepository: PlacesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MyReviewsViewModel(reviewsRepository, profileRepository, placesRepository) as T
        }
    }
}
