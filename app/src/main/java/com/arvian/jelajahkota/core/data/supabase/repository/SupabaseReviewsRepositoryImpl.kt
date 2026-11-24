package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.supabase.SupabaseErrorMapper
import com.arvian.jelajahkota.core.data.supabase.dto.ReviewDto
import com.arvian.jelajahkota.core.data.supabase.dto.ReviewInsertDto
import com.arvian.jelajahkota.core.data.supabase.dto.ReviewUpdateDto
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupabaseReviewsRepositoryImpl(
    private val client: SupabaseClient,
    private val authRepository: SupabaseAuthRepositoryImpl,
    private val onReviewsMutated: (suspend () -> Unit)? = null
) : ReviewsRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val reviews = MutableStateFlow<List<Review>>(emptyList())

    init {
        scope.launch {
            refreshAllReviews()
        }
    }

    suspend fun refreshAllReviews() {
        try {
            val rows = client.from("reviews")
                .select { order("created_at", Order.DESCENDING) }
                .decodeList<ReviewDto>()
            reviews.value = rows.map { it.toDomain() }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Keep cached list when offline
        }
    }

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
        val activeUser = authRepository.currentUserOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk menulis ulasan."))
        val trimmed = review.content.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Isi ulasan tidak boleh kosong."))
        }
        val clampedRating = review.rating.coerceIn(1.0, 5.0)

        val payload = ReviewInsertDto(
            placeId = review.placeId,
            userId = activeUser.id,
            authorName = activeUser.fullName.ifBlank { "Penjelajah" },
            authorAvatarUrl = activeUser.avatarUrl,
            rating = clampedRating,
            content = trimmed
        )

        return try {
            val saved = client.from("reviews")
                .insert(payload) { select() }
                .decodeSingle<ReviewDto>()
                .toDomain(placeName = review.placeName)
            reviews.update { current -> listOf(saved) + current }
            scope.launch { onReviewsMutated?.invoke() }
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal mengirim ulasan.")))
        }
    }

    override suspend fun updateReview(
        reviewId: String,
        rating: Double,
        content: String
    ): Result<Review> {
        val userId = authRepository.currentUserIdOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu."))
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Isi ulasan tidak boleh kosong."))
        }
        val clampedRating = rating.coerceIn(1.0, 5.0)

        return try {
            val saved = client.from("reviews")
                .update(ReviewUpdateDto(rating = clampedRating, content = trimmed)) {
                    select()
                    filter {
                        eq("id", reviewId)
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<ReviewDto>()
                .toDomain()
            reviews.update { current ->
                current.map { existing ->
                    if (existing.id == reviewId) saved.copy(placeName = existing.placeName) else existing
                }
            }
            scope.launch { onReviewsMutated?.invoke() }
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal memperbarui ulasan.")))
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        val userId = authRepository.currentUserIdOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu."))
        return try {
            client.from("reviews").delete {
                filter {
                    eq("id", reviewId)
                    eq("user_id", userId)
                }
            }
            reviews.update { current -> current.filterNot { it.id == reviewId } }
            scope.launch { onReviewsMutated?.invoke() }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal menghapus ulasan.")))
        }
    }
}
