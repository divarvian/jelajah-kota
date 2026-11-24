package com.arvian.jelajahkota.feature.plans

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class PlansViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fixedToday = LocalDate.of(2026, 11, 24)
    private lateinit var fakePlansRepository: FakePlansRepository
    private lateinit var fakePlacesRepository: FakePlacesRepository
    private lateinit var viewModel: PlansViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePlansRepository = FakePlansRepository()
        fakePlacesRepository = FakePlacesRepository()
        viewModel = PlansViewModel(
            plansRepository = fakePlansRepository,
            placesRepository = fakePlacesRepository,
            placeId = "place_jodipan",
            today = fixedToday
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state uses real LocalDate and inclusive duration`() {
        val state = viewModel.uiState.value
        assertEquals(2, state.visitorCount)
        assertEquals(fixedToday, state.startDate)
        assertEquals(fixedToday.plusDays(2), state.endDate)
        assertEquals(3, state.durationDays)
        assertEquals("November 2026", state.displayedMonthTitle)
    }

    @Test
    fun `nextMonth and previousMonth navigate months while blocking months before today`() {
        assertFalse(viewModel.uiState.value.canNavigatePreviousMonth)
        viewModel.previousMonth()
        assertEquals(YearMonth.of(2026, 11), viewModel.uiState.value.displayedMonth)

        viewModel.nextMonth()
        assertEquals(YearMonth.of(2026, 12), viewModel.uiState.value.displayedMonth)
        assertTrue(viewModel.uiState.value.canNavigatePreviousMonth)

        viewModel.previousMonth()
        assertEquals(YearMonth.of(2026, 11), viewModel.uiState.value.displayedMonth)
    }

    @Test
    fun `onDateClicked rejects past dates and supports 2-click range across months`() {
        val yesterday = fixedToday.minusDays(1)
        viewModel.onDateClicked(yesterday)
        assertEquals(fixedToday, viewModel.uiState.value.startDate)

        val start = LocalDate.of(2026, 11, 30)
        val end = LocalDate.of(2026, 12, 2)
        viewModel.onDateClicked(start)
        assertEquals(start, viewModel.uiState.value.startDate)
        assertEquals(start, viewModel.uiState.value.endDate)
        assertEquals(1, viewModel.uiState.value.durationDays)

        viewModel.onDateClicked(end)
        assertEquals(start, viewModel.uiState.value.startDate)
        assertEquals(end, viewModel.uiState.value.endDate)
        assertEquals(3, viewModel.uiState.value.durationDays)
        assertEquals("Sen, 30 Nov 2026", viewModel.uiState.value.departureFormatted)
        assertEquals("Rab, 2 Des 2026", viewModel.uiState.value.returnFormatted)
    }

    @Test
    fun `confirmation viewModel preserves exact epochDay range and saves notes`() = runTest {
        val start = LocalDate.of(2026, 12, 10)
        val end = LocalDate.of(2026, 12, 14)

        val confirmVm = PlansViewModel(
            plansRepository = fakePlansRepository,
            placesRepository = fakePlacesRepository,
            placeId = "place_jodipan",
            initialStartEpochDay = start.toEpochDay(),
            initialEndEpochDay = end.toEpochDay(),
            today = fixedToday
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(start, confirmVm.uiState.value.startDate)
        assertEquals(end, confirmVm.uiState.value.endDate)
        assertEquals(5, confirmVm.uiState.value.durationDays)

        confirmVm.onNotesChanged("Bawa kamera dan payung")
        confirmVm.incrementVisitors()
        confirmVm.confirmPlan()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(confirmVm.uiState.value.saveSuccess)
        val saved = fakePlansRepository.plansFlow.value.first()
        assertEquals("Bawa kamera dan payung", saved.notes)
        assertEquals(3, saved.visitorCount)
        assertEquals(5, saved.durationDays)
        assertEquals(start.toEpochDay(), saved.startEpochDay)
        assertEquals(end.toEpochDay(), saved.endEpochDay)
    }

    @Test
    fun `increment decrement and deletePlan work properly`() = runTest {
        viewModel.decrementVisitors()
        assertEquals(1, viewModel.uiState.value.visitorCount)
        viewModel.decrementVisitors()
        assertEquals(1, viewModel.uiState.value.visitorCount)

        val plan = VisitPlan(
            id = "plan_test",
            placeId = "place_jodipan",
            placeName = "Kampung Jodipan",
            placeLocation = "Malang",
            imageResId = 0,
            startDate = "Sel, 24 Nov 2026",
            endDate = "Kam, 26 Nov 2026",
            durationDays = 3,
            visitorCount = 2
        )
        fakePlansRepository.savePlan(plan)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.savedPlans.size)

        viewModel.deletePlan("plan_test")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.savedPlans.size)
    }

    private class FakePlansRepository : PlansRepository {
        val plansFlow = MutableStateFlow<List<VisitPlan>>(emptyList())

        override fun getPlans(): Flow<List<VisitPlan>> = plansFlow
        override fun getPlanById(planId: String): Flow<VisitPlan?> = plansFlow.map { list -> list.find { it.id == planId } }

        override suspend fun savePlan(plan: VisitPlan): Result<VisitPlan> {
            plansFlow.value = listOf(plan) + plansFlow.value.filterNot { it.id == plan.id }
            return Result.success(plan)
        }

        override suspend fun deletePlan(planId: String): Result<Unit> {
            plansFlow.value = plansFlow.value.filterNot { it.id == planId }
            return Result.success(Unit)
        }
    }

    private class FakePlacesRepository : PlacesRepository {
        private val place = Place(
            id = "place_jodipan",
            name = "Kampung Warna-Warni Jodipan",
            location = "Malang",
            city = "Malang",
            rating = 4.7,
            imageResId = 0
        )

        override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(emptyList())
        override fun getPopularPlaces(): Flow<List<Place>> = flowOf(listOf(place))
        override fun getCategories(): Flow<List<CategoryItem>> = flowOf(emptyList())
        override fun getPlaces(): Flow<List<Place>> = flowOf(listOf(place))
        override fun getPlaceById(id: String): Flow<Place?> = flowOf(if (id == place.id) place else null)
        override fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>> = flowOf(listOf(place))
    }
}
