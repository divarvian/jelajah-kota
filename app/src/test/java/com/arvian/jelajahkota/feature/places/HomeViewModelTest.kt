package com.arvian.jelajahkota.feature.places

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePlacesRepository: FakeHomePlacesRepository
    private lateinit var fakeProfileRepository: FakeHomeProfileRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePlacesRepository = FakeHomePlacesRepository()
        fakeProfileRepository = FakeHomeProfileRepository()
        viewModel = HomeViewModel(fakePlacesRepository, fakeProfileRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsCuratedDataAndSyncedUserProfile() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Arvian Diva", state.user?.fullName)
        assertEquals(0, state.unreadNotificationsCount)
        assertEquals(2, state.cityDestinations.size)
        assertEquals(2, state.popularPlaces.size)
        assertEquals(2, state.categories.size)
    }

    @Test
    fun guestMode_exposesNullUserWhileKeepingDiscoveryFeedAvailable() = runTest {
        fakeProfileRepository.setGuest()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.user)
        assertEquals(2, state.popularPlaces.size)
    }

    @Test
    fun inHomeCityAndCategoryFilters_updatePopularAndRecommendedLive() = runTest {
        advanceUntilIdle()

        viewModel.onCitySelected("Malang")
        assertEquals("Malang", viewModel.uiState.value.selectedCityName)
        assertEquals(listOf("bromo"), viewModel.uiState.value.popularPlaces.map { it.id })

        // Tapping same city again toggles off cleanly
        viewModel.onCitySelected("Malang")
        assertNull(viewModel.uiState.value.selectedCityName)
        assertEquals(2, viewModel.uiState.value.popularPlaces.size)

        viewModel.onCategorySelected("Wisata Kota")
        assertEquals(listOf("malioboro"), viewModel.uiState.value.recommendedPlaces.map { it.id })

        viewModel.onClearFilters()
        assertEquals(2, viewModel.uiState.value.filteredPlaces.size)
    }

    private class FakeHomePlacesRepository : PlacesRepository {
        val places = listOf(
            Place(
                id = "malioboro",
                name = "Malioboro",
                location = "DIY",
                city = "Yogyakarta",
                rating = 4.6,
                reviewCount = 100,
                imageResId = 3,
                ticketPrice = "Gratis",
                ticketPriceAmount = 0,
                isPopular = true,
                category = "Wisata Kota"
            ),
            Place(
                id = "bromo",
                name = "Gunung Bromo",
                location = "Malang",
                city = "Malang",
                rating = 4.9,
                reviewCount = 500,
                imageResId = 4,
                ticketPrice = "Rp 29.000",
                ticketPriceAmount = 29_000,
                isPopular = true,
                category = "Pegunungan"
            )
        )

        override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(
            listOf(
                CityDestination("jogja", "Yogyakarta", 1),
                CityDestination("malang", "Malang", 2)
            )
        )

        override fun getPopularPlaces(): Flow<List<Place>> = flowOf(places.filter { it.isPopular })
        override fun getCategories(): Flow<List<CategoryItem>> = flowOf(
            listOf(
                CategoryItem("cat_city", "Wisata Kota", 4),
                CategoryItem("cat_mountain", "Pegunungan", 5)
            )
        )

        override fun getPlaces(): Flow<List<Place>> = flowOf(places)
        override fun getPlaceById(id: String): Flow<Place?> = flowOf(places.find { it.id == id })
        override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(places)
    }

    private class FakeHomeProfileRepository : ProfileRepository {
        private val user = MutableStateFlow<User?>(
            User(id = "u1", fullName = "Arvian Diva", email = "arvian@jelajahkota.id")
        )

        fun setGuest() {
            user.value = null
        }

        override fun getProfile(): Flow<User?> = user
        override suspend fun updateProfile(
            fullName: String,
            phone: String,
            city: String,
            bio: String,
            birthDate: String,
            gender: String
        ): Result<User> = Result.success(user.value!!)

        override suspend fun updateAvatar(avatarUri: String): Result<User> = Result.success(user.value!!)
    }
}
