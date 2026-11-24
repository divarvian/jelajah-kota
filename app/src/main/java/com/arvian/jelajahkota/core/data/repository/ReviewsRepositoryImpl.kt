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

class ReviewsRepositoryImpl : ReviewsRepository {

    private val reviews = MutableStateFlow(
        listOf(
            Review(
                id = "rev_1",
                placeId = "place_jodipan",
                userId = "user_budi",
                authorName = "Budi Santoso",
                rating = 5.0,
                date = "2 hari lalu",
                content = "Tempatnya sangat bersih, spot fotonya instagramable banget! Warga sekitar juga ramah-ramah dalam menyambut pengunjung.",
                photoResIds = listOf(R.drawable.img_destination_jodipan),
                placeName = "Kampung Warna-Warni Jodipan"
            ),
            Review(
                id = "rev_2",
                placeId = "place_jodipan",
                userId = "user_dimas",
                authorName = "Dimas Anggara",
                rating = 4.6,
                date = "2 minggu lalu",
                content = "Akses lokasi sangat mudah dijangkau dari stasiun kereta Kota Malang. Jembatan kacanya seru banget buat foto.",
                photoResIds = listOf(R.drawable.img_destination_photo),
                placeName = "Kampung Warna-Warni Jodipan"
            ),
            Review(
                id = "rev_3",
                placeId = "place_kayutangan",
                userId = "user_siti",
                authorName = "Siti Rahmawati",
                rating = 4.8,
                date = "1 minggu lalu",
                content = "Suasana heritage yang sangat kental. Paling bagus dikunjungi sore hari menjelang senja saat lampu-lampu jalan mulai menyala.",
                photoResIds = listOf(R.drawable.img_destination_kayutangan),
                placeName = "Kayutangan Heritage"
            ),
            Review(
                id = "rev_4",
                placeId = "place_bromo",
                userId = "user_nadia",
                authorName = "Nadia Putri",
                rating = 5.0,
                date = "3 hari lalu",
                content = "Sunrise dari Bukit Penanjakan benar-benar luar biasa! Pastikan bawa jaket tebal karena suhu pagi sangat dingin.",
                photoResIds = listOf(R.drawable.img_destination_bromo),
                placeName = "Gunung Bromo & Lautan Pasir"
            ),
            Review(
                id = "rev_5",
                placeId = "place_malioboro",
                userId = "user_raka",
                authorName = "Raka Pratama",
                rating = 4.9,
                date = "5 hari lalu",
                content = "Jalur pedestrian sekarang sangat rapi dan nyaman untuk jalan kaki malam hari sambil kulineran gudeg & angkringan.",
                photoResIds = listOf(R.drawable.img_place_malioboro),
                placeName = "Malioboro"
            ),
            Review(
                id = "rev_6",
                placeId = "place_kota_lama",
                userId = "user_ayu",
                authorName = "Ayu Lestari",
                rating = 4.7,
                date = "1 minggu lalu",
                content = "Arsitektur kolonialnya terawat dengan sangat apik. Banyak kafe estetik dan toko barang antik yang menarik.",
                photoResIds = listOf(R.drawable.img_place_kota_lama),
                placeName = "Kota Lama Semarang"
            ),
            Review(
                id = "rev_7",
                placeId = "place_alun_tugu",
                userId = "user_fajar",
                authorName = "Fajar Nugroho",
                rating = 4.6,
                date = "4 hari lalu",
                content = "Taman kota yang sangat asri dan sejuk untuk jogging pagi. Kolam teratai di sekeliling tugu terlihat indah.",
                photoResIds = emptyList(),
                placeName = "Alun-Alun Tugu Malang"
            ),
            Review(
                id = "rev_8",
                placeId = "place_waterfall",
                userId = "user_intan",
                authorName = "Intan Permata",
                rating = 4.7,
                date = "6 hari lalu",
                content = "Air terjunnya segar dan trek menuju lokasi sangat aman untuk keluarga. Taman labirinnya juga seru dicoba!",
                photoResIds = listOf(R.drawable.img_destination_waterfall),
                placeName = "Coban Rondo & Taman Labirin"
            )
        )
    )

    override fun getReviewsForPlace(placeId: String): Flow<List<Review>> =
        reviews.map { list -> list.filter { it.placeId == placeId } }

    override fun getAllReviews(): Flow<List<Review>> = reviews.asStateFlow()

    override fun getReviewsByUser(userId: String): Flow<List<Review>> =
        reviews.map { list ->
            if (userId.isBlank()) emptyList() else list.filter { it.userId == userId }
        }

    override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> =
        getReviewsForPlace(placeId).map { placeReviews ->
            ReviewMetrics.fromReviews(placeReviews)
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

    override suspend fun updateReview(
        reviewId: String,
        rating: Double,
        content: String
    ): Result<Review> {
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Isi ulasan tidak boleh kosong."))
        }
        if (rating !in 1.0..5.0) {
            return Result.failure(IllegalArgumentException("Rating harus antara 1 sampai 5."))
        }
        var updatedReview: Review? = null
        reviews.update { current ->
            current.map { existing ->
                if (existing.id == reviewId) {
                    existing.copy(
                        rating = rating,
                        content = trimmed,
                        date = "Baru diperbarui"
                    ).also { updatedReview = it }
                } else {
                    existing
                }
            }
        }
        return updatedReview?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Ulasan tidak ditemukan."))
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        val existed = reviews.value.any { it.id == reviewId }
        if (!existed) {
            return Result.failure(NoSuchElementException("Ulasan tidak ditemukan."))
        }
        reviews.update { current -> current.filterNot { it.id == reviewId } }
        return Result.success(Unit)
    }
}
