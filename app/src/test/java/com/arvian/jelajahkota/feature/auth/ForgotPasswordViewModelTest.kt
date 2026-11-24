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
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(FakeAuthRepository())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invalid email sets validation error`() {
        viewModel.onEmailChange("not-an-email")
        viewModel.sendResetLink()

        assertEquals("Masukkan alamat email yang valid.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `valid email succeeds and trims whitespace`() = runTest {
        viewModel.onEmailChange("  penjelajah@jelajahkota.id  ")
        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertEquals("penjelajah@jelajahkota.id", viewModel.uiState.value.email)
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
