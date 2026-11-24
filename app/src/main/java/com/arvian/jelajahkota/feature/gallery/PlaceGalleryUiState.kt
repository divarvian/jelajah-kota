package com.arvian.jelajahkota.feature.gallery

import androidx.compose.runtime.Immutable
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto

@Immutable
data class PlaceGalleryUiState(
    val placeId: String = "",
    val placeName: String = "",
    val photos: List<VisitorPhoto> = emptyList(),
    val currentUser: User? = null,
    val isLoading: Boolean = true,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val actionMessage: String? = null
) {
    val isLoggedIn: Boolean get() = currentUser != null
}
