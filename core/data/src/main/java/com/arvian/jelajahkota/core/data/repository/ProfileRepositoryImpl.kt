package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl : ProfileRepository {

    private val userProfile = MutableStateFlow(
        User(
            id = "user_arvian",
            fullName = "Arvian Diva",
            email = "arvian@jelajahkota.id",
            phone = "+62 812-3456-7890",
            city = "Kota Malang",
            bio = "Pecinta sejarah dan destinasi heritage di Jawa Timur.",
            avatarResId = DesignR.drawable.img_user_avatar
        )
    )

    override fun getProfile(): Flow<User> = userProfile.asStateFlow()

    override fun updateProfile(fullName: String, phone: String, city: String, bio: String): Flow<Result<Boolean>> = flow {
        userProfile.value = userProfile.value.copy(
            fullName = fullName,
            phone = phone,
            city = city,
            bio = bio
        )
        emit(Result.success(true))
    }

    override fun updateAvatar(avatarUri: String): Flow<Result<Boolean>> = flow {
        userProfile.value = userProfile.value.copy(
            avatarUrl = avatarUri
        )
        emit(Result.success(true))
    }
}
