package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import kotlinx.coroutines.flow.Flow

interface ReviewsRepository {
    fun getReviewsForPlace(placeId: String): Flow<List<Review>>
    fun getAllReviews(): Flow<List<Review>>
    fun getReviewsByUser(userId: String): Flow<List<Review>>
    fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics>
    suspend fun submitReview(review: Review): Result<Review>
    suspend fun updateReview(reviewId: String, rating: Double, content: String): Result<Review>
    suspend fun deleteReview(reviewId: String): Result<Unit>
}
