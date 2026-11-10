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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        viewModel = SignUpViewModel(fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signUp with mismatched passwords sets errorMessage`() {
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("habib@jelajahkota.id")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Different123!")
        viewModel.onAgreeToTermsChange(true)

        viewModel.signUp {}

        assertEquals("Konfirmasi kata sandi tidak cocok.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `signUp without agreeing to terms sets errorMessage`() {
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("habib@jelajahkota.id")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Secret123!")
        viewModel.onAgreeToTermsChange(false)

        viewModel.signUp {}

        assertEquals("Anda harus menyetujui Syarat Layanan dan Kebijakan Privasi.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `signUp with valid data succeeds`() = runTest {
        var successCalled = false
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("habib@jelajahkota.id")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Secret123!")
        viewModel.onAgreeToTermsChange(true)

        viewModel.signUp { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertTrue(successCalled)
    }

    private class FakeAuthRepository : AuthRepository {
        override fun signIn(email: String, password: String): Flow<Result<User>> = flowOf(Result.success(User(id = "1", fullName = "Habib", email = email)))
        override fun signUp(fullName: String, email: String, password: String): Flow<Result<User>> = flowOf(Result.success(User(id = "1", fullName = fullName, email = email)))
        override fun sendPasswordResetEmail(email: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun verifyOtp(email: String, otp: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun resetPassword(email: String, newPassword: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override fun signOut(): Flow<Result<Unit>> = flowOf(Result.success(Unit))
    }
}
