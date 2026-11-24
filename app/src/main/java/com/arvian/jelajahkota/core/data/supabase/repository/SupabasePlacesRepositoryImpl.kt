package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.dto.CategoryDto
import com.arvian.jelajahkota.core.data.supabase.dto.CityDto
import com.arvian.jelajahkota.core.data.supabase.dto.PlaceDto
import com.arvian.jelajahkota.core.data.supabase.dto.PlacePhotoDto
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Supabase PostgREST-backed [PlacesRepository] with resilient local Malang seed fallback
 * so discovery works offline or before remote migrations/seed have been applied.
 */
class SupabasePlacesRepositoryImpl(
    private val client: SupabaseClient,
    private val fallbackLocal: PlacesRepositoryImpl = PlacesRepositoryImpl()
) : PlacesRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    // Seed synchronously from local Malang catalog for zero-jank frame-0 rendering,
    // then refresh live from Supabase PostgREST in the background.
    private val citiesState = MutableStateFlow(fallbackLocal.getCachedCityDestinations())
    private val categoriesState = MutableStateFlow(fallbackLocal.getCachedCategories())
    private val placesState = MutableStateFlow(fallbackLocal.getCachedPlaces())

    init {
        scope.launch {
            refreshFromRemote()
        }
    }

    suspend fun refreshFromRemote() {
        try {
            val remoteCities = client.from("cities")
                .select { order("sort_order", Order.ASCENDING) }
                .decodeList<CityDto>()
                .map { it.toDomain() }
            if (remoteCities.isNotEmpty()) {
                citiesState.value = remoteCities
            }

            val remoteCategories = client.from("categories")
                .select { order("sort_order", Order.ASCENDING) }
                .decodeList<CategoryDto>()
                .map { it.toDomain() }
            if (remoteCategories.isNotEmpty()) {
                categoriesState.value = remoteCategories
            }

            val remotePhotos = runCatching {
                client.from("place_photos")
                    .select { order("sort_order", Order.ASCENDING) }
                    .decodeList<PlacePhotoDto>()
            }.getOrDefault(emptyList())
            val photosByPlace = remotePhotos.groupBy({ it.placeId }, { it.imageUrl })

            val remotePlaces = client.from("places")
                .select { order("is_popular", Order.DESCENDING) }
                .decodeList<PlaceDto>()
                .map { dto -> dto.toDomain(photosByPlace[dto.id].orEmpty()) }
            if (remotePlaces.isNotEmpty()) {
                placesState.value = remotePlaces
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Retain local curated Malang fallback when offline or unmigrated
        }
    }

    override suspend fun refresh() {
        refreshFromRemote()
    }

    override fun getCityDestinations(): Flow<List<CityDestination>> =
        combine(citiesState, placesState) { cities, places ->
            if (places.isEmpty()) {
                cities
            } else {
                val activeCityNames = places.map { it.city.lowercase() }.toSet()
                val nonEmpty = cities.filter { it.name.lowercase() in activeCityNames }
                nonEmpty.ifEmpty { cities }
            }
        }

    override fun getPopularPlaces(): Flow<List<Place>> = placesState.map { list ->
        list.filter { it.isPopular }
    }

    override fun getCategories(): Flow<List<CategoryItem>> = categoriesState.asStateFlow()

    override fun getPlaces(): Flow<List<Place>> = placesState.asStateFlow()

    override fun getPlaceById(id: String): Flow<Place?> = placesState.map { list ->
        list.find { it.id == id }
    }

    override fun getCachedCityDestinations(): List<CityDestination> {
        val places = placesState.value
        val cities = citiesState.value
        if (places.isEmpty()) return cities
        val activeCityNames = places.map { it.city.lowercase() }.toSet()
        return cities.filter { it.name.lowercase() in activeCityNames }.ifEmpty { cities }
    }

    override fun getCachedCategories(): List<CategoryItem> = categoriesState.value

    override fun getCachedPlaces(): List<Place> = placesState.value

    override fun searchPlaces(
        query: String,
        cityId: String?,
        categoryId: String?
    ): Flow<List<Place>> = placesState.map { list ->
        val cities = citiesState.value
        val categories = categoriesState.value
        val resolvedCityName = cityId?.let { id ->
            cities.find { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }?.name ?: id
        }
        val resolvedCategoryName = categoryId?.let { id ->
            categories.find { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }?.name ?: id
        }

        list.filter { place ->
            val matchesQuery = query.isBlank() ||
                place.name.contains(query, ignoreCase = true) ||
                place.location.contains(query, ignoreCase = true) ||
                place.city.contains(query, ignoreCase = true) ||
                place.category.contains(query, ignoreCase = true) ||
                place.description.contains(query, ignoreCase = true)

            val matchesCity = resolvedCityName.isNullOrBlank() ||
                place.city.equals(resolvedCityName, ignoreCase = true)

            val matchesCategory = resolvedCategoryName.isNullOrBlank() ||
                place.category.equals(resolvedCategoryName, ignoreCase = true)

            matchesQuery && matchesCity && matchesCategory
        }
    }
}
