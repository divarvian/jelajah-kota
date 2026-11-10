package com.arvian.jelajahkota.feature.plans

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.UserProfile
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlansViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePlansRepository: FakePlansRepository
    private lateinit var fakePlacesRepository: FakePlacesRepository
    private lateinit var viewModel: PlansViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePlansRepository = FakePlansRepository()
        fakePlacesRepository = FakePlacesRepository()
        viewModel = PlansViewModel(fakePlansRepository, fakePlacesRepository, "place_malioboro")
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has 2 visitors and 4 duration days`() {
        val state = viewModel.uiState.value
        assertEquals(2, state.visitorCount)
        assertEquals(4, state.durationDays)
    }

    @Test
    fun `increment and decrement visitors updates count correctly`() {
        viewModel.incrementVisitors()
        assertEquals(3, viewModel.uiState.value.visitorCount)

        viewModel.decrementVisitors()
        assertEquals(2, viewModel.uiState.value.visitorCount)

        viewModel.decrementVisitors()
        assertEquals(1, viewModel.uiState.value.visitorCount)

        // Cannot go below 1
        viewModel.decrementVisitors()
        assertEquals(1, viewModel.uiState.value.visitorCount)
    }

    @Test
    fun `selectDateRange updates days and duration correctly`() {
        viewModel.selectDateRange(10, 15)
        val state = viewModel.uiState.value
        assertEquals(10, state.startDay)
        assertEquals(15, state.endDay)
        assertEquals(6, state.durationDays)
    }

    @Test
    fun `confirmPlan saves to repository and executes callback`() = runTest {
        var successCalled = false
        viewModel.confirmPlan { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.saveSuccess)
        assertTrue(successCalled)
        assertEquals(1, fakePlansRepository.savedPlans.size)
    }

    @Test
    fun `deletePlan removes plan from repository`() = runTest {
        val plan = VisitPlan(
            id = "plan_test",
            placeId = "place_malioboro",
            placeName = "Malioboro",
            placeLocation = "Yogyakarta",
            imageResId = 0,
            startDate = "17 Okt",
            endDate = "21 Okt",
            durationDays = 4,
            visitorCount = 2
        )
        fakePlansRepository.savedPlans.add(plan)

        viewModel.deletePlan("plan_test")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, fakePlansRepository.savedPlans.size)
    }

    private class FakePlansRepository : PlansRepository {
        val savedPlans = mutableListOf<VisitPlan>()

        override fun getPlans(): Flow<List<VisitPlan>> = flowOf(savedPlans)
        override fun getPlanById(planId: String): Flow<VisitPlan?> = flowOf(savedPlans.find { it.id == planId })
        override fun savePlan(plan: VisitPlan): Flow<Result<Boolean>> {
            savedPlans.add(plan)
            return flowOf(Result.success(true))
        }
        override fun deletePlan(planId: String): Flow<Result<Boolean>> {
            savedPlans.removeIf { it.id == planId }
            return flowOf(Result.success(true))
        }
    }

    private class FakePlacesRepository : PlacesRepository {
        val testPlace = Place(
            id = "place_malioboro",
            name = "Malioboro",
            location = "Yogyakarta",
            rating = 4.8,
            imageResId = 0,
            description = "Test description"
        )

        override fun getUserProfile(): Flow<UserProfile> = flowOf(UserProfile("1", "User", 0))
        override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(emptyList())
        override fun getPopularPlaces(): Flow<List<Place>> = flowOf(listOf(testPlace))
        override fun getCategories(): Flow<List<CategoryItem>> = flowOf(emptyList())
        override fun getPlaces(): Flow<List<Place>> = flowOf(listOf(testPlace))
        override fun getPlaceById(id: String): Flow<Place?> = flowOf(testPlace.takeIf { it.id == id })
        override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(listOf(testPlace))
    }
}
