package com.arvian.jelajahkota.core.domain.repository

import com.arvian.jelajahkota.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signIn(email: String, password: String): Flow<Result<User>>
    fun signUp(fullName: String, email: String, password: String): Flow<Result<User>>
    fun sendPasswordResetEmail(email: String): Flow<Result<Boolean>>
    fun verifyOtp(email: String, otp: String): Flow<Result<Boolean>>
    fun resetPassword(email: String, newPassword: String): Flow<Result<Boolean>>
    fun getCurrentUser(): Flow<User?>
    fun signOut(): Flow<Result<Unit>>
}
