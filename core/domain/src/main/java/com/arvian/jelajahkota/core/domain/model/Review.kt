package com.arvian.jelajahkota.core.domain.model

data class Review(
    val id: String,
    val placeId: String,
    val authorName: String,
    val authorAvatarResId: Int = 0,
    val rating: Double,
    val date: String,
    val content: String,
    val photoResIds: List<Int> = emptyList()
)

data class ReviewMetrics(
    val overallRating: Double,
    val totalReviews: Int,
    val accuracy: Double,
    val location: Double,
    val cleanliness: Double,
    val communication: Double,
    val checkIn: Double,
    val value: Double
)
