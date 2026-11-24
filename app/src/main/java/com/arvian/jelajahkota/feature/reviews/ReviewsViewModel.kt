package com.arvian.jelajahkota.feature.reviews

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ReviewsUiState(
    val placeId: String = "",
    val placeName: String = "",
    val searchQuery: String = "",
    val reviews: List<Review> = emptyList(),
    val metrics: ReviewMetrics = ReviewMetrics(),
    val currentUser: User? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val actionMessage: String? = null,
    val submitSuccess: Boolean = false
) {
    val isLoggedIn: Boolean get() = currentUser != null

    val filteredReviews: List<Review>
        get() = if (searchQuery.isBlank()) {
            reviews
        } else {
            reviews.filter {
                it.content.contains(searchQuery, ignoreCase = true) ||
                    it.authorName.contains(searchQuery, ignoreCase = true)
            }
        }
}

class ReviewsViewModel(
    private val reviewsRepository: ReviewsRepository,
    private val profileRepository: ProfileRepository,
    private val placeId: String,
    private val placesRepository: PlacesRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState(placeId = placeId, isLoading = true))
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    init {
        loadReviews(placeId)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun submitReview(rating: Int, content: String) {
        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Isi ulasan tidak boleh kosong.") }
            return
        }
        val state = _uiState.value
        val author = state.currentUser
        if (author == null) {
            _uiState.update { it.copy(errorMessage = "Silakan masuk terlebih dahulu untuk menulis ulasan.") }
            return
        }
        val clampedRating = rating.coerceIn(1, 5).toDouble()

        val newReview = Review(
            id = "rev_${System.currentTimeMillis()}",
            placeId = state.placeId,
            userId = author.id,
            authorName = author.fullName.ifBlank { "Penjelajah" },
            authorAvatarUrl = author.avatarUrl,
            authorAvatarResId = author.avatarResId,
            rating = clampedRating,
            date = "Baru saja",
            content = trimmedContent,
            placeName = state.placeName
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewsRepository.submitReview(newReview)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitSuccess = true,
                            actionMessage = "Terima kasih! Ulasan Anda telah dipublikasikan."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Gagal mengirim ulasan."
                        )
                    }
                }
            )
        }
    }

    fun updateReview(reviewId: String, rating: Int, content: String) {
        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Isi ulasan tidak boleh kosong.") }
            return
        }
        val clampedRating = rating.coerceIn(1, 5).toDouble()

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewsRepository.updateReview(reviewId, clampedRating, trimmedContent)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            actionMessage = "Ulasan berhasil diperbarui."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Gagal memperbarui ulasan."
                        )
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
                        it.copy(
                            isSubmitting = false,
                            actionMessage = "Ulasan berhasil dihapus."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Gagal menghapus ulasan."
                        )
                    }
                }
            )
        }
    }

    fun consumeSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = false) }
    }

    fun consumeActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadReviews(placeId: String) {
        viewModelScope.launch {
            val resolvedPlaceName = placesRepository?.getPlaceById(placeId)?.firstOrNull()?.name.orEmpty()
            combine(
                reviewsRepository.getReviewsForPlace(placeId),
                reviewsRepository.getReviewMetricsForPlace(placeId),
                profileRepository.getProfile()
            ) { list, metrics, user ->
                Triple(list, metrics, user)
            }.collect { (list, metrics, user) ->
                _uiState.update { current ->
                    current.copy(
                        placeName = resolvedPlaceName.ifBlank { current.placeName },
                        reviews = list,
                        metrics = metrics,
                        currentUser = user,
                        isLoading = false
                    )
                }
            }
        }
    }

    class Factory(
        private val reviewsRepository: ReviewsRepository,
        private val profileRepository: ProfileRepository,
        private val placeId: String,
        private val placesRepository: PlacesRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(reviewsRepository, profileRepository, placeId, placesRepository) as T
        }
    }
}
