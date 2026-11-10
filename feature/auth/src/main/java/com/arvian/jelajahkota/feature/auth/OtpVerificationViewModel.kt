package com.arvian.jelajahkota.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OtpVerificationUiState(
    val email: String = "",
    val otpValues: List<String> = listOf("", "", "", ""),
    val timerSeconds: Int = 120,
    val canResend: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerified: Boolean = false
) {
    val timerFormatted: String
        get() {
            val minutes = timerSeconds / 60
            val seconds = timerSeconds % 60
            return "%d:%02d menit".format(minutes, seconds)
        }

    val isComplete: Boolean
        get() = otpValues.all { it.isNotBlank() }
}

class OtpVerificationViewModel(
    private val authRepository: AuthRepository,
    email: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpVerificationUiState(email = email))
    val uiState: StateFlow<OtpVerificationUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerSeconds = 120, canResend = false) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            _uiState.update { it.copy(canResend = true) }
        }
    }

    fun onDigitChange(index: Int, value: String) {
        val current = _uiState.value.otpValues.toMutableList()
        if (index in current.indices) {
            current[index] = value
            _uiState.update { it.copy(otpValues = current, errorMessage = null) }
        }
    }

    fun resendCode() {
        if (!_uiState.value.canResend) return
        startTimer()
        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(_uiState.value.email).collect { }
        }
    }

    fun verifyCode(onSuccess: () -> Unit) {
        val fullCode = _uiState.value.otpValues.joinToString("")
        if (fullCode.length < 4) {
            _uiState.update { it.copy(errorMessage = "Silakan masukkan 4 digit kode OTP.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.verifyOtp(_uiState.value.email, fullCode).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isLoading = false, isVerified = true) }
                        onSuccess()
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Kode verifikasi salah.") }
                    }
                )
            }
        }
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val email: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OtpVerificationViewModel(authRepository, email) as T
        }
    }
}
