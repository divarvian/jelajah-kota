package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

class ReviewsRepositoryImpl : ReviewsRepository {

    private val reviews = MutableStateFlow(
        listOf(
            Review(
                id = "rev_1",
                placeId = "place_jodipan",
                authorName = "Budi Santoso",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 5.0,
                date = "2 hari lalu",
                content = "Tempatnya sangat bersih, spot fotonya instagramable banget! Warga sekitar juga ramah-ramah dalam menyambut pengunjung.",
                photoResIds = listOf(R.drawable.img_destination_jodipan)
            ),
            Review(
                id = "rev_2",
                placeId = "place_jodipan",
                authorName = "Dimas Anggara",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 4.6,
                date = "2 minggu lalu",
                content = "Akses lokasi sangat mudah dijangkau dari stasiun kereta Kota Malang. Jembatan kacanya seru banget buat foto.",
                photoResIds = listOf(R.drawable.img_destination_photo)
            ),
            Review(
                id = "rev_3",
                placeId = "place_kayutangan",
                authorName = "Siti Rahmawati",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 4.8,
                date = "1 minggu lalu",
                content = "Suasana heritage yang sangat kental. Paling bagus dikunjungi sore hari menjelang senja saat lampu-lampu jalan mulai menyala.",
                photoResIds = listOf(R.drawable.img_destination_kayutangan)
            ),
            Review(
                id = "rev_4",
                placeId = "place_bromo",
                authorName = "Nadia Putri",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 5.0,
                date = "3 hari lalu",
                content = "Sunrise dari Bukit Penanjakan benar-benar luar biasa! Pastikan bawa jaket tebal karena suhu pagi sangat dingin.",
                photoResIds = listOf(R.drawable.img_destination_bromo)
            ),
            Review(
                id = "rev_5",
                placeId = "place_malioboro",
                authorName = "Raka Pratama",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 4.9,
                date = "5 hari lalu",
                content = "Jalur pedestrian sekarang sangat rapi dan nyaman untuk jalan kaki malam hari sambil kulineran gudeg & angkringan.",
                photoResIds = listOf(R.drawable.img_place_malioboro)
            ),
            Review(
                id = "rev_6",
                placeId = "place_kota_lama",
                authorName = "Ayu Lestari",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 4.7,
                date = "1 minggu lalu",
                content = "Arsitektur kolonialnya terawat dengan sangat apik. Banyak kafe estetik dan toko barang antik yang menarik.",
                photoResIds = listOf(R.drawable.img_place_kota_lama)
            ),
            Review(
                id = "rev_7",
                placeId = "place_alun_tugu",
                authorName = "Fajar Nugroho",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 4.6,
                date = "4 hari lalu",
                content = "Taman kota yang sangat asri dan sejuk untuk jogging pagi. Kolam teratai di sekeliling tugu terlihat indah.",
                photoResIds = emptyList()
            ),
            Review(
                id = "rev_8",
                placeId = "place_waterfall",
                authorName = "Intan Permata",
                authorAvatarResId = R.drawable.img_user_avatar,
                rating = 4.7,
                date = "6 hari lalu",
                content = "Air terjunnya segar dan trek menuju lokasi sangat aman untuk keluarga. Taman labirinnya juga seru dicoba!",
                photoResIds = listOf(R.drawable.img_destination_waterfall)
            )
        )
    )

    override fun getReviewsForPlace(placeId: String): Flow<List<Review>> =
        reviews.map { list -> list.filter { it.placeId == placeId } }

    override fun getAllReviews(): Flow<List<Review>> = reviews.asStateFlow()

    override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> =
        getReviewsForPlace(placeId).map { placeReviews ->
            if (placeReviews.isEmpty()) {
                ReviewMetrics(
                    overallRating = 0.0,
                    totalReviews = 0,
                    accuracy = 0.0,
                    location = 0.0,
                    cleanliness = 0.0,
                    communication = 0.0,
                    checkIn = 0.0,
                    value = 0.0
                )
            } else {
                val avg = placeReviews.map { it.rating }.average()
                fun clamp(delta: Double): Double =
                    ((avg + delta).coerceIn(1.0, 5.0) * 10.0).roundToInt() / 10.0

                ReviewMetrics(
                    overallRating = (avg * 100.0).roundToInt() / 100.0,
                    totalReviews = placeReviews.size,
                    accuracy = clamp(0.1),
                    location = clamp(0.05),
                    cleanliness = clamp(-0.1),
                    communication = clamp(0.15),
                    checkIn = clamp(0.05),
                    value = clamp(0.0)
                )
            }
        }

    override suspend fun submitReview(review: Review): Result<Review> {
        if (review.content.isBlank()) {
            return Result.failure(IllegalArgumentException("Isi ulasan tidak boleh kosong."))
        }
        if (review.rating !in 1.0..5.0) {
            return Result.failure(IllegalArgumentException("Rating harus antara 1 sampai 5."))
        }
        reviews.update { current -> listOf(review) + current }
        return Result.success(review)
    }
}
