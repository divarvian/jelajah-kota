package com.arvian.jelajahkota.core.data.supabase.repository

import com.arvian.jelajahkota.core.data.supabase.SupabaseErrorMapper
import com.arvian.jelajahkota.core.data.supabase.dto.ProfileDto
import com.arvian.jelajahkota.core.data.supabase.dto.SupabaseMappers.toDomain
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class SupabaseAuthRepositoryImpl(
    private val client: SupabaseClient
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUserState: Flow<User?> = _currentUser.asStateFlow()

    init {
        scope.launch {
            client.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val authUser = status.session.user ?: client.auth.currentUserOrNull()
                        _currentUser.value = authUser?.let { loadOrBuildUser(it) }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _currentUser.value = null
                    }
                    else -> Unit
                }
            }
        }
    }

    internal fun updateCachedUser(user: User?) {
        _currentUser.value = user
    }

    internal fun currentUserIdOrNull(): String? =
        client.auth.currentUserOrNull()?.id ?: _currentUser.value?.id

    internal fun currentUserOrNull(): User? = _currentUser.value

    private suspend fun loadOrBuildUser(authUser: UserInfo): User {
        val fallback = userFromAuthInfo(authUser)
        return try {
            val remote = client.from("profiles")
                .select {
                    filter { eq("id", authUser.id) }
                }
                .decodeSingleOrNull<ProfileDto>()
            remote?.toDomain() ?: fallback
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            fallback
        }
    }

    private fun userFromAuthInfo(authUser: UserInfo): User {
        val email = authUser.email.orEmpty()
        val metaName = authUser.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
        val fallbackName = email.substringBefore("@").ifBlank { "Penjelajah" }
        return User(
            id = authUser.id,
            fullName = metaName?.takeIf { it.isNotBlank() } ?: fallbackName,
            email = email,
            city = "",
            avatarResId = 0
        )
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        return try {
            client.auth.signInWith(Email) {
                this.email = trimmedEmail
                this.password = password
            }
            val authUser = client.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Sesi akun tidak ditemukan setelah masuk."))
            val user = loadOrBuildUser(authUser)
            _currentUser.value = user
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal masuk. Periksa kembali email dan kata sandi Anda.")))
        }
    }

    override suspend fun signUp(fullName: String, email: String, password: String): Result<User> {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()
        return try {
            client.auth.signUpWith(Email) {
                this.email = trimmedEmail
                this.password = password
                data = buildJsonObject {
                    put("full_name", trimmedName)
                }
            }
            val authUser = client.auth.currentUserOrNull()
            if (authUser != null) {
                val user = loadOrBuildUser(authUser).copy(fullName = trimmedName.ifBlank { "Penjelajah" })
                _currentUser.value = user
                Result.success(user)
            } else {
                // Email confirmation enabled on server: notify cleanly
                Result.failure(
                    IllegalStateException(
                        "Pendaftaran berhasil! Silakan verifikasi tautan yang dikirim ke $trimmedEmail sebelum masuk."
                    )
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal membuat akun baru.")))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            client.auth.resetPasswordForEmail(email.trim())
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal mengirim kode reset kata sandi.")))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<Unit> {
        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("Kode OTP harus terdiri dari 6 digit angka."))
        }
        return try {
            client.auth.verifyEmailOtp(
                type = OtpType.Email.RECOVERY,
                email = email.trim(),
                token = otp
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Kode OTP salah atau telah kedaluwarsa.")))
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        if (newPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Kata sandi minimal 8 karakter."))
        }
        return try {
            client.auth.updateUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal memperbarui kata sandi.")))
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun signOut(): Result<Unit> {
        return try {
            client.auth.signOut()
            _currentUser.value = null
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            _currentUser.value = null
            Result.failure(IllegalStateException(SupabaseErrorMapper.toUserMessage(e, "Gagal keluar dari akun.")))
        }
    }
}
