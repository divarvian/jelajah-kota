package com.arvian.jelajahkota.feature.plans

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Immutable
data class PlansUiState(
    val selectedPlace: Place? = null,
    val savedPlans: List<VisitPlan> = emptyList(),
    val minSelectableDate: LocalDate = LocalDate.now(),
    val displayedMonth: YearMonth = YearMonth.now(),
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(2),
    val visitorCount: Int = 2,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val durationDays: Int
        get() = (ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1).coerceAtLeast(1)

    val canNavigatePreviousMonth: Boolean
        get() = displayedMonth.isAfter(YearMonth.from(minSelectableDate))

    val displayedMonthTitle: String
        get() = "${IndonesianDateFormatter.monthFull(displayedMonth.month)} ${displayedMonth.year}"

    val dateRangeFormatted: String
        get() = if (startDate == endDate) {
            IndonesianDateFormatter.dayMonthYear(startDate)
        } else if (startDate.month == endDate.month && startDate.year == endDate.year) {
            "${startDate.dayOfMonth} - ${endDate.dayOfMonth} ${IndonesianDateFormatter.monthShort(startDate.month)} ${startDate.year}"
        } else {
            "${IndonesianDateFormatter.dayMonth(startDate)} - ${IndonesianDateFormatter.dayMonthYear(endDate)}"
        }

    val departureFormatted: String
        get() = "${IndonesianDateFormatter.dayOfWeekShort(startDate.dayOfWeek)}, ${IndonesianDateFormatter.dayMonthYear(startDate)}"

    val returnFormatted: String
        get() = "${IndonesianDateFormatter.dayOfWeekShort(endDate.dayOfWeek)}, ${IndonesianDateFormatter.dayMonthYear(endDate)}"
}

object IndonesianDateFormatter {
    fun dayOfWeekShort(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
        DayOfWeek.MONDAY -> "Sen"
        DayOfWeek.TUESDAY -> "Sel"
        DayOfWeek.WEDNESDAY -> "Rab"
        DayOfWeek.THURSDAY -> "Kam"
        DayOfWeek.FRIDAY -> "Jum"
        DayOfWeek.SATURDAY -> "Sab"
        DayOfWeek.SUNDAY -> "Min"
    }

    fun monthShort(month: Month): String = when (month) {
        Month.JANUARY -> "Jan"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"
        Month.MAY -> "Mei"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Agu"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Okt"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Des"
    }

    fun monthFull(month: Month): String = when (month) {
        Month.JANUARY -> "Januari"
        Month.FEBRUARY -> "Februari"
        Month.MARCH -> "Maret"
        Month.APRIL -> "April"
        Month.MAY -> "Mei"
        Month.JUNE -> "Juni"
        Month.JULY -> "Juli"
        Month.AUGUST -> "Agustus"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "Oktober"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "Desember"
    }

    fun dayMonth(date: LocalDate): String = "${date.dayOfMonth} ${monthShort(date.month)}"

    fun dayMonthYear(date: LocalDate): String = "${date.dayOfMonth} ${monthShort(date.month)} ${date.year}"
}

class PlansViewModel(
    private val plansRepository: PlansRepository,
    private val placesRepository: PlacesRepository,
    private val placeId: String? = null,
    initialStartEpochDay: Long? = null,
    initialEndEpochDay: Long? = null,
    today: LocalDate = LocalDate.now()
) : ViewModel() {

    private val initialStart = initialStartEpochDay?.let(LocalDate::ofEpochDay) ?: today
    private val initialEnd = initialEndEpochDay?.let(LocalDate::ofEpochDay)?.takeIf { !it.isBefore(initialStart) }
        ?: initialStart.plusDays(2)

    private val _uiState = MutableStateFlow(
        PlansUiState(
            minSelectableDate = today,
            displayedMonth = YearMonth.from(initialStart),
            startDate = initialStart,
            endDate = initialEnd,
            isLoading = true
        )
    )
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                plansRepository.getPlans().collect { plansList ->
                    _uiState.update { it.copy(savedPlans = plansList, isLoading = false) }
                }
            }

            if (!placeId.isNullOrBlank()) {
                launch {
                    placesRepository.getPlaceById(placeId).collect { place ->
                        _uiState.update { it.copy(selectedPlace = place, isLoading = false) }
                    }
                }
            }
        }
    }

    fun previousMonth() {
        _uiState.update { current ->
            if (!current.canNavigatePreviousMonth) current
            else current.copy(displayedMonth = current.displayedMonth.minusMonths(1))
        }
    }

    fun nextMonth() {
        _uiState.update { current ->
            current.copy(displayedMonth = current.displayedMonth.plusMonths(1))
        }
    }

    fun onDateClicked(date: LocalDate) {
        _uiState.update { current ->
            if (date.isBefore(current.minSelectableDate)) {
                return@update current
            }
            if (current.startDate != current.endDate || date.isBefore(current.startDate)) {
                current.copy(
                    startDate = date,
                    endDate = date,
                    displayedMonth = YearMonth.from(date),
                    errorMessage = null
                )
            } else {
                current.copy(
                    endDate = date,
                    errorMessage = null
                )
            }
        }
    }

    fun selectDateRange(start: LocalDate, end: LocalDate) {
        val validEnd = if (end.isBefore(start)) start else end
        _uiState.update {
            it.copy(
                startDate = start,
                endDate = validEnd,
                displayedMonth = YearMonth.from(start),
                errorMessage = null
            )
        }
    }

    fun incrementVisitors() {
        _uiState.update { it.copy(visitorCount = (it.visitorCount + 1).coerceAtMost(50)) }
    }

    fun decrementVisitors() {
        _uiState.update { it.copy(visitorCount = (it.visitorCount - 1).coerceAtLeast(1)) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            plansRepository.deletePlan(planId)
        }
    }

    fun confirmPlan() {
        val state = _uiState.value
        val place = state.selectedPlace
        if (place == null) {
            _uiState.update { it.copy(errorMessage = "Destinasi belum siap. Coba lagi.") }
            return
        }

        val plan = VisitPlan(
            id = "plan_${System.currentTimeMillis()}",
            placeId = place.id,
            placeName = place.name,
            placeLocation = place.location,
            imageResId = place.imageResId,
            startDate = state.departureFormatted,
            endDate = state.returnFormatted,
            durationDays = state.durationDays,
            visitorCount = state.visitorCount,
            notes = state.notes.trim(),
            startEpochDay = state.startDate.toEpochDay(),
            endEpochDay = state.endDate.toEpochDay()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = plansRepository.savePlan(plan)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Gagal menyimpan rencana.")
                    }
                }
            )
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    class Factory(
        private val plansRepository: PlansRepository,
        private val placesRepository: PlacesRepository,
        private val placeId: String? = null,
        private val initialStartEpochDay: Long? = null,
        private val initialEndEpochDay: Long? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlansViewModel(
                plansRepository = plansRepository,
                placesRepository = placesRepository,
                placeId = placeId,
                initialStartEpochDay = initialStartEpochDay,
                initialEndEpochDay = initialEndEpochDay
            ) as T
        }
    }
}
