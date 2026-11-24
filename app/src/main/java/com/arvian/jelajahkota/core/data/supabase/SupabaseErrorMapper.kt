package com.arvian.jelajahkota.core.data.supabase

import kotlinx.coroutines.CancellationException

/**
 * Translates low-level Supabase / Ktor / PostgreSQL errors into user-safe Bahasa Indonesia messages
 * without leaking tokens, stack traces, or internal SQL schemas.
 */
object SupabaseErrorMapper {
    fun toUserMessage(error: Throwable, fallback: String = "Terjadi kesalahan. Silakan coba lagi."): String {
        if (error is CancellationException) throw error
        val raw = error.message.orEmpty().lowercase()
        return when {
            raw.contains("invalid login credentials") || raw.contains("invalid_credentials") ->
                "Email atau kata sandi salah. Silakan periksa kembali."
            raw.contains("email not confirmed") ->
                "Email Anda belum dikonfirmasi. Silakan cek kotak masuk email Anda."
            raw.contains("user already registered") || raw.contains("already been registered") ->
                "Alamat email ini sudah terdaftar. Silakan masuk dengan akun Anda."
            raw.contains("rate limit") || raw.contains("too many requests") || raw.contains("429") ->
                "Terlalu banyak percobaan. Silakan tunggu beberapa saat lalu coba lagi."
            raw.contains("expired") || raw.contains("otp") || raw.contains("token") ->
                "Kode verifikasi tidak valid atau sudah kedaluwarsa."
            raw.contains("row-level security") || raw.contains("permission denied") || raw.contains("42501") ->
                "Anda tidak memiliki izin untuk melakukan tindakan ini. Pastikan sudah masuk akun."
            raw.contains("unable to resolve host") || raw.contains("timeout") || raw.contains("connect") || raw.contains("network") ->
                "Koneksi internet tidak stabil atau server tidak dapat dijangkau."
            raw.contains("password") && raw.contains("short") ->
                "Kata sandi terlalu pendek. Gunakan minimal 8 karakter."
            error is IllegalArgumentException || error is IllegalStateException ->
                error.message ?: fallback
            else -> fallback
        }
    }
}
