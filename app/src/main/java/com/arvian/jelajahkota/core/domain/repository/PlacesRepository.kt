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
}
