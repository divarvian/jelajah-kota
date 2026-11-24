package com.arvian.jelajahkota.core.domain.model

data class AppSettings(
    val locationEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = false
)
