package com.arvian.jelajahkota.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthFlowScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signInScreen_rootModeHidesBackShowsGuestOptionAndAcceptsEmailInput() {
        var guestClicked = false
        var typedEmail = ""

        composeTestRule.setContent {
            JelajahKotaTheme {
                var state by remember { mutableStateOf(SignInUiState()) }
                SignInScreen(
                    uiState = state,
                    canNavigateBack = false,
                    onEmailChange = {
                        typedEmail = it
                        state = state.copy(email = it)
                    },
                    onPasswordChange = { state = state.copy(password = it) },
                    onRememberMeChange = { state = state.copy(rememberMe = it) },
                    onSignInClick = {},
                    onNavigateBack = {},
                    onNavigateToSignUp = {},
                    onNavigateToForgotPassword = {},
                    onContinueAsGuest = { guestClicked = true }
                )
            }
        }

        // Back button must NOT exist when SignIn is root (prevents popping root entry)
        composeTestRule.onNodeWithContentDescription("Kembali").assertDoesNotExist()

        // Guest escape hatch is visible at root
        composeTestRule.onNodeWithText("Lanjutkan sebagai Tamu").assertIsDisplayed()

        // Typing into email field visibly updates UI text
        composeTestRule.onNodeWithText("nama@email.com").performTextInput("malang@jelajahkota.id")
        assertEquals("malang@jelajahkota.id", typedEmail)
        composeTestRule.onNodeWithText("malang@jelajahkota.id").assertIsDisplayed()

        composeTestRule.onNodeWithText("Lanjutkan sebagai Tamu").performClick()
        assertTrue(guestClicked)
    }

    @Test
    fun signInScreen_modalModeShowsBackButtonAndHidesGuestButton() {
        var backClicked = false

        composeTestRule.setContent {
            JelajahKotaTheme {
                SignInScreen(
                    uiState = SignInUiState(),
                    canNavigateBack = true,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onRememberMeChange = {},
                    onSignInClick = {},
                    onNavigateBack = { backClicked = true },
                    onNavigateToSignUp = {},
                    onNavigateToForgotPassword = {},
                    onContinueAsGuest = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Kembali").assertIsDisplayed().performClick()
        assertTrue(backClicked)
        composeTestRule.onNodeWithText("Lanjutkan sebagai Tamu").assertDoesNotExist()
    }

    @Test
    fun otpScreen_displays6DigitPromptAndChangeEmailAction() {
        var backClicked = false
        composeTestRule.setContent {
            JelajahKotaTheme {
                OtpVerificationScreen(
                    uiState = OtpVerificationUiState(email = "penjelajah@jelajahkota.id"),
                    onDigitChange = { _, _ -> },
                    onVerifyClick = {},
                    onResendClick = {},
                    onNavigateBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Kode Verifikasi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ganti Email").assertIsDisplayed().performClick()
        assertTrue(backClicked)
    }
}
