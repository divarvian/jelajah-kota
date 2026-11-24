package com.arvian.jelajahkota.core.data.repository

import android.content.Context
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.data.supabase.ImageUploadValidator
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import com.arvian.jelajahkota.core.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.File

/**
 * Offline / local demo implementation of [GalleryRepository].
 * Enforces identical upload validation (<= 5 MB, MIME + magic bytes) and owner-only deletion.
 */
class GalleryRepositoryImpl(
    private val sessionStore: UserSessionStore = UserSessionStore(),
    private val cacheDir: File? = null
) : GalleryRepository {

    constructor(sessionStore: UserSessionStore, context: Context) : this(
        sessionStore = sessionStore,
        cacheDir = File(context.cacheDir, "local_gallery").apply { mkdirs() }
    )

    private val photos = MutableStateFlow(
        listOf(
            VisitorPhoto(
                id = "photo_seed_1",
                placeId = "place_jodipan",
                userId = "user_budi",
                uploaderName = "Budi Santoso",
                fallbackResId = R.drawable.img_destination_jodipan,
                caption = "Warna-warni rumah Jodipan terlihat cerah banget saat pagi hari!",
                createdAtLabel = "14 Nov 2026"
            ),
            VisitorPhoto(
                id = "photo_seed_2",
                placeId = "place_jodipan",
                userId = "user_dimas",
                uploaderName = "Dimas Anggara",
                fallbackResId = R.drawable.img_destination_photo,
                caption = "Spot foto favorit di sekitar jembatan kaca.",
                createdAtLabel = "10 Nov 2026"
            ),
            VisitorPhoto(
                id = "photo_seed_3",
                placeId = "place_kayutangan",
                userId = "user_siti",
                uploaderName = "Siti Rahmawati",
                fallbackResId = R.drawable.img_destination_kayutangan,
                caption = "Suasana sore klasik di koridor Kayutangan Heritage.",
                createdAtLabel = "12 Nov 2026"
            ),
            VisitorPhoto(
                id = "photo_seed_4",
                placeId = "place_kayutangan",
                userId = "user_siti",
                uploaderName = "Siti Rahmawati",
                fallbackResId = R.drawable.img_destination_cafe,
                caption = "Ngopi santai di kafe bangunan tempo dulu.",
                createdAtLabel = "12 Nov 2026"
            ),
            VisitorPhoto(
                id = "photo_seed_5",
                placeId = "place_bromo",
                userId = "user_nadia",
                uploaderName = "Nadia Putri",
                fallbackResId = R.drawable.img_destination_bromo,
                caption = "Golden sunrise di kaldera Bromo yang tak terlupakan.",
                createdAtLabel = "18 Nov 2026"
            ),
            VisitorPhoto(
                id = "photo_seed_6",
                placeId = "place_waterfall",
                userId = "user_intan",
                uploaderName = "Intan Permata",
                fallbackResId = R.drawable.img_destination_waterfall,
                caption = "Udara sejuk dan gemericik air terjun Coban Rondo.",
                createdAtLabel = "15 Nov 2026"
            ),
            VisitorPhoto(
                id = "photo_seed_7",
                placeId = "place_alun_tugu",
                userId = "user_fajar",
                uploaderName = "Fajar Nugroho",
                fallbackResId = R.drawable.img_destination_alun_tugu,
                caption = "Kolam teratai Alun-Alun Tugu saat matahari pagi.",
                createdAtLabel = "16 Nov 2026"
            )
        )
    )

    override fun getPhotosForPlace(placeId: String): Flow<List<VisitorPhoto>> =
        photos.map { list -> list.filter { it.placeId == placeId } }

    override suspend fun uploadPhoto(
        placeId: String,
        bytes: ByteArray,
        declaredMimeType: String?,
        caption: String
    ): Result<VisitorPhoto> {
        val activeUser = sessionStore.currentUser.value
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu untuk mengunggah foto."))

        val validated = ImageUploadValidator.validate(
            bytes = bytes,
            declaredMimeType = declaredMimeType,
            maxBytes = ImageUploadValidator.MAX_GALLERY_BYTES
        ).getOrElse { return Result.failure(it) }

        val id = "photo_${System.currentTimeMillis()}"
        var localUri: String? = null
        if (cacheDir != null) {
            val outFile = File(cacheDir, "$id.${validated.extension}")
            outFile.writeBytes(validated.bytes)
            localUri = outFile.toURI().toString()
        }

        val created = VisitorPhoto(
            id = id,
            placeId = placeId,
            userId = activeUser.id,
            uploaderName = activeUser.fullName.ifBlank { "Penjelajah" },
            storagePath = "${activeUser.id}/$placeId/$id.${validated.extension}",
            imageUrl = localUri,
            fallbackResId = R.drawable.img_destination_photo,
            caption = caption.trim().take(300),
            createdAtLabel = "Baru saja"
        )
        photos.update { current -> listOf(created) + current }
        return Result.success(created)
    }

    override suspend fun deletePhoto(photo: VisitorPhoto): Result<Unit> {
        val activeUser = sessionStore.currentUser.value
            ?: return Result.failure(IllegalStateException("Silakan masuk terlebih dahulu."))
        if (photo.userId != activeUser.id) {
            return Result.failure(IllegalAccessException("Anda hanya dapat menghapus foto milik sendiri."))
        }
        photos.update { current -> current.filterNot { it.id == photo.id } }
        return Result.success(Unit)
    }
}
