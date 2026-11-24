package com.arvian.jelajahkota.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

// ----------------------------------------------------
// Splash & Onboarding Destinations
// ----------------------------------------------------
@Serializable
data object SplashDestination

@Serializable
data object OnboardingDestination

// ----------------------------------------------------
// Auth Flow Destinations
// ----------------------------------------------------
@Serializable
data object SignInDestination

@Serializable
data object SignUpDestination

@Serializable
data object ForgotPasswordDestination

@Serializable
data class OtpVerificationDestination(val email: String)

@Serializable
data class ResetPasswordDestination(val email: String)

@Serializable
data object PasswordUpdatedDestination

// ----------------------------------------------------
// Top-Level Main Shell (Hosts 5 Bottom-Nav Tabs)
// ----------------------------------------------------
@Serializable
data object MainShellDestination

// ----------------------------------------------------
// Destination Detail & Planning Destinations
// ----------------------------------------------------
@Serializable
data class PlaceDetailDestination(val placeId: String)

@Serializable
data class DatePickerDestination(val placeId: String)

@Serializable
data class PlanConfirmationDestination(
    val placeId: String,
    val startEpochDay: Long,
    val endEpochDay: Long
)

// ----------------------------------------------------
// Reviews & Visitor Gallery Destinations
// ----------------------------------------------------
@Serializable
data class ReviewsDestination(val placeId: String)

@Serializable
data object MyReviewsDestination

@Serializable
data class PlaceGalleryDestination(val placeId: String)

// ----------------------------------------------------
// Settings & Profile Edit Destinations
// ----------------------------------------------------
@Serializable
data object SettingsDestination

@Serializable
data object EditProfileDestination

@Serializable
data object AvatarUploadDestination

// ----------------------------------------------------
// Helper Extensions for Route Classification
// ----------------------------------------------------
fun NavDestination?.isMainShell(): Boolean = this?.hasRoute<MainShellDestination>() == true
