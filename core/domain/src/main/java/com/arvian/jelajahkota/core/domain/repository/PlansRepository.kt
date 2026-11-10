package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.VisitPlan
import kotlinx.coroutines.flow.Flow

interface PlansRepository {
    fun getPlans(): Flow<List<VisitPlan>>
    fun getPlanById(planId: String): Flow<VisitPlan?>
    fun savePlan(plan: VisitPlan): Flow<Result<Boolean>>
    fun deletePlan(planId: String): Flow<Result<Boolean>>
}
