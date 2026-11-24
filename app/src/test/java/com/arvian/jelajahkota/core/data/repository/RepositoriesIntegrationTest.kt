package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.domain.model.Review
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoriesIntegrationTest {

    @Test
    fun `placesRepository searchPlaces handles jakarta and semarang accurately`() = runTest {
        val repo = PlacesRepositoryImpl()

        val jakartaResults = repo.searchPlaces(query = "", cityId = "jkt", categoryId = null).first()
        assertEquals(0, jakartaResults.size)

        val semarangResults = repo.searchPlaces(query = "", cityId = "semarang", categoryId = null).first()
        assertEquals(1, semarangResults.size)
        assertEquals("place_kota_lama", semarangResults.first().id)

        val malangHeritage = repo.searchPlaces(query = "", cityId = "malang", categoryId = "cat_heritage").first()
        assertEquals(1, malangHeritage.size)
        assertEquals("place_kayutangan", malangHeritage.first().id)
    }

    @Test
    fun `plansRepository seed plan matches actual place id and details`() = runTest {
        val placesRepo = PlacesRepositoryImpl()
        val plansRepo = PlansRepositoryImpl()

        val firstPlan = plansRepo.getPlans().first().first()
        val linkedPlace = placesRepo.getPlaceById(firstPlan.placeId).first()

        assertNotNull(linkedPlace)
        assertEquals(linkedPlace?.name, firstPlan.placeName)
        assertEquals(linkedPlace?.imageResId, firstPlan.imageResId)
    }

    @Test
    fun `reviewsRepository filters by placeId updates metrics and supports edit delete`() = runTest {
        val repo = ReviewsRepositoryImpl()

        val bromoBefore = repo.getReviewsForPlace("place_bromo").first()
        val result = repo.submitReview(
            Review(
                id = "rev_new",
                placeId = "place_bromo",
                userId = "user_tester",
                authorName = "Tester",
                rating = 4.0,
                date = "Baru saja",
                content = "Pemandangan sunrise sangat indah."
            )
        )
        assertTrue(result.isSuccess)

        val bromoAfter = repo.getReviewsForPlace("place_bromo").first()
        val metricsAfter = repo.getReviewMetricsForPlace("place_bromo").first()
        assertEquals(bromoBefore.size + 1, bromoAfter.size)
        assertEquals(bromoAfter.size, metricsAfter.totalReviews)
        assertEquals(4.5, metricsAfter.overallRating, 0.01)

        val byUser = repo.getReviewsByUser("user_tester").first()
        assertEquals(1, byUser.size)

        val updated = repo.updateReview("rev_new", 5.0, "Sunrise terbaik di Jawa Timur!")
        assertTrue(updated.isSuccess)
        assertEquals("Sunrise terbaik di Jawa Timur!", repo.getReviewsByUser("user_tester").first().first().content)

        val deleted = repo.deleteReview("rev_new")
        assertTrue(deleted.isSuccess)
        assertEquals(0, repo.getReviewsByUser("user_tester").first().size)
    }

    @Test
    fun `authRepository and profileRepository start in Guest mode and share UserSessionStore`() = runTest {
        val sessionStore = UserSessionStore()
        val authRepo = AuthRepositoryImpl(sessionStore)
        val profileRepo = ProfileRepositoryImpl(sessionStore)

        // Starts cleanly as Guest (null)
        assertNull(authRepo.getCurrentUser().first())
        assertNull(profileRepo.getProfile().first())

        val signUpResult = authRepo.signUp("Rina Wulandari", "rina@jelajahkota.id", "Secret123!")
        assertTrue(signUpResult.isSuccess)
        assertEquals("Rina Wulandari", profileRepo.getProfile().first()?.fullName)

        val updateResult = profileRepo.updateProfile(
            fullName = "Rina Malang",
            phone = "+62 811-2233-4455",
            city = "Kota Batu",
            bio = "Suka wisata alam",
            birthDate = "12/04/1999",
            gender = "Perempuan"
        )
        assertTrue(updateResult.isSuccess)
        assertEquals("Rina Malang", authRepo.getCurrentUser().first()?.fullName)
        assertEquals("+6281122334455", profileRepo.getProfile().first()?.phone)
        assertEquals("1999-04-12", profileRepo.getProfile().first()?.birthDate)
        assertEquals("female", profileRepo.getProfile().first()?.gender)

        // 6-digit OTP validation
        assertTrue(authRepo.verifyOtp("rina@jelajahkota.id", "1234").isFailure)
        assertTrue(authRepo.verifyOtp("rina@jelajahkota.id", "123456").isSuccess)

        authRepo.signOut()
        assertNull(authRepo.getCurrentUser().first())
        assertNull(profileRepo.getProfile().first())
    }

    @Test
    fun `favoritesRepository toggles favorite id reactively`() = runTest {
        val placesRepo = PlacesRepositoryImpl()
        val favRepo = FavoritesRepositoryImpl(placesRepo)

        assertFalse(favRepo.isFavorite("place_bromo").first())
        assertTrue(favRepo.toggleFavorite("place_bromo"))
        assertTrue(favRepo.isFavorite("place_bromo").first())

        assertFalse(favRepo.toggleFavorite("place_bromo"))
        assertFalse(favRepo.isFavorite("place_bromo").first())
    }
}
