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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: TestFakePlacesRepository
    private lateinit var viewModel: ExploreViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = TestFakePlacesRepository()
        viewModel = ExploreViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsPlaces_onInit_updatesAllPlaces() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.allPlaces.size)
        assertEquals(2, state.filteredPlaces.size)
    }

    @Test
    fun onSearchQueryChanged_filtersPlaces() = runTest {
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Bromo")
        val state = viewModel.uiState.value
        assertEquals("Bromo", state.searchQuery)
        assertEquals(1, state.filteredPlaces.size)
        assertEquals("bromo", state.filteredPlaces.first().id)
    }

    @Test
    fun onCitySelected_filtersPlacesByCity() = runTest {
        advanceUntilIdle()

        viewModel.onCitySelected("Malang")
        val state = viewModel.uiState.value
        assertEquals("Malang", state.selectedCity)
        assertEquals(1, state.filteredPlaces.size)
        assertEquals("Malang", state.filteredPlaces.first().city)
    }

    @Test
    fun onSortOptionSelected_sortsProperly() = runTest {
        advanceUntilIdle()

        viewModel.onSortOptionSelected(ExploreSortOption.RATING_DESC)
        val state = viewModel.uiState.value
        assertEquals(ExploreSortOption.RATING_DESC, state.selectedSortOption)
        // Bromo rating is 4.9, Malioboro is 4.6
        assertEquals("bromo", state.filteredPlaces.first().id)
    }

    @Test
    fun onToggleView_updatesGridView() = runTest {
        assertTrue(viewModel.uiState.value.isGridView)

        viewModel.onToggleView(false)
        assertFalse(viewModel.uiState.value.isGridView)
    }

    @Test
    fun onResetFilters_clearsAllFilters() = runTest {
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Bromo")
        viewModel.onCitySelected("Malang")
        viewModel.onCategorySelected("Mountain")

        viewModel.onResetFilters()
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.selectedCity)
        assertNull(state.selectedCategory)
        assertEquals(2, state.filteredPlaces.size)
    }
}

private class TestFakePlacesRepository : PlacesRepository {
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
            category = "City",
            ticketPrice = "Gratis"
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
            category = "Mountain",
            ticketPrice = "Rp 29.000"
        )
    )

    override fun getUserProfile(): Flow<UserProfile> = flowOf(
        UserProfile("u1", "Arvian", 123, 5)
    )

    override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(emptyList())

    override fun getPopularPlaces(): Flow<List<Place>> = flowOf(places)

    override fun getPlaces(): Flow<List<Place>> = flowOf(places)

    override fun getPlaceById(id: String): Flow<Place?> = flowOf(places.find { it.id == id })

    override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(places)

    override fun getCategories(): Flow<List<CategoryItem>> = flowOf(emptyList())
}
