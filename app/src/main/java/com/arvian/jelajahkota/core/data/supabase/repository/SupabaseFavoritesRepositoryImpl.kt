package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.supabase.dto.FavoriteDto
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupabaseFavoritesRepositoryImpl(
    private val client: SupabaseClient,
    private val placesRepository: PlacesRepository,
    private val authRepository: SupabaseAuthRepositoryImpl
) : FavoritesRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        scope.launch {
            authRepository.getCurrentUser().collect { user ->
                if (user == null) {
                    favoriteIds.value = emptySet()
                } else {
                    refreshFavorites(user.id)
                }
            }
        }
    }

    private suspend fun refreshFavorites(userId: String) {
        try {
            val rows = client.from("favorites")
                .select { filter { eq("user_id", userId) } }
                .decodeList<FavoriteDto>()
            favoriteIds.value = rows.map { it.placeId }.toSet()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Keep last known set on transient network error
        }
    }

    override fun getFavoritePlaceIds(): Flow<Set<String>> = favoriteIds.asStateFlow()

    override fun getFavoritePlaces(): Flow<List<Place>> = combine(
        placesRepository.getPlaces(),
        favoriteIds
    ) { allPlaces, ids ->
        allPlaces.filter { it.id in ids }
    }

    override fun isFavorite(placeId: String): Flow<Boolean> =
        favoriteIds.map { placeId in it }

    override suspend fun toggleFavorite(placeId: String): Boolean {
        val userId = authRepository.currentUserIdOrNull() ?: return false
        val currentlyFavorite = placeId in favoriteIds.value
        val nextState = !currentlyFavorite

        // Optimistic UI update
        favoriteIds.update { current ->
            if (nextState) current + placeId else current - placeId
        }

        return try {
            if (nextState) {
                client.from("favorites").insert(FavoriteDto(userId = userId, placeId = placeId))
            } else {
                client.from("favorites").delete {
                    filter {
                        eq("user_id", userId)
                        eq("place_id", placeId)
                    }
                }
            }
            nextState
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Revert optimistic toggle if remote write fails
            favoriteIds.update { current ->
                if (currentlyFavorite) current + placeId else current - placeId
            }
            currentlyFavorite
        }
    }
}
