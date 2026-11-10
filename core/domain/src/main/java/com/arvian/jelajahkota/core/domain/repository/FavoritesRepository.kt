package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoritePlaceIds(): Flow<Set<String>>
    fun getFavoritePlaces(): Flow<List<Place>>
    fun isFavorite(placeId: String): Flow<Boolean>
    suspend fun toggleFavorite(placeId: String): Boolean
}
