package com.arvian.jelajahkota.feature.reviews

import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeReviewsRepository: FakeReviewsRepository
    private lateinit var viewModel: ReviewsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeReviewsRepository = FakeReviewsRepository()
        viewModel = ReviewsViewModel(fakeReviewsRepository, FakeProfileRepository(), "place_jodipan")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadReviews loads both reviews and metrics from repository`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.reviews.size)
        assertEquals(2, viewModel.uiState.value.metrics.totalReviews)
        assertEquals(4.75, viewModel.uiState.value.metrics.overallRating, 0.01)
    }

    @Test
    fun `searchQuery filters reviews correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("bersih")
        assertEquals(1, viewModel.uiState.value.filteredReviews.size)
        assertEquals("Budi", viewModel.uiState.value.filteredReviews.first().authorName)

        viewModel.onSearchQueryChange("nonexistent")
        assertEquals(0, viewModel.uiState.value.filteredReviews.size)
    }

    @Test
    fun `submitReview rejects blank content and adds valid review using active profile`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.submitReview(5, "   ")
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.submitSuccess)

        viewModel.submitReview(5, "Sangat direkomendasikan untuk keluarga!")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.submitSuccess)
        assertEquals(3, viewModel.uiState.value.reviews.size)
        assertEquals("Arvian Diva", viewModel.uiState.value.reviews.first().authorName)
        assertEquals(3, viewModel.uiState.value.metrics.totalReviews)
    }

    private class FakeReviewsRepository : ReviewsRepository {
        private val flow = MutableStateFlow(
            listOf(
                Review(id = "1", placeId = "place_jodipan", authorName = "Budi", rating = 5.0, date = "2 hari lalu", content = "Tempatnya sangat bersih dan nyaman."),
                Review(id = "2", placeId = "place_jodipan", authorName = "Siti", rating = 4.5, date = "3 hari lalu", content = "Pemandangan bagus.")
            )
        )

        override fun getReviewsForPlace(placeId: String): Flow<List<Review>> =
            flow.map { list -> list.filter { it.placeId == placeId } }

        override fun getAllReviews(): Flow<List<Review>> = flow

        override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> =
            getReviewsForPlace(placeId).map { list ->
                val avg = if (list.isEmpty()) 0.0 else list.map { it.rating }.average()
                ReviewMetrics(avg, list.size, avg, avg, avg, avg, avg, avg)
            }

        override suspend fun submitReview(review: Review): Result<Review> {
            flow.value = listOf(review) + flow.value
            return Result.success(review)
        }
    }

    private class FakeProfileRepository : ProfileRepository {
        private val user = User(id = "u1", fullName = "Arvian Diva", email = "arvian@jelajahkota.id")
        override fun getProfile(): Flow<User> = flowOf(user)
        override suspend fun updateProfile(
            fullName: String,
            phone: String,
            city: String,
            bio: String,
            birthDate: String,
            gender: String
        ): Result<User> = Result.success(user)

        override suspend fun updateAvatar(avatarUri: String): Result<User> = Result.success(user)
    }
}
