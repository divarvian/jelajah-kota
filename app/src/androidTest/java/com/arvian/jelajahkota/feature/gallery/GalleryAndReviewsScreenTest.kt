package com.arvian.jelajahkota.feature.gallery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GalleryAndReviewsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun placeGalleryScreen_displaysEmptyStateAndUploadFab() {
        var uploadClicked = false

        composeTestRule.setContent {
            JelajahKotaTheme {
                PlaceGalleryScreen(
                    uiState = PlaceGalleryUiState(
                        placeId = "place_jodipan",
                        placeName = "Kampung Warna-Warni Jodipan",
                        photos = emptyList(),
                        isLoading = false
                    ),
                    onNavigateBack = {},
                    onUploadClick = { uploadClicked = true },
                    onDeletePhoto = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Belum Ada Foto Pengunjung").assertIsDisplayed()
        composeTestRule.onNodeWithText("Unggah Foto").assertIsDisplayed().performClick()
        assertTrue(uploadClicked)
    }

    @Test
    fun placeGalleryScreen_displaysPhotoCards() {
        composeTestRule.setContent {
            JelajahKotaTheme(darkTheme = true) {
                PlaceGalleryScreen(
                    uiState = PlaceGalleryUiState(
                        placeId = "place_jodipan",
                        placeName = "Kampung Warna-Warni Jodipan",
                        currentUser = User(id = "u1", fullName = "Arvian", email = "arvian@jelajahkota.id"),
                        photos = listOf(
                            VisitorPhoto(
                                id = "p1",
                                placeId = "place_jodipan",
                                userId = "u1",
                                uploaderName = "Arvian",
                                fallbackResId = R.drawable.img_destination_jodipan,
                                caption = "Pagi cerah di Jodipan"
                            )
                        ),
                        isLoading = false
                    ),
                    onNavigateBack = {},
                    onUploadClick = {},
                    onDeletePhoto = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Arvian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pagi cerah di Jodipan").assertIsDisplayed()
    }
}
