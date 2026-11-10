package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val placesRepository: PlacesRepository
) : FavoritesRepository {

    private val favoriteIds = MutableStateFlow<Set<String>>(
        setOf("place_malioboro", "place_kota_lama")
    )

    override fun getFavoritePlaceIds(): Flow<Set<String>> = favoriteIds.asStateFlow()

    override fun getFavoritePlaces(): Flow<List<Place>> {
        return combine(placesRepository.getPlaces(), favoriteIds) { places, ids ->
            places.filter { ids.contains(it.id) }
        }
    }

    override fun isFavorite(placeId: String): Flow<Boolean> = favoriteIds.map { ids ->
        ids.contains(placeId)
    }

    override suspend fun toggleFavorite(placeId: String): Boolean {
        val current = favoriteIds.value.toMutableSet()
        val isNowFavorite = if (current.contains(placeId)) {
            current.remove(placeId)
            false
        } else {
            current.add(placeId)
            true
        }
        favoriteIds.value = current
        return isNowFavorite
    }
}
