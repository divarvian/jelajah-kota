package com.arvian.jelajahkota.core.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String = "",
    val city: String = "Kota Malang",
    val bio: String = "",
    val avatarUrl: String? = null,
    val avatarResId: Int = 0
)

data class PasswordValidation(
    val hasMinLength: Boolean = false,
    val hasUpperAndLower: Boolean = false,
    val hasNumber: Boolean = false,
    val hasSymbol: Boolean = false,
    val isMatching: Boolean = false
) {
    val isValid: Boolean
        get() = hasMinLength && hasUpperAndLower && hasNumber && hasSymbol && isMatching
}
