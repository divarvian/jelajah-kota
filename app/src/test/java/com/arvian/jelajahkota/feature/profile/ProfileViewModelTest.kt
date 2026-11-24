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
import kotlinx.coroutines.flow.map
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
    fun `initial profile and user-owned counters are loaded from repositories`() {
        val state = viewModel.uiState.value
        assertTrue(state.isLoggedIn)
        assertEquals("Test User", state.user?.fullName)
        assertEquals("test@jelajahkota.id", state.user?.email)
        assertEquals(2, state.favoritesCount)
        assertEquals(1, state.plansCount)
        // Only reviews owned by user "1" are counted (2 out of 3 total reviews)
        assertEquals(2, state.reviewsCount)
    }

    @Test
    fun `draft form fields including birthDate and gender update state correctly`() {
        viewModel.onFullNameChange("New Name")
        viewModel.onBioChange("New Bio")
        viewModel.onCityChange("Kota Malang")
        viewModel.onPhoneChange("08123456789")
        viewModel.onBirthDateChange("1998-10-10")
        viewModel.onGenderChange("Perempuan")

        val state = viewModel.uiState.value
        assertEquals("New Name", state.draftFullName)
        assertEquals("New Bio", state.draftBio)
        assertEquals("Kota Malang", state.draftCity)
        assertEquals("08123456789", state.draftPhone)
        assertEquals("1998-10-10", state.draftBirthDate)
        assertEquals("female", state.draftGender)
    }

    @Test
    fun `saveProfile blocks invalid phone and future birthDate with inline fieldErrors`() = runTest {
        viewModel.onPhoneChange("123")
        viewModel.onBirthDateChange("2099-01-01")
        viewModel.saveProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.saveSuccess)
        assertTrue(state.fieldErrors.phoneError != null)
        assertTrue(state.fieldErrors.birthDateError != null)
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
        assertEquals("Updated Name", viewModel.uiState.value.user?.fullName)
        assertFalse(viewModel.uiState.value.isSaving)

        val newUri = "local://avatar_test.webp"
        viewModel.uploadAvatar(newUri)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.avatarUpdated)
        assertEquals(newUri, viewModel.uiState.value.user?.avatarUrl)

        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.signedOut)
    }

    private class FakeProfileRepo : ProfileRepository {
        private val userFlow = MutableStateFlow<User?>(
            User(id = "1", fullName = "Test User", email = "test@jelajahkota.id")
        )

        override fun getProfile(): Flow<User?> = userFlow
        override suspend fun updateProfile(
            fullName: String,
            phone: String,
            city: String,
            bio: String,
            birthDate: String,
            gender: String
        ): Result<User> {
            val current = userFlow.value ?: return Result.failure(IllegalStateException("Guest"))
            val updated = current.copy(
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
            val current = userFlow.value ?: return Result.failure(IllegalStateException("Guest"))
            val updated = current.copy(avatarUrl = avatarUri)
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
        private val rememberedEmail = MutableStateFlow<String?>(null)
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

        override fun getRememberedEmail(): Flow<String?> = rememberedEmail
        override suspend fun setRememberedEmail(email: String?) {
            rememberedEmail.value = email
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
            listOf(
                VisitPlan(
                    id = "pl1",
                    placeId = "p1",
                    placeName = "Place",
                    placeLocation = "Malang",
                    imageResId = 0,
                    startDate = "Sen",
                    endDate = "Sel",
                    durationDays = 2,
                    visitorCount = 2
                )
            )
        )

        override fun getPlanById(planId: String): Flow<VisitPlan?> = flowOf(null)
        override suspend fun savePlan(plan: VisitPlan): Result<VisitPlan> = Result.success(plan)
        override suspend fun deletePlan(planId: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeReviewsRepo : ReviewsRepository {
        private val sample = listOf(
            Review(id = "r1", placeId = "p1", userId = "1", authorName = "Test User", rating = 5.0, date = "now", content = "good"),
            Review(id = "r2", placeId = "p1", userId = "other", authorName = "B", rating = 4.0, date = "now", content = "ok"),
            Review(id = "r3", placeId = "p2", userId = "1", authorName = "Test User", rating = 5.0, date = "now", content = "great")
        )

        override fun getReviewsForPlace(placeId: String): Flow<List<Review>> = flowOf(sample)
        override fun getAllReviews(): Flow<List<Review>> = flowOf(sample)
        override fun getReviewsByUser(userId: String): Flow<List<Review>> =
            flowOf(sample).map { list -> list.filter { it.userId == userId } }

        override fun getReviewMetricsForPlace(placeId: String): Flow<ReviewMetrics> =
            flowOf(ReviewMetrics.fromReviews(sample))

        override suspend fun submitReview(review: Review): Result<Review> = Result.success(review)
        override suspend fun updateReview(reviewId: String, rating: Double, content: String): Result<Review> =
            Result.success(sample.first().copy(rating = rating, content = content))

        override suspend fun deleteReview(reviewId: String): Result<Unit> = Result.success(Unit)
    }
}
