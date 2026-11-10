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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: ResetPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        viewModel = ResetPasswordViewModel(fakeAuthRepository, "arvian@jelajahkota.id")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `password shorter than 8 chars fails minLength validation`() {
        viewModel.onNewPasswordChange("Ab1!")
        assertFalse(viewModel.uiState.value.validation.hasMinLength)
        assertFalse(viewModel.uiState.value.validation.isValid)
    }

    @Test
    fun `password without number or symbol fails number validation`() {
        viewModel.onNewPasswordChange("Abcdefgh")
        assertTrue(viewModel.uiState.value.validation.hasMinLength)
        assertTrue(viewModel.uiState.value.validation.hasUpperAndLower)
        assertFalse(viewModel.uiState.value.validation.hasNumber)
        assertFalse(viewModel.uiState.value.validation.isValid)
    }

    @Test
    fun `password satisfying all criteria is valid and succeeds`() = runTest {
        var successCalled = false
        viewModel.onNewPasswordChange("Password123!")
        viewModel.onConfirmPasswordChange("Password123!")

        val validation = viewModel.uiState.value.validation
        assertTrue(validation.hasMinLength)
        assertTrue(validation.hasUpperAndLower)
        assertTrue(validation.hasNumber)
        assertTrue(validation.isMatching)
        assertTrue(validation.isValid)

        viewModel.submitReset { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertTrue(successCalled)
    }

    private class FakeAuthRepository : AuthRepository {
        override fun signIn(email: String, password: String): Flow<Result<User>> = flowOf(Result.success(User(id = "1", fullName = "Arvian", email = email)))
        override fun signUp(fullName: String, email: String, password: String): Flow<Result<User>> = flowOf(Result.success(User(id = "1", fullName = fullName, email = email)))
        override fun sendPasswordResetEmail(email: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun verifyOtp(email: String, otp: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun resetPassword(email: String, newPassword: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override fun signOut(): Flow<Result<Unit>> = flowOf(Result.success(Unit))
    }
}
