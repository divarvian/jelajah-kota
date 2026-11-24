package com.arvian.jelajahkota.feature.auth

import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpVerificationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OtpVerificationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OtpVerificationViewModel(FakeAuthRepository(), "penjelajah@jelajahkota.id")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onDigitChange filters non digits`() {
        viewModel.onDigitChange(0, "a")
        assertEquals("", viewModel.uiState.value.otpValues[0])

        viewModel.onDigitChange(0, "7")
        assertEquals("7", viewModel.uiState.value.otpValues[0])
    }

    @Test
    fun `verifyCode fails when incomplete`() {
        viewModel.onDigitChange(0, "1")
        viewModel.onDigitChange(1, "2")
        viewModel.verifyCode()

        assertEquals("Silakan masukkan 4 digit kode OTP.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isVerified)
    }

    @Test
    fun `verifyCode succeeds with 4 digits`() = runTest {
        viewModel.onDigitChange(0, "1")
        viewModel.onDigitChange(1, "2")
        viewModel.onDigitChange(2, "3")
        viewModel.onDigitChange(3, "4")

        viewModel.verifyCode()
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.isVerified)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun signIn(email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = "User", email = email))

        override suspend fun signUp(fullName: String, email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = fullName, email = email))

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun verifyOtp(email: String, otp: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = Result.success(Unit)
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    }
}
