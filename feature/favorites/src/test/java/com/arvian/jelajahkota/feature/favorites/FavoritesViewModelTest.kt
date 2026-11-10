package com.arvian.jelajahkota.feature.favorites

import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
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
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeFavoritesRepository: FakeFavoritesRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeFavoritesRepository = FakeFavoritesRepository()
        viewModel = FavoritesViewModel(fakeFavoritesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFavorites loads places from repository`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.favoritePlaces.size)
    }

    @Test
    fun `searchQuery filters favorite places correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Coban")
        assertEquals(1, viewModel.uiState.value.filteredPlaces.size)
        assertEquals("Coban Rondo", viewModel.uiState.value.filteredPlaces.first().name)

        viewModel.onSearchQueryChange("Unknown")
        assertEquals(0, viewModel.uiState.value.filteredPlaces.size)
    }

    private class FakeFavoritesRepository : FavoritesRepository {
        val places = listOf(
            Place(id = "1", name = "Coban Rondo", location = "Pujon, Malang", rating = 4.7, imageResId = 0),
            Place(id = "2", name = "Kayutangan", location = "Klojen, Malang", rating = 4.8, imageResId = 0)
        )

        override fun getFavoritePlaceIds(): Flow<Set<String>> = flowOf(setOf("1", "2"))
        override fun getFavoritePlaces(): Flow<List<Place>> = flowOf(places)
        override fun isFavorite(placeId: String): Flow<Boolean> = flowOf(true)
        override suspend fun toggleFavorite(placeId: String): Boolean = true
    }
}
