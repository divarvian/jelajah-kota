package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.supabase.SupabaseErrorMapper
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.data.supabase.dto.VisitPlanDto
import com.arvian.jelajahkota.core.data.supabase.dto.VisitPlanInsertDto
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class SupabasePlansRepositoryImpl(
    private val client: SupabaseClient,
    private val authRepository: SupabaseAuthRepositoryImpl
) : PlansRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val plans = MutableStateFlow<List<VisitPlan>>(emptyList())

    init {
        scope.launch {
            authRepository.getCurrentUser().collect { user ->
                if (user == null) {
                    plans.value = emptyList()
                } else {
                    refreshPlans(user.id)
                }
            }
        }
    }

    private suspend fun refreshPlans(userId: String) {
        try {
            val rows = client.from("visit_plans")
                .select {
                    filter { eq("user_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<VisitPlanDto>()
            plans.value = rows.map { it.toDomain() }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Preserve current cached plans on transient error
        }
    }

    override fun getPlans(): Flow<List<VisitPlan>> = plans.asStateFlow()

    override fun getPlanById(planId: String): Flow<VisitPlan?> =
        plans.map { list -> list.find { it.id == planId } }

    override suspend fun savePlan(plan: VisitPlan): Result<VisitPlan> {
        val userId = authRepository.currentUserIdOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk menyimpan rencana."))

        val startIso = if (plan.startEpochDay != 0L) {
            LocalDate.ofEpochDay(plan.startEpochDay).toString()
        } else {
            LocalDate.now().toString()
        }
        val endIso = if (plan.endEpochDay != 0L) {
            LocalDate.ofEpochDay(plan.endEpochDay).toString()
        } else {
            startIso
        }

        val payload = VisitPlanInsertDto(
            userId = userId,
            placeId = plan.placeId,
            placeName = plan.placeName,
            placeLocation = plan.placeLocation,
            startDate = startIso,
            endDate = endIso,
            visitorCount = plan.visitorCount.coerceIn(1, 50),
            notes = plan.notes.trim().take(1000)
        )

        return try {
            val saved = client.from("visit_plans")
                .insert(payload) { select() }
                .decodeSingle<VisitPlanDto>()
                .toDomain(plan.imageUrl)
            plans.update { current -> listOf(saved) + current }
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal menyimpan rencana kunjungan.")))
        }
    }

    override suspend fun deletePlan(planId: String): Result<Unit> {
        val userId = authRepository.currentUserIdOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu."))
        return try {
            client.from("visit_plans").delete {
                filter {
                    eq("id", planId)
                    eq("user_id", userId)
                }
            }
            plans.update { current -> current.filterNot { it.id == planId } }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal menghapus rencana kunjungan.")))
        }
    }
}
