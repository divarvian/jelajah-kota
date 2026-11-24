package com.arvian.jelajahkota.core.data.supabase

import com.arvian.jelajahkota.core.data.supabase.dto.PlaceDto
import com.arvian.jelajahkota.core.data.supabase.dto.ProfileDto
import com.arvian.jelajahkota.core.data.supabase.dto.ReviewDto
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.data.supabase.dto.VisitPlanDto
import com.arvian.jelajahkota.core.data.supabase.dto.VisitorPhotoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseMappersAndErrorTest {

    @Test
    fun `SupabaseConfig rejects blanks and placeholder values`() {
        assertFalse(SupabaseConfig("", "").isConfigured)
        assertFalse(SupabaseConfig("https://your-project-ref.supabase.co", "sb_publishable_replace_me").isConfigured)
        assertTrue(SupabaseConfig("https://demo.supabase.co", "sb_publishable_real_key_123").isConfigured)
    }

    @Test
    fun `DTO mappers convert Supabase snake_case rows accurately to domain models`() {
        val profile = ProfileDto(
            id = "uid-1",
            fullName = "Arvian Diva",
            email = "arvian@jelajahkota.id",
            city = "Kota Malang"
        ).toDomain()
        assertEquals("Arvian Diva", profile.fullName)
        assertEquals("Kota Malang", profile.city)

        val place = PlaceDto(
            id = "place_jodipan",
            name = "Kampung Warna-Warni Jodipan",
            location = "Blimbing, Malang",
            cityName = "Malang",
            categoryName = "Wisata Kota",
            ticketPriceAmount = 5000,
            isPopular = true
        ).toDomain(listOf("https://cdn.example.com/1.webp"))
        assertEquals("place_jodipan", place.id)
        assertEquals(1, place.galleryImageUrls.size)
        assertTrue(place.imageResId != 0)

        val review = ReviewDto(
            id = "rev-1",
            placeId = "place_jodipan",
            userId = "uid-1",
            authorName = "Arvian",
            rating = 5.0,
            content = "Sangat bagus!",
            createdAt = "2026-11-24T05:00:00Z"
        ).toDomain(placeName = "Kampung Jodipan")
        assertEquals("uid-1", review.userId)
        assertEquals("Kampung Jodipan", review.placeName)

        val plan = VisitPlanDto(
            id = "plan-1",
            userId = "uid-1",
            placeId = "place_bromo",
            placeName = "Gunung Bromo",
            placeLocation = "Malang",
            startDate = "2026-12-01",
            endDate = "2026-12-03",
            visitorCount = 4,
            notes = "Bawa jaket"
        ).toDomain()
        assertEquals(3, plan.durationDays)
        assertEquals(4, plan.visitorCount)

        val photo = VisitorPhotoDto(
            id = "photo-1",
            placeId = "place_jodipan",
            userId = "uid-1",
            uploaderName = "Arvian",
            storagePath = "uid-1/place_jodipan/a.jpg",
            imageUrl = "https://cdn.example.com/a.jpg",
            caption = "Pagi cerah"
        ).toDomain()
        assertEquals("Pagi cerah", photo.caption)
        assertEquals("uid-1/place_jodipan/a.jpg", photo.storagePath)
    }

    @Test
    fun `SupabaseErrorMapper translates known errors to user-friendly Indonesian messages`() {
        val creds = SupabaseErrorMapper.toUserMessage(RuntimeException("Invalid login credentials"))
        assertTrue(creds.contains("Email atau kata sandi salah"))

        val rls = SupabaseErrorMapper.toUserMessage(RuntimeException("new row violates row-level security policy (42501)"))
        assertTrue(rls.contains("tidak memiliki izin"))

        val network = SupabaseErrorMapper.toUserMessage(RuntimeException("Unable to resolve host"))
        assertTrue(network.contains("Koneksi internet"))
    }
}
