package com.arvian.jelajahkota.core.domain.model

import androidx.annotation.DrawableRes

data class VisitPlan(
    val id: String,
    val placeId: String,
    val placeName: String,
    val placeLocation: String,
    @param:DrawableRes val imageResId: Int,
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val visitorCount: Int,
    val notes: String = "",
    val startEpochDay: Long = 0L,
    val endEpochDay: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
