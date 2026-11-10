package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl : AuthRepository {

    private val currentUser = MutableStateFlow<User?>(
        User(
            id = "user_default",
            fullName = "Arvian Diva",
            email = "arvian@jelajahkota.id",
            phone = "+62 812-3456-7890",
            city = "Kota Malang",
            bio = "Pecinta sejarah dan destinasi heritage di Jawa Timur."
        )
    )

    override fun signIn(email: String, password: String): Flow<Result<User>> = flow {
        if (email.isBlank() || password.isBlank()) {
            emit(Result.failure(IllegalArgumentException("Email dan kata sandi wajib diisi.")))
            return@flow
        }
        val user = User(
            id = "user_${email.hashCode()}",
            fullName = if (email.contains("@")) email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() } else "Pengguna",
            email = email,
            phone = "+62 812-3456-7890",
            city = "Kota Malang",
            bio = "Menjelajahi Kota Malang bersama JelajahKota."
        )
        currentUser.value = user
        emit(Result.success(user))
    }

    override fun signUp(fullName: String, email: String, password: String): Flow<Result<User>> = flow {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            emit(Result.failure(IllegalArgumentException("Seluruh data pendaftaran wajib diisi.")))
            return@flow
        }
        val user = User(
            id = "user_${email.hashCode()}",
            fullName = fullName,
            email = email,
            phone = "",
            city = "Kota Malang",
            bio = ""
        )
        currentUser.value = user
        emit(Result.success(user))
    }

    override fun sendPasswordResetEmail(email: String): Flow<Result<Boolean>> = flow {
        if (email.isBlank() || !email.contains("@")) {
            emit(Result.failure(IllegalArgumentException("Masukkan alamat email yang valid.")))
            return@flow
        }
        emit(Result.success(true))
    }

    override fun verifyOtp(email: String, otp: String): Flow<Result<Boolean>> = flow {
        if (otp.length != 4) {
            emit(Result.failure(IllegalArgumentException("Kode OTP harus terdiri dari 4 digit.")))
            return@flow
        }
        emit(Result.success(true))
    }

    override fun resetPassword(email: String, newPassword: String): Flow<Result<Boolean>> = flow {
        if (newPassword.length < 8) {
            emit(Result.failure(IllegalArgumentException("Kata sandi minimal 8 karakter.")))
            return@flow
        }
        emit(Result.success(true))
    }

    override fun getCurrentUser(): Flow<User?> = currentUser.asStateFlow()

    override fun signOut(): Flow<Result<Unit>> = flow {
        currentUser.value = null
        emit(Result.success(Unit))
    }
}
