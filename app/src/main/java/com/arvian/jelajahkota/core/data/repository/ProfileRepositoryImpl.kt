package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val sessionStore: UserSessionStore = UserSessionStore()
) : ProfileRepository {

    override fun getProfile(): Flow<User> =
        sessionStore.currentUser.map { it ?: UserSessionStore.DEFAULT_USER }

    override suspend fun updateProfile(
        fullName: String,
        phone: String,
        city: String,
        bio: String,
        birthDate: String,
        gender: String
    ): Result<User> {
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama tidak boleh kosong."))
        }
        var updatedUser = UserSessionStore.DEFAULT_USER
        sessionStore.updateUser { current ->
            current.copy(
                fullName = trimmedName,
                phone = phone.trim(),
                city = city.trim().ifBlank { current.city },
                bio = bio.trim(),
                birthDate = birthDate.trim(),
                gender = gender.trim()
            ).also { updatedUser = it }
        }
        return Result.success(updatedUser)
    }

    override suspend fun updateAvatar(avatarUri: String): Result<User> {
        var updatedUser = UserSessionStore.DEFAULT_USER
        sessionStore.updateUser { current ->
            current.copy(avatarUrl = avatarUri).also { updatedUser = it }
        }
        return Result.success(updatedUser)
    }
}
