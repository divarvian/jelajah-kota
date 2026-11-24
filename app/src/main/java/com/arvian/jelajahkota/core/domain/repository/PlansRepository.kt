package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.VisitPlan
import kotlinx.coroutines.flow.Flow

interface PlansRepository {
    fun getPlans(): Flow<List<VisitPlan>>
    fun getPlanById(planId: String): Flow<VisitPlan?>
    suspend fun savePlan(plan: VisitPlan): Result<VisitPlan>
    suspend fun deletePlan(planId: String): Result<Unit>
}
