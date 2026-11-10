package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<User>
    fun updateProfile(fullName: String, phone: String, city: String, bio: String): Flow<Result<Boolean>>
    fun updateAvatar(avatarUri: String): Flow<Result<Boolean>>
}
