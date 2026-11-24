package com.arvian.jelajahkota.core.data.supabase.dto

import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import com.arvian.jelajahkota.feature.plans.IndonesianDateFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("full_name") val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val bio: String = "",
    @SerialName("birth_date") val birthDate: String? = null,
    val gender: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class CityDto(
    val id: String,
    val name: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class PlaceDto(
    val id: String,
    @SerialName("city_id") val cityId: String = "",
    @SerialName("category_id") val categoryId: String = "",
    val name: String,
    val location: String,
    @SerialName("city_name") val cityName: String = "Kota Malang",
    @SerialName("category_name") val categoryName: String = "",
    val description: String = "",
    val duration: String = "1-2 Jam",
    @SerialName("min_duration_minutes") val minDurationMinutes: Int? = null,
    @SerialName("max_duration_minutes") val maxDurationMinutes: Int? = null,
    @SerialName("ticket_price") val ticketPrice: String = "Gratis",
    @SerialName("ticket_price_amount") val ticketPriceAmount: Int = 0,
    @SerialName("opening_hours") val openingHours: String = "07.00 - 18.00 WIB",
    @SerialName("operational_days") val operationalDays: String = "Setiap Hari (Senin - Minggu)",
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val rating: Double = 0.0,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("is_popular") val isPopular: Boolean = false
)

@Serializable
data class PlacePhotoDto(
    val id: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class ReviewDto(
    val id: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("author_name") val authorName: String = "Penjelajah",
    @SerialName("author_avatar_url") val authorAvatarUrl: String? = null,
    val rating: Double,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ReviewInsertDto(
    @SerialName("place_id") val placeId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("author_avatar_url") val authorAvatarUrl: String? = null,
    val rating: Double,
    val content: String
)

@Serializable
data class ReviewUpdateDto(
    val rating: Double,
    val content: String
)

@Serializable
data class FavoriteDto(
    @SerialName("user_id") val userId: String,
    @SerialName("place_id") val placeId: String
)

@Serializable
data class VisitPlanDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("place_name") val placeName: String,
    @SerialName("place_location") val placeLocation: String = "",
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("visitor_count") val visitorCount: Int = 1,
    val notes: String = "",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class VisitPlanInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("place_name") val placeName: String,
    @SerialName("place_location") val placeLocation: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("visitor_count") val visitorCount: Int,
    val notes: String
)

@Serializable
data class VisitorPhotoDto(
    val id: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("uploader_name") val uploaderName: String = "Penjelajah",
    @SerialName("storage_path") val storagePath: String,
    @SerialName("image_url") val imageUrl: String,
    val caption: String = "",
    @SerialName("mime_type") val mimeType: String = "image/jpeg",
    @SerialName("file_size_bytes") val fileSizeBytes: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class VisitorPhotoInsertDto(
    @SerialName("place_id") val placeId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("uploader_name") val uploaderName: String,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("image_url") val imageUrl: String,
    val caption: String,
    @SerialName("mime_type") val mimeType: String,
    @SerialName("file_size_bytes") val fileSizeBytes: Int
)

object SupabaseMappers {

    fun fallbackDrawableForCity(cityId: String): Int = when (cityId.lowercase()) {
        "malang" -> R.drawable.img_destination_kayutangan
        "batu" -> R.drawable.img_destination_night_view
        "kab_malang" -> R.drawable.img_destination_bromo
        "jogja" -> R.drawable.img_city_yogyakarta
        "semarang" -> R.drawable.img_place_kota_lama
        "jkt" -> R.drawable.img_city_jakarta
        "solo" -> R.drawable.img_city_solo
        "bdg" -> R.drawable.img_city_bandung
        "sby" -> R.drawable.img_city_surabaya
        else -> R.drawable.img_destination_alun_tugu
    }

    fun fallbackDrawableForCategory(categoryId: String): Int = when (categoryId.lowercase()) {
        "cat_city" -> R.drawable.img_category_beach
        "cat_heritage" -> R.drawable.img_destination_cafe
        "cat_mountain" -> R.drawable.img_category_mountain
        "cat_nature" -> R.drawable.img_destination_waterfall
        "cat_family" -> R.drawable.img_destination_photo
        else -> R.drawable.img_destination_photo
    }

    fun fallbackDrawableForPlace(placeId: String): Int = when (placeId.lowercase()) {
        "place_jodipan" -> R.drawable.img_destination_jodipan
        "place_kayutangan" -> R.drawable.img_destination_kayutangan
        "place_bromo" -> R.drawable.img_destination_bromo
        "place_alun_tugu" -> R.drawable.img_destination_alun_tugu
        "place_waterfall" -> R.drawable.img_destination_waterfall
        "place_museum_angkut" -> R.drawable.img_destination_photo
        "place_alun_batu" -> R.drawable.img_destination_night_view
        "place_malioboro" -> R.drawable.img_place_malioboro
        "place_kota_lama" -> R.drawable.img_place_kota_lama
        else -> R.drawable.img_destination_photo
    }

    fun fallbackGalleryForPlace(placeId: String): List<Int> = when (placeId.lowercase()) {
        "place_jodipan" -> listOf(R.drawable.img_destination_jodipan, R.drawable.img_destination_photo, R.drawable.img_destination_cafe)
        "place_kayutangan" -> listOf(R.drawable.img_destination_kayutangan, R.drawable.img_destination_cafe, R.drawable.img_destination_night_view)
        "place_bromo" -> listOf(R.drawable.img_destination_bromo, R.drawable.img_destination_photo, R.drawable.img_destination_night_view)
        "place_alun_tugu" -> listOf(R.drawable.img_destination_alun_tugu, R.drawable.img_destination_night_view)
        "place_waterfall" -> listOf(R.drawable.img_destination_waterfall, R.drawable.img_destination_photo)
        "place_museum_angkut" -> listOf(R.drawable.img_destination_photo, R.drawable.img_destination_night_view, R.drawable.img_destination_cafe)
        "place_alun_batu" -> listOf(R.drawable.img_destination_night_view, R.drawable.img_destination_culinary)
        "place_malioboro" -> listOf(R.drawable.img_place_malioboro, R.drawable.img_destination_night_view, R.drawable.img_destination_culinary)
        "place_kota_lama" -> listOf(R.drawable.img_place_kota_lama, R.drawable.img_destination_cafe)
        else -> listOf(fallbackDrawableForPlace(placeId))
    }

    fun ProfileDto.toDomain(): User = User(
        id = id,
        fullName = fullName.ifBlank { email.substringBefore("@").ifBlank { "Penjelajah" } },
        email = email,
        phone = phone,
        city = city,
        bio = bio,
        birthDate = birthDate.orEmpty(),
        gender = gender.orEmpty(),
        avatarUrl = avatarUrl,
        avatarResId = 0
    )

    fun CityDto.toDomain(): CityDestination = CityDestination(
        id = id,
        name = name,
        imageResId = fallbackDrawableForCity(id),
        imageUrl = imageUrl
    )

    fun CategoryDto.toDomain(): CategoryItem = CategoryItem(
        id = id,
        name = name,
        iconResId = fallbackDrawableForCategory(id),
        iconUrl = iconUrl
    )

    fun PlaceDto.toDomain(photoUrls: List<String> = emptyList()): Place = Place(
        id = id,
        name = name,
        location = location,
        city = cityName.ifBlank { "Kota Malang" },
        rating = rating,
        reviewCount = reviewCount,
        imageResId = fallbackDrawableForPlace(id),
        imageUrl = imageUrl,
        galleryImageResIds = if (photoUrls.isNotEmpty()) emptyList() else fallbackGalleryForPlace(id),
        galleryImageUrls = photoUrls,
        description = description,
        duration = Place.formatDurationRange(minDurationMinutes, maxDurationMinutes, duration),
        minDurationMinutes = minDurationMinutes,
        maxDurationMinutes = maxDurationMinutes,
        ticketPrice = ticketPrice,
        ticketPriceAmount = ticketPriceAmount,
        openingHours = openingHours,
        operationalDays = operationalDays,
        latitude = latitude,
        longitude = longitude,
        isPopular = isPopular,
        category = categoryName
    )

    fun ReviewDto.toDomain(placeName: String = ""): Review = Review(
        id = id,
        placeId = placeId,
        userId = userId,
        authorName = authorName.ifBlank { "Penjelajah" },
        authorAvatarUrl = authorAvatarUrl,
        authorAvatarResId = 0,
        rating = rating,
        date = formatTimestampLabel(createdAt),
        content = content,
        placeName = placeName
    )

    fun VisitPlanDto.toDomain(placeImageUrl: String? = null): VisitPlan {
        val parsedStart = runCatching { LocalDate.parse(startDate) }.getOrElse { LocalDate.now() }
        val parsedEnd = runCatching { LocalDate.parse(endDate) }.getOrElse { parsedStart }
        val duration = (ChronoUnit.DAYS.between(parsedStart, parsedEnd).toInt() + 1).coerceAtLeast(1)
        val formattedStart = "${IndonesianDateFormatter.dayOfWeekShort(parsedStart.dayOfWeek)}, ${IndonesianDateFormatter.dayMonthYear(parsedStart)}"
        val formattedEnd = "${IndonesianDateFormatter.dayOfWeekShort(parsedEnd.dayOfWeek)}, ${IndonesianDateFormatter.dayMonthYear(parsedEnd)}"
        return VisitPlan(
            id = id,
            placeId = placeId,
            placeName = placeName,
            placeLocation = placeLocation,
            imageResId = fallbackDrawableForPlace(placeId),
            imageUrl = placeImageUrl,
            startDate = formattedStart,
            endDate = formattedEnd,
            durationDays = duration,
            visitorCount = visitorCount,
            notes = notes,
            startEpochDay = parsedStart.toEpochDay(),
            endEpochDay = parsedEnd.toEpochDay()
        )
    }

    fun VisitorPhotoDto.toDomain(): VisitorPhoto = VisitorPhoto(
        id = id,
        placeId = placeId,
        userId = userId,
        uploaderName = uploaderName.ifBlank { "Penjelajah" },
        storagePath = storagePath,
        imageUrl = imageUrl,
        fallbackResId = fallbackDrawableForPlace(placeId),
        caption = caption,
        createdAtLabel = formatTimestampLabel(createdAt)
    )

    fun formatTimestampLabel(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "Baru saja"
        return runCatching {
            val instant = Instant.parse(isoString)
            val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            IndonesianDateFormatter.dayMonthYear(date)
        }.getOrElse {
            isoString.take(10)
        }
    }
}
