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
    fun `signUp with invalid email sets errorMessage`() {
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Secret123!")
        viewModel.onAgreeToTermsChange(true)

        viewModel.signUp()

        assertEquals("Masukkan alamat email yang valid.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `signUp with mismatched passwords sets errorMessage`() {
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("habib@jelajahkota.id")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Different123!")
        viewModel.onAgreeToTermsChange(true)

        viewModel.signUp()

        assertEquals("Konfirmasi kata sandi tidak cocok.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `signUp without agreeing to terms sets errorMessage`() {
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("habib@jelajahkota.id")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Secret123!")
        viewModel.onAgreeToTermsChange(false)

        viewModel.signUp()

        assertEquals("Anda harus menyetujui Syarat Layanan dan Kebijakan Privasi.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `signUp with valid data succeeds`() = runTest {
        viewModel.onFullNameChange("Habib Ahmad")
        viewModel.onEmailChange("habib@jelajahkota.id")
        viewModel.onPasswordChange("Secret123!")
        viewModel.onConfirmPasswordChange("Secret123!")
        viewModel.onAgreeToTermsChange(true)

        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun signIn(email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = "Habib", email = email))

        override suspend fun signUp(fullName: String, email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = fullName, email = email))

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun verifyOtp(email: String, otp: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = Result.success(Unit)
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    }
}
