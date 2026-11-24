package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.supabase.ImageUploadValidator
import com.arvian.jelajahkota.core.data.supabase.SupabaseErrorMapper
import com.arvian.jelajahkota.core.data.supabase.dto.ProfileDto
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.validation.ProfileValidator
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class SupabaseProfileRepositoryImpl(
    private val client: SupabaseClient,
    private val authRepository: SupabaseAuthRepositoryImpl
) : ProfileRepository {

    override fun getProfile(): Flow<User?> = authRepository.getCurrentUser()

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
        val activeUser = authRepository.currentUserOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengubah profil."))

        val payload = ProfileDto(
            id = activeUser.id,
            fullName = validated.fullName,
            email = activeUser.email,
            phone = validated.phone,
            city = validated.city,
            bio = validated.bio,
            birthDate = validated.birthDateIso,
            gender = validated.genderCode,
            avatarUrl = activeUser.avatarUrl
        )

        return try {
            val saved = client.from("profiles")
                .upsert(payload) { select() }
                .decodeSingle<ProfileDto>()
                .toDomain()
            authRepository.updateCachedUser(saved)
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal menyimpan profil.")))
        }
    }

    override suspend fun updateAvatar(avatarUri: String): Result<User> {
        val activeUser = authRepository.currentUserOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu."))

        val payload = ProfileDto(
            id = activeUser.id,
            fullName = activeUser.fullName,
            email = activeUser.email,
            phone = activeUser.phone,
            city = activeUser.city,
            bio = activeUser.bio,
            birthDate = activeUser.birthDate.takeIf { it.isNotBlank() },
            gender = activeUser.gender.takeIf { it.isNotBlank() },
            avatarUrl = avatarUri
        )

        return try {
            val saved = client.from("profiles")
                .upsert(payload) { select() }
                .decodeSingle<ProfileDto>()
                .toDomain()
            authRepository.updateCachedUser(saved)
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal memperbarui foto profil.")))
        }
    }

    override suspend fun uploadAvatar(bytes: ByteArray, declaredMimeType: String?): Result<User> {
        val activeUser = authRepository.currentUserOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengganti foto profil."))

        val validated = ImageUploadValidator.validate(
            bytes = bytes,
            declaredMimeType = declaredMimeType,
            maxBytes = ImageUploadValidator.MAX_AVATAR_BYTES
        ).getOrElse { return Result.failure(it) }

        val storagePath = "${activeUser.id}/avatar_${System.currentTimeMillis()}.${validated.extension}"
        return try {
            val bucket = client.storage.from("avatars")
            bucket.upload(path = storagePath, data = validated.bytes) {
                upsert = true
                contentType = ContentType.parse(validated.mimeType)
            }
            val publicUrl = bucket.publicUrl(storagePath)
            updateAvatar(publicUrl)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal mengunggah foto profil.")))
        }
    }
}
