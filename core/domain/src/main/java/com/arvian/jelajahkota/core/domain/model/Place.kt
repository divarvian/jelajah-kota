package com.arvian.jelajahkota.core.domain.model

data class Place(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val reviewCount: Int = 0,
    val imageResId: Int,
    val isPopular: Boolean = false,
    val category: String = ""
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
