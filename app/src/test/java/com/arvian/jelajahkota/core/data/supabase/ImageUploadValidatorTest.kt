package com.arvian.jelajahkota.core.data.supabase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageUploadValidatorTest {

    private val validJpegHeader = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00, 0x10)
    private val validPngHeader = byteArrayOf(
        0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00
    )
    private val validWebpHeader = "RIFF1234WEBP".toByteArray(Charsets.US_ASCII) + byteArrayOf(0x00, 0x01)

    @Test
    fun `accepts valid JPEG PNG and WebP signatures within size limit`() {
        val jpeg = ImageUploadValidator.validate(validJpegHeader, "image/jpeg", ImageUploadValidator.MAX_AVATAR_BYTES)
        assertTrue(jpeg.isSuccess)
        assertEquals("image/jpeg", jpeg.getOrThrow().mimeType)
        assertEquals("jpg", jpeg.getOrThrow().extension)

        val png = ImageUploadValidator.validate(validPngHeader, "image/png", ImageUploadValidator.MAX_GALLERY_BYTES)
        assertTrue(png.isSuccess)
        assertEquals("image/png", png.getOrThrow().mimeType)
        assertEquals("png", png.getOrThrow().extension)

        val webp = ImageUploadValidator.validate(validWebpHeader, "image/webp", ImageUploadValidator.MAX_GALLERY_BYTES)
        assertTrue(webp.isSuccess)
        assertEquals("image/webp", webp.getOrThrow().mimeType)
        assertEquals("webp", webp.getOrThrow().extension)
    }

    @Test
    fun `rejects empty bytes and oversized payloads`() {
        val empty = ImageUploadValidator.validate(ByteArray(0), "image/jpeg", ImageUploadValidator.MAX_AVATAR_BYTES)
        assertTrue(empty.isFailure)

        val oversized = validJpegHeader + ByteArray(ImageUploadValidator.MAX_AVATAR_BYTES)
        val result = ImageUploadValidator.validate(oversized, "image/jpeg", ImageUploadValidator.MAX_AVATAR_BYTES)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message.orEmpty().contains("2 MB"))
    }

    @Test
    fun `rejects spoofed non-image content claiming to be image jpeg`() {
        val fakeSvg = "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>".toByteArray()
        val result = ImageUploadValidator.validate(fakeSvg, "image/jpeg", ImageUploadValidator.MAX_GALLERY_BYTES)
        assertTrue(result.isFailure)
    }

    @Test
    fun `rejects mismatched declared mime type versus real magic bytes`() {
        val result = ImageUploadValidator.validate(validPngHeader, "image/jpeg", ImageUploadValidator.MAX_GALLERY_BYTES)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message.orEmpty().contains("tidak sesuai"))
    }
}
