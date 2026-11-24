package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FavoritesRepositoryImpl(
    private val placesRepository: PlacesRepository
) : FavoritesRepository {

    private val favoriteIds = MutableStateFlow(
        setOf("place_jodipan", "place_kayutangan", "place_malioboro")
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
        var isNowFavorite = false
        favoriteIds.update { current ->
            if (current.contains(placeId)) {
                isNowFavorite = false
                current - placeId
            } else {
                isNowFavorite = true
                current + placeId
            }
        }
        return isNowFavorite
    }
}
