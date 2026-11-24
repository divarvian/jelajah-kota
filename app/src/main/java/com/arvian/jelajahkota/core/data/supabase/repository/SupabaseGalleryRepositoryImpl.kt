package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.supabase.ImageUploadValidator
import com.arvian.jelajahkota.core.data.supabase.SupabaseErrorMapper
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.data.supabase.dto.VisitorPhotoDto
import com.arvian.jelajahkota.core.data.supabase.dto.VisitorPhotoInsertDto
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import com.arvian.jelajahkota.core.domain.repository.GalleryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SupabaseGalleryRepositoryImpl(
    private val client: SupabaseClient,
    private val authRepository: SupabaseAuthRepositoryImpl
) : GalleryRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val photosByPlace = MutableStateFlow<Map<String, List<VisitorPhoto>>>(emptyMap())

    override fun getPhotosForPlace(placeId: String): Flow<List<VisitorPhoto>> {
        scope.launch { refreshPlacePhotos(placeId) }
        return photosByPlace.map { it[placeId].orEmpty() }
    }

    private suspend fun refreshPlacePhotos(placeId: String) {
        try {
            val rows = client.from("visitor_photos")
                .select {
                    filter { eq("place_id", placeId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<VisitorPhotoDto>()
                .map { it.toDomain() }
            photosByPlace.update { current -> current + (placeId to rows) }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Keep existing cache on transient network issue
        }
    }

    override suspend fun uploadPhoto(
        placeId: String,
        bytes: ByteArray,
        declaredMimeType: String?,
        caption: String
    ): Result<VisitorPhoto> {
        val activeUser = authRepository.currentUserOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengunggah foto."))

        val validated = ImageUploadValidator.validate(
            bytes = bytes,
            declaredMimeType = declaredMimeType,
            maxBytes = ImageUploadValidator.MAX_GALLERY_BYTES
        ).getOrElse { return Result.failure(it) }

        val storagePath = "${activeUser.id}/$placeId/${UUID.randomUUID()}.${validated.extension}"
        return try {
            val bucket = client.storage.from("visitor-photos")
            bucket.upload(path = storagePath, data = validated.bytes) {
                upsert = false
                contentType = ContentType.parse(validated.mimeType)
            }
            val publicUrl = bucket.publicUrl(storagePath)

            val insertPayload = VisitorPhotoInsertDto(
                placeId = placeId,
                userId = activeUser.id,
                uploaderName = activeUser.fullName.ifBlank { "Penjelajah" },
                storagePath = storagePath,
                imageUrl = publicUrl,
                caption = caption.trim().take(300),
                mimeType = validated.mimeType,
                fileSizeBytes = validated.bytes.size
            )

            val saved = client.from("visitor_photos")
                .insert(insertPayload) { select() }
                .decodeSingle<VisitorPhotoDto>()
                .toDomain()

            photosByPlace.update { current ->
                val updatedList = listOf(saved) + current[placeId].orEmpty()
                current + (placeId to updatedList)
            }
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal mengunggah foto ke galeri.")))
        }
    }

    override suspend fun deletePhoto(photo: VisitorPhoto): Result<Unit> {
        val userId = authRepository.currentUserIdOrNull()
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu."))
        if (photo.userId != userId) {
            return Result.failure(IllegalAccessException("Anda hanya dapat menghapus foto milik sendiri."))
        }
        return try {
            client.from("visitor_photos").delete {
                filter {
                    eq("id", photo.id)
                    eq("user_id", userId)
                }
            }
            if (photo.storagePath.isNotBlank()) {
                runCatching {
                    client.storage.from("visitor-photos").delete(photo.storagePath)
                }
            }
            photosByPlace.update { current ->
                val updated = current[photo.placeId].orEmpty().filterNot { it.id == photo.id }
                current + (photo.placeId to updated)
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal menghapus foto.")))
        }
    }
}
