package com.arvian.jelajahkota.feature.profile

import com.arvian.jelajahkota.core.domain.model.AppSettings
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.Review
import com.arvian.jelajahkota.core.domain.model.ReviewMetrics
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.model.VisitPlan
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    private lateinit var fakePreferencesRepository: FakePreferencesRepo
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePreferencesRepository = FakePreferencesRepo()
        viewModel = ProfileViewModel(
            profileRepository = FakeProfileRepo(),
            authRepository = FakeAuthRepo(),
            preferencesRepository = fakePreferencesRepository,
            favoritesRepository = FakeFavoritesRepo(),
            plansRepository = FakePlansRepo(),
            reviewsRepository = FakeReviewsRepo()
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial profile and dynamic counters are loaded from repositories`() {
        val state = viewModel.uiState.value
        assertEquals("Test User", state.user.fullName)
        assertEquals("test@jelajahkota.id", state.user.email)
        assertEquals(2, state.favoritesCount)
        assertEquals(1, state.plansCount)
        assertEquals(3, state.reviewsCount)
    }

    @Test
    fun `form fields including birthDate and gender update state correctly`() {
        viewModel.onFullNameChange("New Name")
        viewModel.onBioChange("New Bio")
        viewModel.onCityChange("Malang")
        viewModel.onPhoneChange("08123456789")
        viewModel.onBirthDateChange("10/10/1998")
        viewModel.onGenderChange("Perempuan")

        val state = viewModel.uiState.value
        assertEquals("New Name", state.user.fullName)
        assertEquals("New Bio", state.user.bio)
        assertEquals("Malang", state.user.city)
        assertEquals("08123456789", state.user.phone)
        assertEquals("10/10/1998", state.user.birthDate)
        assertEquals("Perempuan", state.user.gender)
    }

    @Test
    fun `toggles persist preferences to UserPreferencesRepository`() = runTest {
        viewModel.toggleLocation(false)
        viewModel.toggleDarkMode(true)
        viewModel.togglePushNotifications(false)
        viewModel.toggleEmailNotifications(true)
        testDispatcher.scheduler.advanceUntilIdle()

        val savedSettings = fakePreferencesRepository.observeAppSettings().first()
        assertFalse(savedSettings.locationEnabled)
        assertTrue(savedSettings.darkModeEnabled)
        assertFalse(savedSettings.pushNotificationsEnabled)
        assertTrue(savedSettings.emailNotificationsEnabled)
    }

    @Test
    fun `saveProfile uploadAvatar and signOut update UiState flags`() = runTest {
        viewModel.onFullNameChange("Updated Name")
        viewModel.saveProfile()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.saveSuccess)
        assertFalse(viewModel.uiState.value.isSaving)

        val newUri = "local://avatar_test.webp"
        viewModel.uploadAvatar(newUri)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.avatarUpdated)
        assertEquals(newUri, viewModel.uiState.value.user.avatarUrl)

        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.signedOut)
    }

    private class FakeProfileRepo : ProfileRepository {
        private val userFlow = MutableStateFlow(User(id = "1", fullName = "Test User", email = "test@jelajahkota.id"))
        override fun getProfile(): Flow<User> = userFlow
        override suspend fun updateProfile(
            fullName: String,
            phone: String,
            city: String,
            bio: String,
            birthDate: String,
            gender: String
        ): Result<User> {
            val updated = userFlow.value.copy(
                fullName = fullName,
                phone = phone,
                city = city,
                bio = bio,
                birthDate = birthDate,
                gender = gender
            )
            userFlow.value = updated
            return Result.success(updated)
        }

        override suspend fun updateAvatar(avatarUri: String): Result<User> {
            val updated = userFlow.value.copy(avatarUrl = avatarUri)
            userFlow.value = updated
            return Result.success(updated)
        }
    }

    private class FakeAuthRepo : AuthRepository {
        override suspend fun signIn(email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = "Test", email = email))

        override suspend fun signUp(fullName: String, email: String, password: String): Result<User> =
            Result.success(User(id = "1", fullName = fullName, email = email))

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun verifyOtp(email: String, otp: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = Result.success(Unit)
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    }

    private class FakePreferencesRepo : UserPreferencesRepository {
        private val settings = MutableStateFlow(AppSettings())
        override fun isOnboardingCompleted(): Flow<Boolean> = flowOf(true)
        override suspend fun setOnboardingCompleted(completed: Boolean) {}
        override fun observeAppSettings(): Flow<AppSettings> = settings
        override suspend fun setLocationEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(locationEnabled = enabled)
        }

        override suspend fun setDarkModeEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(darkModeEnabled = enabled)
        }

        override suspend fun setPushNotificationsEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(pushNotificationsEnabled = enabled)
        }

        override suspend fun setEmailNotificationsEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(emailNotificationsEnabled = enabled)
        }
    }

    private class FakeFavoritesRepo : FavoritesRepository {
        override fun getFavoritePlaceIds(): Flow<Set<String>> = flowOf(setOf("p1", "p2"))
        override fun getFavoritePlaces(): Flow<List<Place>> = flowOf(emptyList())
        override fun isFavorite(placeId: String): Flow<Boolean> = flowOf(false)
        override suspend fun toggleFavorite(placeId: String): Boolean = true
    }

    private class FakePlansRepo : PlansRepository {
        override fun getPlans(): Flow<List<VisitPlan>> = flowOf(
            listOf(VisitPlan("pl1", "p1", "Place", "Malang", 0, "Sen", "Sel", 2, 2))
        )

        override fun getPlanById(planId: String): Flow<VisitPlan?> = flowOf(null)
        override suspend fun savePlan(plan: VisitPlan): Result<VisitPlan> = Result.success(plan)
        override suspend fun deletePlan(planId: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeReviewsRepo : ReviewsRepository {
        private val sample = listOf(
            Review("r1", "p1", "A", 0, 5.0, "now", "good"),
            Review("r2", "p1", "B", 0, 4.0, "now", "ok"),
            Review("r3", "p2", "C", 0, 5.0, "now", "great")
        )

        override fun getReviewsForPlace(placeId: String): Flow<List<Review>> = flowOf(sample)
        override fun getAllReviews(): Flow<List<Review>> = flowOf(sample)
        override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> =
            flowOf(ReviewMetrics(4.7, 3, 4.7, 4.7, 4.7, 4.7, 4.7, 4.7))

        override suspend fun submitReview(review: Review): Result<Review> = Result.success(review)
    }
}
