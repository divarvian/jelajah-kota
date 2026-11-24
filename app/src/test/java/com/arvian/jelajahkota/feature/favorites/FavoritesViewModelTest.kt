package com.arvian.jelajahkota.feature.favorites

import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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

    @Test
    fun `toggleFavorite removes place from favorites list`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleFavorite("1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.favoritePlaces.size)
        assertEquals("2", viewModel.uiState.value.favoritePlaces.first().id)
    }

    private class FakeFavoritesRepository : FavoritesRepository {
        private val allPlaces = listOf(
            Place(id = "1", name = "Coban Rondo", location = "Pujon, Malang", rating = 4.7, imageResId = 0),
            Place(id = "2", name = "Kayutangan", location = "Klojen, Malang", rating = 4.8, imageResId = 0)
        )
        private val ids = MutableStateFlow(setOf("1", "2"))

        override fun getFavoritePlaceIds(): Flow<Set<String>> = ids
        override fun getFavoritePlaces(): Flow<List<Place>> = ids.map { set -> allPlaces.filter { it.id in set } }
        override fun isFavorite(placeId: String): Flow<Boolean> = ids.map { placeId in it }
        override suspend fun toggleFavorite(placeId: String): Boolean {
            val next = if (placeId in ids.value) ids.value - placeId else ids.value + placeId
            ids.value = next
            return placeId in next
        }
    }
}
