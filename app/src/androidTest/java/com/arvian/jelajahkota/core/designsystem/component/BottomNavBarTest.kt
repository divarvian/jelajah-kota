package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BottomNavBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allFiveLabelsRemainVisibleAndBerandaIsClickableFromEksplorasi() {
        var selectedTab by mutableStateOf(JelajahKotaTab.EKSPLORASI)

        composeTestRule.setContent {
            JelajahKotaTheme {
                JelajahKotaBottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }

        // All 5 labels stay visible even when Eksplorasi is active (Material 3 standard)
        composeTestRule.onNodeWithText("Beranda").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eksplorasi").assertIsDisplayed().assertIsSelected()
        composeTestRule.onNodeWithText("Jadwalkan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()

        // Tapping Beranda from Eksplorasi must switch tab immediately
        composeTestRule.onNodeWithText("Beranda").performClick()
        assertEquals(JelajahKotaTab.BERANDA, selectedTab)
        composeTestRule.onNodeWithText("Beranda").assertIsSelected()
    }
}
