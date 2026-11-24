package com.arvian.jelajahkota.feature.reviews

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ReviewsUiState(
    val placeId: String = "",
    val searchQuery: String = "",
    val reviews: List<Review> = emptyList(),
    val metrics: ReviewMetrics = ReviewMetrics(
        overallRating = 0.0,
        totalReviews = 0,
        accuracy = 0.0,
        location = 0.0,
        cleanliness = 0.0,
        communication = 0.0,
        checkIn = 0.0,
        value = 0.0
    ),
    val currentUser: User? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val submitSuccess: Boolean = false
) {
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
    placeId: String
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
        val clampedRating = rating.coerceIn(1, 5).toDouble()
        val state = _uiState.value
        val author = state.currentUser

        val newReview = Review(
            id = "rev_${System.currentTimeMillis()}",
            placeId = state.placeId,
            authorName = author?.fullName?.ifBlank { "Penjelajah" } ?: "Penjelajah",
            authorAvatarResId = author?.avatarResId ?: 0,
            rating = clampedRating,
            date = "Baru saja",
            content = trimmedContent
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = reviewsRepository.submitReview(newReview)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) }
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

    fun consumeSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadReviews(placeId: String) {
        viewModelScope.launch {
            combine(
                reviewsRepository.getReviewsForPlace(placeId),
                reviewsRepository.getReviewMetricsForPlace(placeId),
                profileRepository.getProfile()
            ) { list, metrics, user ->
                Triple(list, metrics, user)
            }.collect { (list, metrics, user) ->
                _uiState.update { current ->
                    current.copy(
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
        private val placeId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(reviewsRepository, profileRepository, placeId) as T
        }
    }
}
