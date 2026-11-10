package com.arvian.jelajahkota.feature.reviews

import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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
        viewModel = ReviewsViewModel(fakeReviewsRepository, "place_malioboro")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadReviews loads reviews from repository`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.reviews.size)
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

    private class FakeReviewsRepository : ReviewsRepository {
        override fun getReviewsForPlace(placeId: String): Flow<List<Review>> = flowOf(
            listOf(
                Review(id = "1", placeId = placeId, authorName = "Budi", rating = 5.0, date = "2 hari lalu", content = "Tempatnya sangat bersih dan nyaman."),
                Review(id = "2", placeId = placeId, authorName = "Siti", rating = 4.5, date = "3 hari lalu", content = "Pemandangan bagus.")
            )
        )

        override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> = flowOf(
            ReviewMetrics(overallRating = 4.82, totalReviews = 32, accuracy = 4.7, location = 4.5, cleanliness = 4.7, communication = 4.6, checkIn = 4.9, value = 4.7)
        )

        override fun submitReview(review: Review): Flow<Result<Boolean>> = flowOf(Result.success(true))
    }
}
