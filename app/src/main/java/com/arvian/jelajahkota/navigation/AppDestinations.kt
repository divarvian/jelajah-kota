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
data class EmailSentDestination(val email: String)

@Serializable
data class OtpVerificationDestination(val email: String)

@Serializable
data class ResetPasswordDestination(val email: String)

@Serializable
data object PasswordUpdatedDestination

// ----------------------------------------------------
// Top-Level Main Shell Destinations (Unified Bottom Bar)
// ----------------------------------------------------
@Serializable
data object MainDestination

@Serializable
data object ExploreDestination

@Serializable
data object PlansListDestination

@Serializable
data object FavoritesDestination

@Serializable
data object ProfileDestination

// ----------------------------------------------------
// Destination Detail & Planning Destinations
// ----------------------------------------------------
@Serializable
data class PlaceDetailDestination(val placeId: String)

@Serializable
data class SchedulePlanDestination(val placeId: String)

@Serializable
data class DatePickerDestination(val placeId: String)

@Serializable
data class PlanConfirmationDestination(val placeId: String)

// ----------------------------------------------------
// Reviews Flow Destination
// ----------------------------------------------------
@Serializable
data class ReviewsDestination(val placeId: String)

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
// Helper Extensions for Fast Top-Level Checks
// ----------------------------------------------------
fun NavDestination?.isTopLevel(): Boolean = this?.let {
    it.hasRoute<MainDestination>() ||
    it.hasRoute<ExploreDestination>() ||
    it.hasRoute<PlansListDestination>() ||
    it.hasRoute<FavoritesDestination>() ||
    it.hasRoute<ProfileDestination>()
} ?: false
