package com.arvian.jelajahkota.core.domain.model

data class VisitPlan(
    val id: String,
    val placeId: String,
    val placeName: String,
    val placeLocation: String,
    val imageResId: Int,
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val visitorCount: Int,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
