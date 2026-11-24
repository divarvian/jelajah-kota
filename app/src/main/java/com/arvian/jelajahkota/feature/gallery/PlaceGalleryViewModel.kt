package com.arvian.jelajahkota.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.GalleryRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaceGalleryViewModel(
    private val galleryRepository: GalleryRepository,
    private val placesRepository: PlacesRepository,
    private val authRepository: AuthRepository,
    private val placeId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceGalleryUiState(placeId = placeId, isLoading = true))
    val uiState: StateFlow<PlaceGalleryUiState> = _uiState.asStateFlow()

    init {
        observeGallery()
    }

    private fun observeGallery() {
        viewModelScope.launch {
            combine(
                galleryRepository.getPhotosForPlace(placeId),
                placesRepository.getPlaceById(placeId),
                authRepository.getCurrentUser()
            ) { photos, place, user ->
                Triple(photos, place?.name.orEmpty(), user)
            }.collect { (photos, placeName, user) ->
                _uiState.update { current ->
                    current.copy(
                        placeName = placeName,
                        photos = photos,
                        currentUser = user,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun uploadPhoto(bytes: ByteArray, declaredMimeType: String?, caption: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null) }
            val result = galleryRepository.uploadPhoto(
                placeId = placeId,
                bytes = bytes,
                declaredMimeType = declaredMimeType,
                caption = caption
            )
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            actionMessage = "Foto berhasil diunggah ke galeri pengunjung!"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            errorMessage = error.message ?: "Gagal mengunggah foto."
                        )
                    }
                }
            )
        }
    }

    fun deletePhoto(photo: VisitorPhoto) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            val result = galleryRepository.deletePhoto(photo)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(actionMessage = "Foto berhasil dihapus.") }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Gagal menghapus foto.")
                    }
                }
            )
        }
    }

    fun reportError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    class Factory(
        private val galleryRepository: GalleryRepository,
        private val placesRepository: PlacesRepository,
        private val authRepository: AuthRepository,
        private val placeId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlaceGalleryViewModel(galleryRepository, placesRepository, authRepository, placeId) as T
        }
    }
}
