package com.arvian.jelajahkota.feature.places

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var favoritesRepo: FakeFavoritesRepo
    private lateinit var viewModel: PlaceDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoritesRepo = FakeFavoritesRepo()
        viewModel = PlaceDetailViewModel(
            placesRepository = FakePlacesRepo(),
            favoritesRepository = favoritesRepo,
            reviewsRepository = FakeReviewsRepo(),
            placeId = "place_jodipan"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsPlaceFavoriteStateLatestReviewAndDeduplicatedGallery() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Kampung Jodipan", state.place?.name)
        assertFalse(state.isFavorite)
        assertEquals("Budi", state.latestReview?.authorName)
        assertEquals(listOf(10, 20), state.galleryImages)
    }

    @Test
    fun toggleFavorite_persistsToFavoritesRepository() = runTest {
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isFavorite)

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    private class FakePlacesRepo : PlacesRepository {
        private val place = Place(
            id = "place_jodipan",
            name = "Kampung Jodipan",
            location = "Malang",
            city = "Malang",
            rating = 4.7,
            reviewCount = 200,
            imageResId = 10,
            galleryImageResIds = listOf(10, 10, 20)
        )

        override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(emptyList())
        override fun getPopularPlaces(): Flow<List<Place>> = flowOf(listOf(place))
        override fun getCategories(): Flow<List<CategoryItem>> = flowOf(emptyList())
        override fun getPlaces(): Flow<List<Place>> = flowOf(listOf(place))
        override fun getPlaceById(id: String): Flow<Place?> = flowOf(if (id == place.id) place else null)
        override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(listOf(place))
    }

    private class FakeFavoritesRepo : FavoritesRepository {
        private val ids = MutableStateFlow<Set<String>>(emptySet())
        override fun getFavoritePlaceIds(): Flow<Set<String>> = ids
        override fun getFavoritePlaces(): Flow<List<Place>> = flowOf(emptyList())
        override fun isFavorite(placeId: String): Flow<Boolean> = ids.map { placeId in it }
        override suspend fun toggleFavorite(placeId: String): Boolean {
            val next = if (placeId in ids.value) ids.value - placeId else ids.value + placeId
            ids.value = next
            return placeId in next
        }
    }

    private class FakeReviewsRepo : ReviewsRepository {
        private val review = Review(
            id = "r1",
            placeId = "place_jodipan",
            userId = "u1",
            authorName = "Budi",
            rating = 5.0,
            date = "Baru saja",
            content = "Sangat bagus!"
        )

        override fun getReviewsForPlace(placeId: String): Flow<List<Review>> = flowOf(listOf(review))
        override fun getAllReviews(): Flow<List<Review>> = flowOf(listOf(review))
        override fun getReviewsByUser(userId: String): Flow<List<Review>> = flowOf(listOf(review))
        override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> =
            flowOf(ReviewMetrics.fromReviews(listOf(review)))

        override suspend fun submitReview(review: Review): Result<Review> = Result.success(review)
        override suspend fun updateReview(reviewId: String, rating: Double, content: String): Result<Review> =
            Result.success(review.copy(rating = rating, content = content))

        override suspend fun deleteReview(reviewId: String): Result<Unit> = Result.success(Unit)
    }
}
