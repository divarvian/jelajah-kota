package com.arvian.jelajahkota.core.domain.validation

import com.arvian.jelajahkota.core.domain.model.UserGender
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

data class ValidatedProfilePayload(
    val fullName: String,
    val phone: String,
    val city: String,
    val bio: String,
    val birthDateIso: String?,
    val genderCode: String?
)

data class ProfileFieldErrors(
    val fullNameError: String? = null,
    val phoneError: String? = null,
    val cityError: String? = null,
    val bioError: String? = null,
    val birthDateError: String? = null,
    val genderError: String? = null
) {
    val hasErrors: Boolean
        get() = fullNameError != null ||
            phoneError != null ||
            cityError != null ||
            bioError != null ||
            birthDateError != null ||
            genderError != null

    val firstErrorMessage: String?
        get() = fullNameError ?: phoneError ?: cityError ?: bioError ?: birthDateError ?: genderError
}

/**
 * Pure-Kotlin profile validation & formatting rules aligned 1:1 with PostgreSQL CHECK constraints
 * on `public.profiles` (migration 20261124000003).
 */
object ProfileValidator {

    const val MAX_NAME_LENGTH = 80
    const val MIN_NAME_LENGTH = 2
    const val MAX_CITY_LENGTH = 80
    const val MAX_BIO_LENGTH = 300
    val MIN_BIRTH_DATE: LocalDate = LocalDate.of(1900, 1, 1)

    val CURATED_CITIES: List<String> = listOf(
        "Kota Malang",
        "Kota Batu",
        "Kab. Malang",
        "Surabaya",
        "Yogyakarta",
        "Semarang",
        "Solo",
        "Bandung",
        "Jakarta",
        "Denpasar"
    )

    private val phoneRegex = Regex("^\\+?[0-9]{9,15}$")
    private val isoDateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
        .withResolverStyle(ResolverStyle.STRICT)
    private val legacySlashFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    fun validate(
        fullName: String,
        phone: String,
        city: String,
        bio: String,
        birthDate: String,
        gender: String,
        today: LocalDate = LocalDate.now()
    ): Pair<ValidatedProfilePayload?, ProfileFieldErrors> {
        val trimmedName = fullName.trim()
        val nameError = when {
            trimmedName.isBlank() -> "Nama lengkap tidak boleh kosong."
            trimmedName.length < MIN_NAME_LENGTH -> "Nama lengkap minimal $MIN_NAME_LENGTH karakter."
            trimmedName.length > MAX_NAME_LENGTH -> "Nama lengkap maksimal $MAX_NAME_LENGTH karakter."
            else -> null
        }

        val (normalizedPhone, phoneError) = validateAndNormalizePhone(phone)
        val trimmedCity = city.trim()
        val cityError = if (trimmedCity.length > MAX_CITY_LENGTH) {
            "Nama kota maksimal $MAX_CITY_LENGTH karakter."
        } else {
            null
        }

        val trimmedBio = bio.trim()
        val bioError = if (trimmedBio.length > MAX_BIO_LENGTH) {
            "Bio maksimal $MAX_BIO_LENGTH karakter."
        } else {
            null
        }

        val (normalizedBirthDate, birthDateError) = validateBirthDateIso(birthDate, today)
        val (normalizedGender, genderError) = validateGenderCode(gender)

        val errors = ProfileFieldErrors(
            fullNameError = nameError,
            phoneError = phoneError,
            cityError = cityError,
            bioError = bioError,
            birthDateError = birthDateError,
            genderError = genderError
        )

        if (errors.hasErrors) {
            return null to errors
        }

        return ValidatedProfilePayload(
            fullName = trimmedName,
            phone = normalizedPhone,
            city = trimmedCity,
            bio = trimmedBio,
            birthDateIso = normalizedBirthDate,
            genderCode = normalizedGender
        ) to errors
    }

    fun validateAndNormalizePhone(raw: String): Pair<String, String?> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return "" to null
        val stripped = trimmed.replace(Regex("[\\s\\-()]+"), "")
        val normalized = when {
            stripped.startsWith("0") && stripped.length >= 9 -> "+62${stripped.substring(1)}"
            else -> stripped
        }
        return if (phoneRegex.matches(normalized)) {
            normalized to null
        } else {
            normalized to "Gunakan format nomor HP valid (9–15 digit, contoh: 081234567890 atau +6281234567890)."
        }
    }

    fun validateBirthDateIso(
        raw: String,
        today: LocalDate = LocalDate.now()
    ): Pair<String?, String?> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null to null

        val parsed = runCatching { LocalDate.parse(trimmed, isoDateFormatter) }
            .recoverCatching { LocalDate.parse(trimmed, legacySlashFormatter) }
            .getOrNull()
            ?: return null to "Format tanggal lahir tidak valid. Pilih tanggal melalui kalender."

        return when {
            parsed.isBefore(MIN_BIRTH_DATE) -> null to "Tanggal lahir tidak valid (minimal tahun 1900)."
            parsed.isAfter(today) -> null to "Tanggal lahir tidak boleh di masa depan."
            else -> parsed.format(isoDateFormatter) to null
        }
    }

    fun validateGenderCode(raw: String): Pair<String?, String?> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null to null
        val gender = UserGender.fromRaw(trimmed)
        return if (gender != null) {
            gender.code to null
        } else {
            null to "Pilih jenis kelamin dari opsi yang tersedia."
        }
    }

    fun formatBirthDateForDisplay(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        val parsed = runCatching { LocalDate.parse(trimmed, isoDateFormatter) }
            .recoverCatching { LocalDate.parse(trimmed, legacySlashFormatter) }
            .getOrNull()
            ?: return trimmed
        val monthShort = when (parsed.month) {
            Month.JANUARY -> "Jan"
            Month.FEBRUARY -> "Feb"
            Month.MARCH -> "Mar"
            Month.APRIL -> "Apr"
            Month.MAY -> "Mei"
            Month.JUNE -> "Jun"
            Month.JULY -> "Jul"
            Month.AUGUST -> "Agu"
            Month.SEPTEMBER -> "Sep"
            Month.OCTOBER -> "Okt"
            Month.NOVEMBER -> "Nov"
            Month.DECEMBER -> "Des"
        }
        return "${parsed.dayOfMonth} $monthShort ${parsed.year}"
    }

    fun initialsForName(fullName: String?): String {
        val parts = fullName?.trim()
            ?.split(Regex("\\s+"))
            ?.filter { it.isNotBlank() }
            .orEmpty()
        if (parts.isEmpty()) return ""
        return when (parts.size) {
            1 -> parts[0].take(1).uppercase()
            else -> "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
        }
    }
}
