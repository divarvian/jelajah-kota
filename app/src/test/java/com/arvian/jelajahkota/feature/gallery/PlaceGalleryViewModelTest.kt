package com.arvian.jelajahkota.feature.gallery

import com.arvian.jelajahkota.core.data.repository.AuthRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.GalleryRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.UserSessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceGalleryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sessionStore: UserSessionStore
    private lateinit var viewModel: PlaceGalleryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionStore = UserSessionStore(initialUser = UserSessionStore.DEFAULT_USER)
        viewModel = PlaceGalleryViewModel(
            galleryRepository = GalleryRepositoryImpl(sessionStore),
            placesRepository = PlacesRepositoryImpl(),
            authRepository = AuthRepositoryImpl(sessionStore),
            placeId = "place_jodipan"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uploadPhoto validates bytes adds photo and owner can delete own photo only`() = runTest {
        advanceUntilIdle()
        val initialCount = viewModel.uiState.value.photos.size
        assertTrue(initialCount >= 1)

        // Reject invalid fake image
        viewModel.uploadPhoto("not-an-image".toByteArray(), "image/jpeg", "Fake")
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(initialCount, viewModel.uiState.value.photos.size)

        // Accept real JPEG magic header
        val validJpeg = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00, 0x10)
        viewModel.uploadPhoto(validJpeg, "image/jpeg", "Foto baru di Jodipan")
        advanceUntilIdle()

        assertEquals(initialCount + 1, viewModel.uiState.value.photos.size)
        val uploaded = viewModel.uiState.value.photos.first()
        assertEquals("Foto baru di Jodipan", uploaded.caption)
        assertEquals(UserSessionStore.DEFAULT_USER.id, uploaded.userId)

        // Attempting to delete someone else's seeded photo fails
        val someoneElses = viewModel.uiState.value.photos.last()
        viewModel.deletePhoto(someoneElses)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(initialCount + 1, viewModel.uiState.value.photos.size)

        // Deleting own uploaded photo succeeds
        viewModel.deletePhoto(uploaded)
        advanceUntilIdle()
        assertEquals(initialCount, viewModel.uiState.value.photos.size)
    }
}
