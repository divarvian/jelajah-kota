package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun getCityDestinations(): Flow<List<CityDestination>>
    fun getPopularPlaces(): Flow<List<Place>>
    fun getCategories(): Flow<List<CategoryItem>>
    fun getPlaces(): Flow<List<Place>>
    fun getPlaceById(id: String): Flow<Place?>
    fun searchPlaces(query: String, cityId: String?, categoryId: String?): Flow<List<Place>>

    /**
     * Optional synchronous in-memory snapshots so ViewModels can render cached discovery data
     * on frame 0 without flashing a loading spinner during navigation transitions.
     */
    fun getCachedCityDestinations(): List<CityDestination> = emptyList()
    fun getCachedCategories(): List<CategoryItem> = emptyList()
    fun getCachedPlaces(): List<Place> = emptyList()
    fun getCachedPlaceById(id: String): Place? = getCachedPlaces().find { it.id == id }

    /** Refreshes catalog data from the remote backend when online. */
    suspend fun refresh() = Unit
}
