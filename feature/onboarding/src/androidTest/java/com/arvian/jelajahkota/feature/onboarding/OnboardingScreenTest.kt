package com.arvian.jelajahkota.feature.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_displaysFirstSlideAndActions() {
        var skipClicked = false
        var signInClicked = false
        var signUpClicked = false

        composeTestRule.setContent {
            JelajahKotaTheme {
                OnboardingScreen(
                    onSkipClick = { skipClicked = true },
                    onSignInClick = { signInClicked = true },
                    onSignUpClick = { signUpClicked = true }
                )
            }
        }

        // Verify Node 52:702 text elements are displayed
        composeTestRule.onNodeWithText("Lewati").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jelajahi Destinasi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kotamu!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temukan ratusan destinasi wisata menarik di sudut kotamu!").assertIsDisplayed()

        // Verify indicator semantics
        composeTestRule.onNodeWithContentDescription("Halaman 1 dari 3").assertIsDisplayed()

        // Verify CTAs
        composeTestRule.onNodeWithText("Masuk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Daftar Akun").assertIsDisplayed()

        // Test clicks
        composeTestRule.onNodeWithText("Lewati").performClick()
        assertTrue(skipClicked)

        composeTestRule.onNodeWithText("Masuk").performClick()
        assertTrue(signInClicked)

        composeTestRule.onNodeWithText("Daftar Akun").performClick()
        assertTrue(signUpClicked)
    }
}
