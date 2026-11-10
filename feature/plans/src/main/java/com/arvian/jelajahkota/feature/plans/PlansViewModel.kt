package com.arvian.jelajahkota.feature.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlansUiState(
    val selectedPlace: Place = Place(
        id = "place_malioboro",
        name = "Malioboro",
        location = "Sosromenduran, Gedong Tengen, Kota Yogyakarta",
        city = "Yogyakarta",
        rating = 4.8,
        reviewCount = 3450,
        imageResId = DesignR.drawable.img_place_malioboro,
        galleryImageResIds = listOf(
            DesignR.drawable.img_place_malioboro,
            DesignR.drawable.img_destination_night_view
        ),
        description = "Kawasan jalan ikonik di jantung kota Yogyakarta dengan pedestrian rindang, deretan seniman jalanan, pertokoan khas batik, kuliner gudeg, dan suasana malam heritage tempo dulu.",
        duration = "2-3 Jam",
        ticketPrice = "Gratis",
        openingHours = "24 Jam (Setiap Hari)",
        isPopular = true,
        category = "City"
    ),
    val savedPlans: List<VisitPlan> = emptyList(),
    val startDay: Int = 17,
    val endDay: Int = 21,
    val monthName: String = "Okt",
    val year: Int = 2026,
    val durationDays: Int = 4,
    val visitorCount: Int = 2,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isDescriptionExpanded: Boolean = false
) {
    val dateRangeFormatted: String
        get() = "$startDay - $endDay $monthName $year"

    val departureFormatted: String
        get() = "Jumat   $startDay $monthName"

    val returnFormatted: String
        get() = "Selasa  $endDay $monthName"
}

class PlansViewModel(
    private val plansRepository: PlansRepository,
    private val placesRepository: PlacesRepository,
    private val placeId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlansUiState(isLoading = true))
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load saved plans list
            launch {
                plansRepository.getPlans().collect { plansList ->
                    _uiState.update { it.copy(savedPlans = plansList, isLoading = false) }
                }
            }

            // Load place if placeId provided
            if (!placeId.isNullOrBlank()) {
                launch {
                    placesRepository.getPlaceById(placeId).collect { place ->
                        if (place != null) {
                            _uiState.update { it.copy(selectedPlace = place) }
                        }
                    }
                }
            }
        }
    }

    fun toggleDescriptionExpanded() {
        _uiState.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
    }

    fun selectDateRange(startDay: Int, endDay: Int) {
        val duration = (endDay - startDay + 1).coerceAtLeast(1)
        _uiState.update {
            it.copy(
                startDay = startDay,
                endDay = endDay,
                durationDays = duration
            )
        }
    }

    fun incrementVisitors() {
        _uiState.update { it.copy(visitorCount = it.visitorCount + 1) }
    }

    fun decrementVisitors() {
        _uiState.update { it.copy(visitorCount = (it.visitorCount - 1).coerceAtLeast(1)) }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            plansRepository.deletePlan(planId).collect {}
        }
    }

    fun confirmPlan(onSuccess: () -> Unit) {
        val state = _uiState.value
        val plan = VisitPlan(
            id = "plan_${System.currentTimeMillis()}",
            placeId = state.selectedPlace.id,
            placeName = state.selectedPlace.name,
            placeLocation = state.selectedPlace.location,
            imageResId = state.selectedPlace.imageResId,
            startDate = state.departureFormatted,
            endDate = state.returnFormatted,
            durationDays = state.durationDays,
            visitorCount = state.visitorCount,
            notes = state.notes
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            plansRepository.savePlan(plan).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                        onSuccess()
                    },
                    onFailure = {
                        _uiState.update { it.copy(isSaving = false) }
                    }
                )
            }
        }
    }

    class Factory(
        private val plansRepository: PlansRepository,
        private val placesRepository: PlacesRepository,
        private val placeId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlansViewModel(plansRepository, placesRepository, placeId) as T
        }
    }
}
