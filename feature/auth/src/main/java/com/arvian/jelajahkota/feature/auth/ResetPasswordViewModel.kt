package com.arvian.jelajahkota.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.PasswordValidation
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val email: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val validation: PasswordValidation = PasswordValidation(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class ResetPasswordViewModel(
    private val authRepository: AuthRepository,
    email: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState(email = email))
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(password: String) {
        val confirm = _uiState.value.confirmPassword
        val validation = evaluatePassword(password, confirm)
        _uiState.update {
            it.copy(
                newPassword = password,
                validation = validation,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(confirm: String) {
        val password = _uiState.value.newPassword
        val validation = evaluatePassword(password, confirm)
        _uiState.update {
            it.copy(
                confirmPassword = confirm,
                validation = validation,
                errorMessage = null
            )
        }
    }

    private fun evaluatePassword(password: String, confirm: String): PasswordValidation {
        val hasMinLength = password.length >= 8
        val hasUpperAndLower = password.any { it.isUpperCase() } && password.any { it.isLowerCase() }
        val hasNumber = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        val hasNumberOrSymbol = hasNumber || hasSymbol
        val isMatching = password.isNotEmpty() && password == confirm

        return PasswordValidation(
            hasMinLength = hasMinLength,
            hasUpperAndLower = hasUpperAndLower,
            hasNumber = hasNumberOrSymbol,
            hasSymbol = hasNumberOrSymbol,
            isMatching = isMatching
        )
    }

    fun submitReset(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.validation.isValid) {
            _uiState.update { it.copy(errorMessage = "Pastikan seluruh kriteria kata sandi telah terpenuhi.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.resetPassword(state.email, state.newPassword).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                        onSuccess()
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Gagal mereset kata sandi.") }
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
            return ResetPasswordViewModel(authRepository, email) as T
        }
    }
}
