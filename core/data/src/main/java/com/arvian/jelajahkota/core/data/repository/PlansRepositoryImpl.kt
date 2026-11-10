package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlansRepositoryImpl : PlansRepository {

    private val plans = MutableStateFlow<List<VisitPlan>>(
        listOf(
            VisitPlan(
                id = "plan_1",
                placeId = "place_malioboro",
                placeName = "Kampung Warna-Warni Jodipan",
                placeLocation = "Klojen, Kota Malang",
                imageResId = DesignR.drawable.img_place_malioboro,
                startDate = "6 Nov 2026",
                endDate = "9 Nov 2026",
                durationDays = 4,
                visitorCount = 2,
                notes = "Membawa kamera mirrorless & payung lipat."
            )
        )
    )

    override fun getPlans(): Flow<List<VisitPlan>> = plans.asStateFlow()

    override fun getPlanById(planId: String): Flow<VisitPlan?> = plans.map { list ->
        list.find { it.id == planId }
    }

    override fun savePlan(plan: VisitPlan): Flow<Result<Boolean>> = flow {
        val current = plans.value.toMutableList()
        current.removeAll { it.id == plan.id }
        current.add(0, plan)
        plans.value = current
        emit(Result.success(true))
    }

    override fun deletePlan(planId: String): Flow<Result<Boolean>> = flow {
        val current = plans.value.toMutableList()
        current.removeAll { it.id == planId }
        plans.value = current
        emit(Result.success(true))
    }
}
