package com.arvian.jelajahkota.core.domain.model

import androidx.annotation.DrawableRes

data class VisitorPhoto(
    val id: String,
    val placeId: String,
    val userId: String,
    val uploaderName: String,
    val storagePath: String = "",
    val imageUrl: String? = null,
    @param:DrawableRes val fallbackResId: Int = 0,
    val caption: String = "",
    val createdAtLabel: String = ""
)
