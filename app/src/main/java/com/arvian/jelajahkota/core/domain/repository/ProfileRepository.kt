package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<User?>
    suspend fun updateProfile(
        fullName: String,
        phone: String,
        city: String,
        bio: String,
        birthDate: String = "",
        gender: String = ""
    ): Result<User>
    suspend fun updateAvatar(avatarUri: String): Result<User>
    suspend fun uploadAvatar(bytes: ByteArray, declaredMimeType: String?): Result<User> =
        updateAvatar("local://avatar_${System.currentTimeMillis()}.webp")
}
