package com.arvian.jelajahkota.feature.places

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.UserProfile
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

class FakePlacesRepository : PlacesRepository {
    val places = listOf(
        Place(
            id = "malioboro",
            name = "Malioboro",
            location = "DIY",
            city = "Yogyakarta",
            rating = 4.6,
            reviewCount = 100,
            imageResId = 3,
            isPopular = true,
            category = "City"
        ),
        Place(
            id = "bromo",
            name = "Gunung Bromo",
            location = "Malang",
            city = "Malang",
            rating = 4.9,
            reviewCount = 500,
            imageResId = 4,
            isPopular = true,
            category = "Mountain"
        )
    )

    override fun getUserProfile(): Flow<UserProfile> = flowOf(
        UserProfile("u1", "AntekAntekAsing", 123, 5)
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
            CategoryItem("cat_city", "City", 4),
            CategoryItem("cat_mountain", "Mountain", 5)
        )
    )
    override fun getPlaces(): Flow<List<Place>> = flowOf(places)
    override fun getPlaceById(id: String): Flow<Place?> = flowOf(places.find { it.id == id })
    override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(places)
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakePlacesRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakePlacesRepository()
        viewModel = HomeViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsDataSuccessfully() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("AntekAntekAsing", state.userProfile?.name)
        assertEquals(5, state.userProfile?.unreadNotificationsCount)
        assertEquals(2, state.cityDestinations.size)
        assertEquals("Yogyakarta", state.cityDestinations.first().name)
        assertEquals(2, state.popularPlaces.size)
        assertEquals("Malioboro", state.popularPlaces.first().name)
        assertEquals(2, state.categories.size)
        assertEquals(HomeNavTab.BERANDA, state.selectedNavTab)
    }

    @Test
    fun onSearchQueryChanged_updatesQueryAndFilteredPlaces() = runTest {
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Bromo")
        assertEquals("Bromo", viewModel.uiState.value.searchQuery)
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("Gunung Bromo", viewModel.uiState.value.filteredPlaces.first().name)
    }

    @Test
    fun onCitySelected_togglesSelectionAndFilters() = runTest {
        advanceUntilIdle()

        viewModel.onCitySelected("malang")
        assertEquals("malang", viewModel.uiState.value.selectedCity)
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("Gunung Bromo", viewModel.uiState.value.filteredPlaces.first().name)

        viewModel.onCitySelected("malang")
        assertNull(viewModel.uiState.value.selectedCity)
        assertEquals(2, viewModel.uiState.value.filteredPlaces.size)
    }

    @Test
    fun onCategorySelected_togglesSelectionAndFilters() = runTest {
        advanceUntilIdle()

        viewModel.onCategorySelected("cat_city")
        assertEquals("cat_city", viewModel.uiState.value.selectedCategory)
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("Malioboro", viewModel.uiState.value.filteredPlaces.first().name)

        viewModel.onCategorySelected("cat_city")
        assertNull(viewModel.uiState.value.selectedCategory)
        assertEquals(2, viewModel.uiState.value.filteredPlaces.size)
    }

    @Test
    fun onNavTabSelected_updatesActiveTab() = runTest {
        advanceUntilIdle()

        viewModel.onNavTabSelected(HomeNavTab.EKSPLORASI)
        assertEquals(HomeNavTab.EKSPLORASI, viewModel.uiState.value.selectedNavTab)

        viewModel.onNavTabSelected(HomeNavTab.FAVORIT)
        assertEquals(HomeNavTab.FAVORIT, viewModel.uiState.value.selectedNavTab)
    }
}
