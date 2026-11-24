package com.arvian.jelajahkota.core.domain.model

enum class UserGender(
    val code: String,
    val label: String
) {
    MALE("male", "Laki-laki"),
    FEMALE("female", "Perempuan"),
    PREFER_NOT_TO_SAY("prefer_not_to_say", "Tidak disebutkan");

    companion object {
        fun fromRaw(raw: String?): UserGender? {
            val normalized = raw?.trim()?.lowercase().orEmpty()
            if (normalized.isBlank()) return null
            return when (normalized) {
                "male", "laki-laki", "pria", "l" -> MALE
                "female", "perempuan", "wanita", "p" -> FEMALE
                "prefer_not_to_say", "tidak disebutkan", "memilih tidak menyebutkan", "lainnya" -> PREFER_NOT_TO_SAY
                else -> null
            }
        }
    }
}

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String = "",
    val city: String = "",
    val bio: String = "",
    val birthDate: String = "",
    val gender: String = "",
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
