package com.arvian.jelajahkota.core.data.repository

import android.content.Context
import com.arvian.jelajahkota.core.data.supabase.ImageUploadValidator
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.validation.ProfileValidator
import kotlinx.coroutines.flow.Flow
import java.io.File

class ProfileRepositoryImpl(
    private val sessionStore: UserSessionStore = UserSessionStore(),
    private val cacheDir: File? = null
) : ProfileRepository {

    constructor(sessionStore: UserSessionStore, context: Context) : this(
        sessionStore = sessionStore,
        cacheDir = File(context.cacheDir, "local_avatars").apply { mkdirs() }
    )

    override fun getProfile(): Flow<User?> = sessionStore.currentUser

    override suspend fun updateProfile(
        fullName: String,
        phone: String,
        city: String,
        bio: String,
        birthDate: String,
        gender: String
    ): Result<User> {
        val (validated, errors) = ProfileValidator.validate(
            fullName = fullName,
            phone = phone,
            city = city,
            bio = bio,
            birthDate = birthDate,
            gender = gender
        )
        if (validated == null) {
            return Result.failure(IllegalArgumentException(errors.firstErrorMessage ?: "Data profil tidak valid."))
        }
        val activeUser = sessionStore.currentUser.value
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengubah profil."))

        val updatedUser = activeUser.copy(
            fullName = validated.fullName,
            phone = validated.phone,
            city = validated.city,
            bio = validated.bio,
            birthDate = validated.birthDateIso.orEmpty(),
            gender = validated.genderCode.orEmpty()
        )
        sessionStore.setUser(updatedUser)
        return Result.success(updatedUser)
    }

    override suspend fun updateAvatar(avatarUri: String): Result<User> {
        val activeUser = sessionStore.currentUser.value
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengganti foto profil."))

        val updatedUser = activeUser.copy(avatarUrl = avatarUri)
        sessionStore.setUser(updatedUser)
        return Result.success(updatedUser)
    }

    override suspend fun uploadAvatar(bytes: ByteArray, declaredMimeType: String?): Result<User> {
        val activeUser = sessionStore.currentUser.value
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengganti foto profil."))

        val validated = ImageUploadValidator.validate(
            bytes = bytes,
            declaredMimeType = declaredMimeType,
            maxBytes = ImageUploadValidator.MAX_AVATAR_BYTES
        ).getOrElse { return Result.failure(it) }

        val uriString = if (cacheDir != null) {
            val outFile = File(cacheDir, "avatar_${activeUser.id}_${System.currentTimeMillis()}.${validated.extension}")
            outFile.writeBytes(validated.bytes)
            outFile.toURI().toString()
        } else {
            "local://avatar_${System.currentTimeMillis()}.${validated.extension}"
        }
        return updateAvatar(uriString)
    }
}
