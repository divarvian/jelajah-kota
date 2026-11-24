package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPhotosForPlace(placeId: String): Flow<List<VisitorPhoto>>
    suspend fun uploadPhoto(
        placeId: String,
        bytes: ByteArray,
        declaredMimeType: String?,
        caption: String
    ): Result<VisitorPhoto>
    suspend fun deletePhoto(photo: VisitorPhoto): Result<Unit>
}
