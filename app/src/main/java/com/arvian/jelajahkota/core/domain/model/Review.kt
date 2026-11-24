package com.arvian.jelajahkota.core.domain.model

import kotlin.math.roundToInt

data class Review(
    val id: String,
    val placeId: String,
    val userId: String = "",
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val authorAvatarResId: Int = 0,
    val rating: Double,
    val date: String,
    val content: String,
    val photoResIds: List<Int> = emptyList(),
    val placeName: String = ""
)

/**
 * Honest review aggregates computed directly from real reviews in the database:
 * overall mean rating, total count, and exact 5-star to 1-star distribution.
 */
data class ReviewMetrics(
    val overallRating: Double = 0.0,
    val totalReviews: Int = 0,
    val starCounts: Map<Int, Int> = emptyMap()
) {
    fun countForStar(star: Int): Int = starCounts[star] ?: 0

    fun fractionForStar(star: Int): Float =
        if (totalReviews <= 0) 0f else (countForStar(star).toFloat() / totalReviews.toFloat()).coerceIn(0f, 1f)

    companion object {
        fun fromReviews(reviews: List<Review>): ReviewMetrics {
            if (reviews.isEmpty()) return ReviewMetrics()
            val avg = reviews.map { it.rating }.average()
            val counts = (1..5).associateWith { star ->
                reviews.count { it.rating.roundToInt().coerceIn(1, 5) == star }
            }
            return ReviewMetrics(
                overallRating = (avg * 100.0).roundToInt() / 100.0,
                totalReviews = reviews.size,
                starCounts = counts
            )
        }
    }
}
