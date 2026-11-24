package com.arvian.jelajahkota.core.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ProfileValidatorTest {

    private val fixedToday = LocalDate.of(2026, 11, 24)

    @Test
    fun validFullProfile_normalizesPhoneDateAndGenderToDbContract() {
        val (payload, errors) = ProfileValidator.validate(
            fullName = "  Arvian Diva  ",
            phone = "0812-3456-7890",
            city = "Kota Malang",
            bio = "Penjelajah wisata Jawa Timur",
            birthDate = "06/09/2000",
            gender = "Laki-laki",
            today = fixedToday
        )

        assertFalse(errors.hasErrors)
        assertNotNull(payload)
        assertEquals("Arvian Diva", payload?.fullName)
        assertEquals("+6281234567890", payload?.phone)
        assertEquals("Kota Malang", payload?.city)
        assertEquals("2000-09-06", payload?.birthDateIso)
        assertEquals("male", payload?.genderCode)
    }

    @Test
    fun optionalBlankFields_areAllowedAndMappedToNullOrEmpty() {
        val (payload, errors) = ProfileValidator.validate(
            fullName = "Budi Santoso",
            phone = "",
            city = "",
            bio = "",
            birthDate = "",
            gender = "",
            today = fixedToday
        )

        assertFalse(errors.hasErrors)
        assertEquals("", payload?.phone)
        assertEquals("", payload?.city)
        assertNull(payload?.birthDateIso)
        assertNull(payload?.genderCode)
    }

    @Test
    fun invalidInputs_returnPrecisePerFieldErrors() {
        val (payload, errors) = ProfileValidator.validate(
            fullName = "A",
            phone = "abc123",
            city = "Kota Malang",
            bio = "x".repeat(305),
            birthDate = "2099-05-01",
            gender = "alien",
            today = fixedToday
        )

        assertNull(payload)
        assertTrue(errors.hasErrors)
        assertNotNull(errors.fullNameError)
        assertNotNull(errors.phoneError)
        assertNotNull(errors.bioError)
        assertNotNull(errors.birthDateError)
        assertNotNull(errors.genderError)
    }

    @Test
    fun formatBirthDateForDisplay_andInitialsForName_workCleanly() {
        assertEquals("6 Sep 2000", ProfileValidator.formatBirthDateForDisplay("2000-09-06"))
        assertEquals("12 Apr 1999", ProfileValidator.formatBirthDateForDisplay("12/04/1999"))
        assertEquals("AD", ProfileValidator.initialsForName("Arvian Diva"))
        assertEquals("B", ProfileValidator.initialsForName("Budi"))
        assertEquals("", ProfileValidator.initialsForName("   "))
    }
}
