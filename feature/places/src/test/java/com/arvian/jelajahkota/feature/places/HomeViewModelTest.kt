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
    override fun getUserProfile(): Flow<UserProfile> = flowOf(
        UserProfile("u1", "AntekAntekAsing", 123, 5)
    )

    override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(
        listOf(
            CityDestination("jogja", "Yogyakarta", 1),
            CityDestination("jkt", "Jakarta", 2)
        )
    )

    override fun getPopularPlaces(): Flow<List<Place>> = flowOf(
        listOf(
            Place("malioboro", "Malioboro", "DIY", 4.6, 100, 3, true)
        )
    )

    override fun getCategories(): Flow<List<CategoryItem>> = flowOf(
        listOf(
            CategoryItem("beach", "Beach", 4)
        )
    )
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
        assertEquals(1, state.popularPlaces.size)
        assertEquals("Malioboro", state.popularPlaces.first().name)
        assertEquals(1, state.categories.size)
        assertEquals("Beach", state.categories.first().name)
        assertEquals(HomeNavTab.BERANDA, state.selectedNavTab)
    }

    @Test
    fun onSearchQueryChanged_updatesQuery() = runTest {
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Bromo")
        assertEquals("Bromo", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun onCitySelected_togglesSelection() = runTest {
        advanceUntilIdle()

        viewModel.onCitySelected("jogja")
        assertEquals("jogja", viewModel.uiState.value.selectedCity)

        viewModel.onCitySelected("jogja")
        assertNull(viewModel.uiState.value.selectedCity)
    }

    @Test
    fun onCategorySelected_togglesSelection() = runTest {
        advanceUntilIdle()

        viewModel.onCategorySelected("beach")
        assertEquals("beach", viewModel.uiState.value.selectedCategory)

        viewModel.onCategorySelected("beach")
        assertNull(viewModel.uiState.value.selectedCategory)
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
