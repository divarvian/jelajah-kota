package com.arvian.jelajahkota.feature.places

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.domain.model.Place
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ExploreAndDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val samplePlace = Place(
        id = "place_jodipan",
        name = "Kampung Warna-Warni Jodipan",
        location = "Blimbing, Kota Malang",
        city = "Malang",
        rating = 4.7,
        reviewCount = 2120,
        imageResId = R.drawable.img_destination_jodipan,
        description = "Permukiman mural warna-warni di tepi Sungai Brantas Kota Malang.",
        ticketPrice = "Rp 5.000 / orang",
        ticketPriceAmount = 5000,
        category = "Wisata Kota"
    )

    @Test
    fun exploreScreen_rendersFilterChipsAndDestinationCard() {
        var clickedPlaceId = ""

        composeTestRule.setContent {
            JelajahKotaTheme {
                ExploreScreen(
                    uiState = ExploreUiState(
                        allPlaces = listOf(samplePlace),
                        availableCities = listOf("Semua", "Malang"),
                        availableCategories = listOf("Semua", "Wisata Kota"),
                        isLoading = false
                    ),
                    onSearchQueryChange = {},
                    onCitySelected = {},
                    onCategorySelected = {},
                    onSortOptionSelected = {},
                    onToggleView = {},
                    onResetFilters = {},
                    onPlaceClick = { clickedPlaceId = it.id }
                )
            }
        }

        composeTestRule.onNodeWithText("Eksplorasi Destinasi").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 Destinasi Ditemukan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kampung Warna-Warni Jodipan").assertIsDisplayed().performClick()
        assertTrue(clickedPlaceId == "place_jodipan")
    }

    @Test
    fun placeDetailScreen_displaysGalleryAndPlanCta() {
        var galleryOpened = false
        var favoriteToggled = false
        var planClicked = false

        composeTestRule.setContent {
            JelajahKotaTheme(darkTheme = true) {
                PlaceDetailScreen(
                    uiState = PlaceDetailUiState(
                        place = samplePlace,
                        isFavorite = false,
                        isLoggedIn = true,
                        reviewsCount = 2,
                        isLoading = false
                    ),
                    onNavigateBack = {},
                    onToggleFavorite = { favoriteToggled = true },
                    onToggleDescription = {},
                    onShareClick = {},
                    onPlanClick = { planClicked = true },
                    onNavigateToReviews = {},
                    onNavigateToGallery = { galleryOpened = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Kampung Warna-Warni Jodipan").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Tambah ke favorit").performClick()
        assertTrue(favoriteToggled)

        composeTestRule.onNodeWithText("Rencanakan").assertIsDisplayed().performClick()
        assertTrue(planClicked)

        composeTestRule.onNodeWithText("Galeri Pengunjung >").performClick()
        assertTrue(galleryOpened)
    }
}
