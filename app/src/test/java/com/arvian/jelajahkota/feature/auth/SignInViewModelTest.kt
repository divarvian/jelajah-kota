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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: SignInViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        viewModel = SignInViewModel(fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.rememberMe)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
    }

    @Test
    fun `onEmailChange and onPasswordChange updates state`() {
        viewModel.onEmailChange("test@jelajahkota.id")
        viewModel.onPasswordChange("password123")
        viewModel.onRememberMeChange(true)

        val state = viewModel.uiState.value
        assertEquals("test@jelajahkota.id", state.email)
        assertEquals("password123", state.password)
        assertTrue(state.rememberMe)
    }

    @Test
    fun `signIn with blank or invalid email sets errorMessage`() {
        viewModel.signIn()
        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onEmailChange("bukan-email")
        viewModel.onPasswordChange("password123")
        viewModel.signIn()
        assertEquals("Masukkan alamat email yang valid.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `signIn with valid credentials succeeds`() = runTest {
        viewModel.onEmailChange("arvian@jelajahkota.id")
        viewModel.onPasswordChange("password123")

        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `signIn failure surfaces error message`() = runTest {
        fakeAuthRepository.shouldFail = true
        viewModel.onEmailChange("arvian@jelajahkota.id")
        viewModel.onPasswordChange("password123")

        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals("Kredensial salah", viewModel.uiState.value.errorMessage)
    }

    private class FakeAuthRepository : AuthRepository {
        var shouldFail = false
        override suspend fun signIn(email: String, password: String): Result<User> =
            if (shouldFail) Result.failure(IllegalStateException("Kredensial salah"))
            else Result.success(User(id = "1", fullName = "Arvian", email = email))

        override suspend fun signUp(fullName: String, email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = fullName, email = email))

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun verifyOtp(email: String, otp: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = Result.success(Unit)
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    }
}
