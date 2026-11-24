package com.arvian.jelajahkota.feature.reviews

import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ProfileRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ReviewsRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.UserSessionStore
import com.arvian.jelajahkota.core.domain.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyReviewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sessionStore: UserSessionStore
    private lateinit var reviewsRepo: ReviewsRepositoryImpl
    private lateinit var viewModel: MyReviewsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionStore = UserSessionStore(initialUser = UserSessionStore.DEFAULT_USER)
        reviewsRepo = ReviewsRepositoryImpl()
        viewModel = MyReviewsViewModel(
            reviewsRepository = reviewsRepo,
            profileRepository = ProfileRepositoryImpl(sessionStore),
            placesRepository = PlacesRepositoryImpl()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filters only signed-in user reviews and supports update and delete`() = runTest {
        reviewsRepo.submitReview(
            Review(
                id = "my_rev_1",
                placeId = "place_kayutangan",
                userId = UserSessionStore.DEFAULT_USER.id,
                authorName = UserSessionStore.DEFAULT_USER.fullName,
                rating = 5.0,
                date = "Baru saja",
                content = "Heritage Malang favoritku!"
            )
        )
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.reviews.size)
        assertEquals("Kayutangan Heritage", viewModel.uiState.value.reviews.first().placeName)

        viewModel.updateReview("my_rev_1", 4, "Sore hari paling nyaman.")
        advanceUntilIdle()
        assertEquals("Sore hari paling nyaman.", viewModel.uiState.value.reviews.first().content)

        viewModel.deleteReview("my_rev_1")
        advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.reviews.size)
    }
}
