package com.arvian.jelajahkota.core.domain.model

import androidx.annotation.DrawableRes

data class Place(
    val id: String,
    val name: String,
    val location: String,
    val city: String = "Malang",
    val rating: Double,
    val reviewCount: Int = 0,
    @param:DrawableRes val imageResId: Int,
    val galleryImageResIds: List<Int> = emptyList(),
    val description: String = "",
    val duration: String = "1-2 Jam",
    val ticketPrice: String = "Gratis",
    val ticketPriceAmount: Int = 0,
    val openingHours: String = "07.00 - 18.00 WIB",
    val operationalDays: String = "Setiap Hari (Senin - Minggu)",
    val isPopular: Boolean = false,
    val category: String = ""
)

data class CityDestination(
    val id: String,
    val name: String,
    @param:DrawableRes val imageResId: Int
)

data class CategoryItem(
    val id: String,
    val name: String,
    @param:DrawableRes val iconResId: Int
)
