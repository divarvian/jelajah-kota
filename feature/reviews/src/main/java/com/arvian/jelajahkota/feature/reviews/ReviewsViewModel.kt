package com.arvian.jelajahkota.feature.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewsUiState(
    val placeId: String = "place_malioboro",
    val searchQuery: String = "",
    val reviews: List<Review> = emptyList(),
    val metrics: ReviewMetrics = ReviewMetrics(
        overallRating = 4.82,
        totalReviews = 32,
        accuracy = 4.7,
        location = 4.5,
        cleanliness = 4.7,
        communication = 4.6,
        checkIn = 4.9,
        value = 4.7
    ),
    val isLoading: Boolean = false
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
    placeId: String = "place_malioboro"
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState(placeId = placeId))
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    init {
        loadReviews()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            reviewsRepository.getReviewsForPlace(_uiState.value.placeId).collect { list ->
                _uiState.update { it.copy(reviews = list, isLoading = false) }
            }
        }
    }

    class Factory(
        private val reviewsRepository: ReviewsRepository,
        private val placeId: String = "place_malioboro"
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(reviewsRepository, placeId) as T
        }
    }
}
