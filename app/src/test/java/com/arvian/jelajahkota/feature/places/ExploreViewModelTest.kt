package com.arvian.jelajahkota.feature.places

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
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
    fun loadsPlacesAndDynamicFilterChips_onInit() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.allPlaces.size)
        assertTrue(state.availableCities.containsAll(listOf("Semua", "Malang", "Yogyakarta")))
        assertTrue(state.availableCategories.containsAll(listOf("Semua", "Wisata Kota", "Pegunungan", "Wisata Alam")))
    }

    @Test
    fun onCategorySelected_matchesRealPlaceCategories() = runTest {
        advanceUntilIdle()

        viewModel.onCategorySelected("Pegunungan")
        val state = viewModel.uiState.value
        assertEquals(1, state.filteredPlaces.size)
        assertEquals("bromo", state.filteredPlaces.first().id)
    }

    @Test
    fun onCitySelected_filtersPlacesByCity() = runTest {
        advanceUntilIdle()

        viewModel.onCitySelected("Yogyakarta")
        val state = viewModel.uiState.value
        assertEquals("Yogyakarta", state.selectedCity)
        assertEquals(1, state.filteredPlaces.size)
        assertEquals("malioboro", state.filteredPlaces.first().id)
    }

    @Test
    fun sortPriceAsc_sortsBromoDomesticPriceBeforeCobanRondoWithoutOverflow() = runTest {
        advanceUntilIdle()

        viewModel.onSortOptionSelected(ExploreSortOption.PRICE_ASC)
        val ids = viewModel.uiState.value.filteredPlaces.map { it.id }
        assertEquals(listOf("malioboro", "bromo", "coban"), ids)
    }

    @Test
    fun initialFiltersFromHome_areAppliedOnStartup() = runTest {
        val prefilteredVm = ExploreViewModel(
            placesRepository = fakeRepository,
            initialQuery = "",
            initialCity = "Malang",
            initialCategory = "Wisata Alam"
        )
        advanceUntilIdle()

        val state = prefilteredVm.uiState.value
        assertEquals(1, state.filteredPlaces.size)
        assertEquals("coban", state.filteredPlaces.first().id)
    }

    @Test
    fun onToggleViewAndResetFilters_workCorrectly() = runTest {
        advanceUntilIdle()

        viewModel.onToggleView(false)
        assertFalse(viewModel.uiState.value.isGridView)

        viewModel.onSearchQueryChanged("Bromo")
        viewModel.onCitySelected("Malang")
        viewModel.onResetFilters()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.selectedCity)
        assertNull(state.selectedCategory)
        assertEquals(ExploreSortOption.POPULAR, state.selectedSortOption)
    }

    private class TestFakePlacesRepository : PlacesRepository {
        private val places = listOf(
            Place(
                id = "malioboro",
                name = "Malioboro",
                location = "Yogyakarta",
                city = "Yogyakarta",
                rating = 4.6,
                reviewCount = 1200,
                imageResId = 0,
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
                reviewCount = 5600,
                imageResId = 0,
                ticketPrice = "Rp 29.000 (Domestik) / Rp 320.000 (Mancanegara)",
                ticketPriceAmount = 29_000,
                isPopular = true,
                category = "Pegunungan"
            ),
            Place(
                id = "coban",
                name = "Coban Rondo",
                location = "Pujon, Malang",
                city = "Malang",
                rating = 4.7,
                reviewCount = 1950,
                imageResId = 0,
                ticketPrice = "Rp 35.000 / orang",
                ticketPriceAmount = 35_000,
                isPopular = false,
                category = "Wisata Alam"
            )
        )

        override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(
            listOf(
                CityDestination("malang", "Malang", 0),
                CityDestination("jogja", "Yogyakarta", 0)
            )
        )

        override fun getPopularPlaces(): Flow<List<Place>> = flowOf(places.filter { it.isPopular })
        override fun getCategories(): Flow<List<CategoryItem>> = flowOf(
            listOf(
                CategoryItem("cat_city", "Wisata Kota", 0),
                CategoryItem("cat_mountain", "Pegunungan", 0),
                CategoryItem("cat_nature", "Wisata Alam", 0)
            )
        )

        override fun getPlaces(): Flow<List<Place>> = flowOf(places)
        override fun getPlaceById(id: String): Flow<Place?> = flowOf(places.find { it.id == id })
        override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(places)
    }
}
