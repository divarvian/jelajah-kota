package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(fullName: String, email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun verifyOtp(email: String, otp: String): Result<Unit>
    suspend fun resetPassword(email: String, newPassword: String): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun signOut(): Result<Unit>
}
