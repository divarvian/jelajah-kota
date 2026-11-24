package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class PlansRepositoryImpl : PlansRepository {

    private val plans = MutableStateFlow(
        listOf(
            VisitPlan(
                id = "plan_1",
                placeId = "place_jodipan",
                placeName = "Kampung Warna-Warni Jodipan",
                placeLocation = "Jl. Juanda, Jodipan, Kec. Blimbing, Kota Malang",
                imageResId = R.drawable.img_destination_jodipan,
                startDate = "Jum, 6 Nov 2026",
                endDate = "Sen, 9 Nov 2026",
                durationDays = 4,
                visitorCount = 2,
                notes = "Membawa kamera mirrorless & payung lipat.",
                startEpochDay = LocalDate.of(2026, 11, 6).toEpochDay(),
                endEpochDay = LocalDate.of(2026, 11, 9).toEpochDay()
            )
        )
    )

    override fun getPlans(): Flow<List<VisitPlan>> = plans.asStateFlow()

    override fun getPlanById(planId: String): Flow<VisitPlan?> = plans.map { list ->
        list.find { it.id == planId }
    }

    override suspend fun savePlan(plan: VisitPlan): Result<VisitPlan> {
        plans.update { current ->
            listOf(plan) + current.filterNot { it.id == plan.id }
        }
        return Result.success(plan)
    }

    override suspend fun deletePlan(planId: String): Result<Unit> {
        plans.update { current ->
            current.filterNot { it.id == planId }
        }
        return Result.success(Unit)
    }
}
