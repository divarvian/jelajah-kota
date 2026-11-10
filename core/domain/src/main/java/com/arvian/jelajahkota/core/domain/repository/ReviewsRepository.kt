package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import kotlinx.coroutines.flow.Flow

interface ReviewsRepository {
    fun getReviewsForPlace(placeId: String): Flow<List<Review>>
    fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics>
    fun submitReview(review: Review): Flow<Result<Boolean>>
}
