package com.arvian.jelajahkota.core.domain.model

import androidx.annotation.DrawableRes
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Place(
    val id: String,
    val name: String,
    val location: String,
    val city: String = "Kota Malang",
    val rating: Double,
    val reviewCount: Int = 0,
    @param:DrawableRes val imageResId: Int,
    val imageUrl: String? = null,
    val galleryImageResIds: List<Int> = emptyList(),
    val galleryImageUrls: List<String> = emptyList(),
    val description: String = "",
    val duration: String = "1-2 Jam",
    val minDurationMinutes: Int? = null,
    val maxDurationMinutes: Int? = null,
    val ticketPrice: String = "Gratis",
    val ticketPriceAmount: Int = 0,
    val openingHours: String = "07.00 - 18.00 WIB",
    val operationalDays: String = "Setiap Hari (Senin - Minggu)",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isPopular: Boolean = false,
    val category: String = ""
) {
    val hasCoordinates: Boolean
        get() = latitude != null && longitude != null

    val hasLiveRating: Boolean
        get() = rating > 0.0 && reviewCount > 0

    fun distanceKmFrom(userLat: Double, userLng: Double): Double? {
        val lat = latitude ?: return null
        val lng = longitude ?: return null
        return haversineKm(userLat, userLng, lat, lng)
    }

    companion object {
        private const val EARTH_RADIUS_KM = 6371.0

        fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val rLat1 = Math.toRadians(lat1)
            val rLat2 = Math.toRadians(lat2)
            val a = sin(dLat / 2).pow(2) + cos(rLat1) * cos(rLat2) * sin(dLng / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return EARTH_RADIUS_KM * c
        }

        fun formatDistanceLabel(distanceKm: Double): String = when {
            distanceKm < 1.0 -> "${(distanceKm * 1000).toInt().coerceAtLeast(50)} m"
            distanceKm < 10.0 -> "${String.format(Locale.US, "%.1f", distanceKm).replace('.', ',')} km"
            else -> "${distanceKm.toInt()} km"
        }

        fun formatDurationRange(minMinutes: Int?, maxMinutes: Int?, fallback: String): String {
            if (minMinutes == null && maxMinutes == null) return fallback.ifBlank { "1-2 Jam" }
            fun formatSingle(mins: Int): String = if (mins >= 60 && mins % 60 == 0) {
                "${mins / 60} Jam"
            } else if (mins >= 60) {
                "${mins / 60}j ${mins % 60}m"
            } else {
                "$mins Menit"
            }
            return when {
                minMinutes != null && maxMinutes != null && minMinutes != maxMinutes -> {
                    if (minMinutes >= 60 && maxMinutes >= 60 && minMinutes % 60 == 0 && maxMinutes % 60 == 0) {
                        "${minMinutes / 60}-${maxMinutes / 60} Jam"
                    } else {
                        "${formatSingle(minMinutes)} - ${formatSingle(maxMinutes)}"
                    }
                }
                minMinutes != null -> formatSingle(minMinutes)
                maxMinutes != null -> formatSingle(maxMinutes)
                else -> fallback.ifBlank { "1-2 Jam" }
            }
        }
    }
}

data class CityDestination(
    val id: String,
    val name: String,
    @param:DrawableRes val imageResId: Int,
    val imageUrl: String? = null
)

data class CategoryItem(
    val id: String,
    val name: String,
    @param:DrawableRes val iconResId: Int,
    val iconUrl: String? = null
)
