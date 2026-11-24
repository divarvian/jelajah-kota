package com.arvian.jelajahkota.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationDistanceTest {

    @Test
    fun haversineDistance_betweenKayutanganAndJodipan_isAboutOneKilometer() {
        val jodipan = Place(
            id = "place_jodipan",
            name = "Jodipan",
            location = "Malang",
            rating = 4.8,
            reviewCount = 2,
            imageResId = 0,
            latitude = -7.983010,
            longitude = 112.637510
        )

        // Coordinates of Kayutangan Heritage, Malang
        val km = jodipan.distanceKmFrom(-7.977810, 112.630510)
        assertNotNull(km)
        assertTrue("Expected ~0.96 km, got $km", km!! in 0.7..1.3)
        assertTrue(Place.formatDistanceLabel(km).endsWith("m") || Place.formatDistanceLabel(km).endsWith("km"))
    }

    @Test
    fun formatDistanceAndDurationRange_produceIndonesianLabels() {
        assertEquals("500 m", Place.formatDistanceLabel(0.5))
        assertEquals("2,4 km", Place.formatDistanceLabel(2.42))
        assertEquals("18 km", Place.formatDistanceLabel(18.7))

        assertEquals("1-2 Jam", Place.formatDurationRange(60, 120, ""))
        assertEquals("45 Menit", Place.formatDurationRange(45, 45, ""))
        assertEquals("2-3 Jam", Place.formatDurationRange(null, null, "2-3 Jam"))
    }

    @Test
    fun reviewMetricsFromReviews_computesHonestStarDistribution() {
        val reviews = listOf(
            Review(id = "1", placeId = "p1", authorName = "A", rating = 5.0, date = "now", content = "ok"),
            Review(id = "2", placeId = "p1", authorName = "B", rating = 5.0, date = "now", content = "ok"),
            Review(id = "3", placeId = "p1", authorName = "C", rating = 4.0, date = "now", content = "ok"),
            Review(id = "4", placeId = "p1", authorName = "D", rating = 2.0, date = "now", content = "ok")
        )
        val metrics = ReviewMetrics.fromReviews(reviews)
        assertEquals(4, metrics.totalReviews)
        assertEquals(4.0, metrics.overallRating, 0.01)
        assertEquals(2, metrics.countForStar(5))
        assertEquals(1, metrics.countForStar(4))
        assertEquals(0, metrics.countForStar(3))
        assertEquals(1, metrics.countForStar(2))
        assertEquals(0, metrics.countForStar(1))
        assertEquals(0.5f, metrics.fractionForStar(5), 0.01f)
    }
}
