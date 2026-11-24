package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val sessionStore: UserSessionStore = UserSessionStore()
) : AuthRepository {

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override suspend fun signIn(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email dan kata sandi wajib diisi."))
        }
        if (!emailPattern.matches(trimmedEmail)) {
            return Result.failure(IllegalArgumentException("Format alamat email tidak valid."))
        }

        val existing = sessionStore.currentUser.value
        val user = if (existing != null && existing.email.equals(trimmedEmail, ignoreCase = true)) {
            existing
        } else {
            val derivedName = trimmedEmail
                .substringBefore("@")
                .replace(Regex("[._-]+"), " ")
                .split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
                .ifBlank { "Penjelajah" }

            User(
                id = "user_${trimmedEmail.lowercase().hashCode()}",
                fullName = derivedName,
                email = trimmedEmail,
                phone = "",
                city = "",
                bio = "",
                avatarResId = 0
            )
        }
        sessionStore.setUser(user)
        return Result.success(user)
    }

    override suspend fun signUp(fullName: String, email: String, password: String): Result<User> {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()
        if (trimmedName.isBlank() || trimmedEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Seluruh data pendaftaran wajib diisi."))
        }
        if (!emailPattern.matches(trimmedEmail)) {
            return Result.failure(IllegalArgumentException("Format alamat email tidak valid."))
        }
        if (password.length < 8) {
            return Result.failure(IllegalArgumentException("Kata sandi minimal 8 karakter."))
        }

        val user = User(
            id = "user_${trimmedEmail.lowercase().hashCode()}",
            fullName = trimmedName,
            email = trimmedEmail,
            phone = "",
            city = "",
            bio = "",
            avatarResId = 0
        )
        sessionStore.setUser(user)
        return Result.success(user)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !emailPattern.matches(trimmedEmail)) {
            return Result.failure(IllegalArgumentException("Masukkan alamat email yang valid."))
        }
        return Result.success(Unit)
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<Unit> {
        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("Kode OTP harus terdiri dari 6 digit angka."))
        }
        return Result.success(Unit)
    }

    override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        if (newPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Kata sandi minimal 8 karakter."))
        }
        return Result.success(Unit)
    }

    override fun getCurrentUser(): Flow<User?> = sessionStore.currentUser

    override suspend fun signOut(): Result<Unit> {
        sessionStore.setUser(null)
        return Result.success(Unit)
    }
}
