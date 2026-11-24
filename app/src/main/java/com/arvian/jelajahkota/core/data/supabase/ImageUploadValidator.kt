package com.arvian.jelajahkota.core.data.supabase

/**
 * Validated image payload ready for upload to Supabase Storage or local demo cache.
 */
data class ValidatedImage(
    val bytes: ByteArray,
    val mimeType: String,
    val extension: String
)

/**
 * Enforces strict upload validation before bytes leave the device:
 * - Non-empty & within byte limit (2 MB avatar, 5 MB visitor gallery photo).
 * - File magic-byte signature verification (JPEG, PNG, WebP) — never trusts filename extension alone.
 * - Cross-checks declared MIME type against detected magic bytes when supplied.
 */
object ImageUploadValidator {

    const val MAX_AVATAR_BYTES: Int = 2 * 1024 * 1024 // 2 MB
    const val MAX_GALLERY_BYTES: Int = 5 * 1024 * 1024 // 5 MB

    private val ALLOWED_MIMES = setOf("image/jpeg", "image/png", "image/webp")

    fun validate(
        bytes: ByteArray,
        declaredMimeType: String?,
        maxBytes: Int
    ): Result<ValidatedImage> {
        if (bytes.isEmpty()) {
            return Result.failure(IllegalArgumentException("File gambar kosong atau tidak dapat dibaca."))
        }
        if (bytes.size > maxBytes) {
            val maxMb = maxBytes / (1024 * 1024)
            return Result.failure(IllegalArgumentException("Ukuran gambar melebihi batas maksimal ${maxMb} MB."))
        }

        val detected = detectSignature(bytes)
            ?: return Result.failure(
                IllegalArgumentException("Format gambar tidak didukung. Gunakan file JPG, PNG, atau WebP asli.")
            )

        val normalizedDeclared = declaredMimeType
            ?.lowercase()
            ?.trim()
            ?.let { if (it == "image/jpg") "image/jpeg" else it }

        if (!normalizedDeclared.isNullOrBlank()) {
            if (normalizedDeclared !in ALLOWED_MIMES) {
                return Result.failure(
                    IllegalArgumentException("Tipe file tidak diizinkan. Hanya JPG, PNG, dan WebP yang diperbolehkan.")
                )
            }
            if (normalizedDeclared != detected.first) {
                return Result.failure(
                    IllegalArgumentException("Tipe file tidak sesuai dengan isi gambar sebenarnya.")
                )
            }
        }

        return Result.success(
            ValidatedImage(
                bytes = bytes,
                mimeType = detected.first,
                extension = detected.second
            )
        )
    }

    private fun detectSignature(bytes: ByteArray): Pair<String, String>? {
        // JPEG: FF D8 FF
        if (bytes.size >= 3 &&
            (bytes[0].toInt() and 0xFF) == 0xFF &&
            (bytes[1].toInt() and 0xFF) == 0xD8 &&
            (bytes[2].toInt() and 0xFF) == 0xFF
        ) {
            return "image/jpeg" to "jpg"
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (bytes.size >= 8 &&
            (bytes[0].toInt() and 0xFF) == 0x89 &&
            (bytes[1].toInt() and 0xFF) == 0x50 &&
            (bytes[2].toInt() and 0xFF) == 0x4E &&
            (bytes[3].toInt() and 0xFF) == 0x47 &&
            (bytes[4].toInt() and 0xFF) == 0x0D &&
            (bytes[5].toInt() and 0xFF) == 0x0A &&
            (bytes[6].toInt() and 0xFF) == 0x1A &&
            (bytes[7].toInt() and 0xFF) == 0x0A
        ) {
            return "image/png" to "png"
        }

        // WebP: RIFF .... WEBP
        if (bytes.size >= 12 &&
            bytes[0] == 'R'.code.toByte() &&
            bytes[1] == 'I'.code.toByte() &&
            bytes[2] == 'F'.code.toByte() &&
            bytes[3] == 'F'.code.toByte() &&
            bytes[8] == 'W'.code.toByte() &&
            bytes[9] == 'E'.code.toByte() &&
            bytes[10] == 'B'.code.toByte() &&
            bytes[11] == 'P'.code.toByte()
        ) {
            return "image/webp" to "webp"
        }

        return null
    }
}
