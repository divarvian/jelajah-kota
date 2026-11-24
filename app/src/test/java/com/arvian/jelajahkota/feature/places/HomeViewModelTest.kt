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
    fun initialState_loadsDataAndSyncedUserProfile() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Arvian Diva", state.user?.fullName)
        assertEquals(2, state.cityDestinations.size)
        assertEquals(2, state.popularPlaces.size)
        assertEquals(2, state.categories.size)
    }

    @Test
    fun onSearchQueryChanged_updatesQueryAndFilteredPlaces() = runTest {
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Bromo")
        assertEquals("Bromo", viewModel.uiState.value.searchQuery)
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("bromo", viewModel.uiState.value.filteredPlaces.first().id)
    }

    @Test
    fun onCitySelected_togglesSelectionAndFilters() = runTest {
        advanceUntilIdle()

        viewModel.onCitySelected("malang")
        assertEquals("malang", viewModel.uiState.value.selectedCity)
        assertEquals("Malang", viewModel.uiState.value.selectedCityName)
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("Malang", viewModel.uiState.value.filteredPlaces.first().city)

        viewModel.onCitySelected("malang")
        assertNull(viewModel.uiState.value.selectedCity)
    }

    @Test
    fun onCategorySelected_togglesSelectionAndFilters() = runTest {
        advanceUntilIdle()

        viewModel.onCategorySelected("cat_mountain")
        assertEquals("cat_mountain", viewModel.uiState.value.selectedCategory)
        assertEquals("Pegunungan", viewModel.uiState.value.selectedCategoryName)
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("bromo", viewModel.uiState.value.filteredPlaces.first().id)

        viewModel.onCategorySelected("cat_mountain")
        assertNull(viewModel.uiState.value.selectedCategory)
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
        private val user = MutableStateFlow(
            User(id = "u1", fullName = "Arvian Diva", email = "arvian@jelajahkota.id")
        )

        override fun getProfile(): Flow<User> = user
        override suspend fun updateProfile(
            fullName: String,
            phone: String,
            city: String,
            bio: String,
            birthDate: String,
            gender: String
        ): Result<User> = Result.success(user.value)

        override suspend fun updateAvatar(avatarUri: String): Result<User> = Result.success(user.value)
    }
}
