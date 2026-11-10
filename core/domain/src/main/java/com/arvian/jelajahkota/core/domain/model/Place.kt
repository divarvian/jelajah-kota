package com.arvian.jelajahkota.core.domain.model

data class Place(
    val id: String,
    val name: String,
    val location: String,
    val city: String = "Kota Malang",
    val rating: Double,
    val reviewCount: Int = 0,
    val imageResId: Int,
    val galleryImageResIds: List<Int> = emptyList(),
    val description: String = "",
    val duration: String = "1-2 Jam",
    val ticketPrice: String = "Gratis",
    val openingHours: String = "07.00 - 18.00 WIB",
    val operationalDays: String = "Setiap Hari (Senin - Minggu)",
    val isPopular: Boolean = false,
    val category: String = "",
    val isFavorite: Boolean = false
)

data class CityDestination(
    val id: String,
    val name: String,
    val imageResId: Int
)

data class CategoryItem(
    val id: String,
    val name: String,
    val iconResId: Int
)

data class UserProfile(
    val id: String,
    val name: String,
    val avatarResId: Int,
    val unreadNotificationsCount: Int = 0
)
