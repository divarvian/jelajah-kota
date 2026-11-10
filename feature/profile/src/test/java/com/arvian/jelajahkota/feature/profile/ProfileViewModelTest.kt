package com.arvian.jelajahkota.feature.profile

import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
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
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeProfileRepository: FakeProfileRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeProfileRepository = FakeProfileRepository()
        fakeAuthRepository = FakeAuthRepository()
        viewModel = ProfileViewModel(fakeProfileRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial profile is loaded from repository`() {
        val state = viewModel.uiState.value
        assertEquals("Test User", state.user.fullName)
        assertEquals("test@jelajahkota.id", state.user.email)
    }

    @Test
    fun `form fields update state correctly`() {
        viewModel.onFullNameChange("New Name")
        viewModel.onBioChange("New Bio")
        viewModel.onCityChange("Malang")
        viewModel.onPhoneChange("08123456789")

        val state = viewModel.uiState.value
        assertEquals("New Name", state.user.fullName)
        assertEquals("New Bio", state.user.bio)
        assertEquals("Malang", state.user.city)
        assertEquals("08123456789", state.user.phone)
    }

    @Test
    fun `toggles update preferences correctly`() {
        viewModel.toggleLocation(false)
        viewModel.toggleDarkMode(true)
        viewModel.togglePushNotifications(false)
        viewModel.toggleEmailNotifications(true)

        val state = viewModel.uiState.value
        assertFalse(state.locationEnabled)
        assertTrue(state.darkModeEnabled)
        assertFalse(state.pushNotificationsEnabled)
        assertTrue(state.emailNotificationsEnabled)
    }

    @Test
    fun `saveProfile triggers repository and calls onSuccess`() = runTest {
        var successCalled = false
        viewModel.onFullNameChange("Updated Name")
        viewModel.saveProfile { successCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successCalled)
        assertTrue(viewModel.uiState.value.saveSuccess)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `uploadAvatar updates avatarUrl and calls onSuccess`() = runTest {
        var successCalled = false
        val newUri = "content://media/avatar_test.png"
        viewModel.uploadAvatar(newUri) { successCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals(newUri, viewModel.uiState.value.user.avatarUrl)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `signOut triggers authRepository and calls callback`() = runTest {
        var signOutCalled = false
        viewModel.signOut { signOutCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(signOutCalled)
    }

    private class FakeProfileRepository : ProfileRepository {
        var currentUser = User(
            id = "test_user_id",
            fullName = "Test User",
            email = "test@jelajahkota.id",
            phone = "08123456789",
            city = "Malang",
            bio = "Traveller"
        )

        override fun getProfile(): Flow<User> = flowOf(currentUser)

        override fun updateProfile(
            fullName: String,
            phone: String,
            city: String,
            bio: String
        ): Flow<Result<Boolean>> {
            currentUser = currentUser.copy(fullName = fullName, phone = phone, city = city, bio = bio)
            return flowOf(Result.success(true))
        }

        override fun updateAvatar(avatarUri: String): Flow<Result<Boolean>> {
            currentUser = currentUser.copy(avatarUrl = avatarUri)
            return flowOf(Result.success(true))
        }
    }

    private class FakeAuthRepository : AuthRepository {
        override fun signIn(email: String, password: String): Flow<Result<User>> =
            flowOf(Result.success(User(id = "1", fullName = "Test", email = email)))
        override fun signUp(fullName: String, email: String, password: String): Flow<Result<User>> =
            flowOf(Result.success(User(id = "1", fullName = fullName, email = email)))
        override fun sendPasswordResetEmail(email: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun verifyOtp(email: String, otp: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun resetPassword(email: String, newPassword: String): Flow<Result<Boolean>> = flowOf(Result.success(true))
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override fun signOut(): Flow<Result<Unit>> = flowOf(Result.success(Unit))
    }
}
