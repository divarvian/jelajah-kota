package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class ReviewsRepositoryImpl : ReviewsRepository {

    private val reviews = MutableStateFlow<List<Review>>(
        listOf(
            Review(
                id = "rev_1",
                placeId = "place_malioboro",
                authorName = "Budi Santoso",
                authorAvatarResId = DesignR.drawable.img_user_avatar,
                rating = 5.0,
                date = "2 hari lalu",
                content = "Tempatnya sangat bersih, spot fotonya instagramable banget! Warga sekitar juga ramah-ramah dalam menyambut pengunjung.",
                photoResIds = listOf(DesignR.drawable.img_place_malioboro)
            ),
            Review(
                id = "rev_2",
                placeId = "place_malioboro",
                authorName = "Siti Rahmawati",
                authorAvatarResId = DesignR.drawable.img_user_avatar,
                rating = 4.8,
                date = "1 minggu lalu",
                content = "Suasana heritage yang sangat kental. Paling bagus dikunjungi sore hari menjelang senja saat lampu-lampu jalan mulai menyala.",
                photoResIds = emptyList()
            ),
            Review(
                id = "rev_3",
                placeId = "place_malioboro",
                authorName = "Dimas Anggara",
                authorAvatarResId = DesignR.drawable.img_user_avatar,
                rating = 5.0,
                date = "2 minggu lalu",
                content = "Akses lokasi sangat mudah dijangkau dari stasiun kereta Kota Malang. Banyak pilihan kuliner khas di sekitarnya.",
                photoResIds = listOf(DesignR.drawable.img_place_kota_lama)
            )
        )
    )

    private val defaultMetrics = ReviewMetrics(
        overallRating = 4.82,
        totalReviews = 32,
        accuracy = 4.9,
        location = 4.8,
        cleanliness = 4.7,
        communication = 5.0,
        checkIn = 4.9,
        value = 4.8
    )

    override fun getReviewsForPlace(placeId: String): Flow<List<Review>> = reviews.asStateFlow()

    override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> = flow {
        emit(defaultMetrics)
    }

    override fun submitReview(review: Review): Flow<Result<Boolean>> = flow {
        val current = reviews.value.toMutableList()
        current.add(0, review)
        reviews.value = current
        emit(Result.success(true))
    }
}
