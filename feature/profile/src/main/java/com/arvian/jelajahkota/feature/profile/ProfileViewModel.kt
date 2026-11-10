package com.arvian.jelajahkota.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User = User(
        id = "user_arvian",
        fullName = "Arvian Diva",
        email = "arvian@jelajahkota.id",
        phone = "+62 812-3456-7890",
        city = "Suhat, Kota Malang",
        bio = "Penjelajah Kota, Pecinta Sejarah & Kuliner Malang"
    ),
    val locationEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getProfile().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(user = it.user.copy(fullName = name)) }
    }

    fun onBioChange(bio: String) {
        _uiState.update { it.copy(user = it.user.copy(bio = bio)) }
    }

    fun onCityChange(city: String) {
        _uiState.update { it.copy(user = it.user.copy(city = city)) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(user = it.user.copy(phone = phone)) }
    }

    fun toggleLocation(enabled: Boolean) {
        _uiState.update { it.copy(locationEnabled = enabled) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    fun togglePushNotifications(enabled: Boolean) {
        _uiState.update { it.copy(pushNotificationsEnabled = enabled) }
    }

    fun toggleEmailNotifications(enabled: Boolean) {
        _uiState.update { it.copy(emailNotificationsEnabled = enabled) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val user = _uiState.value.user
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            profileRepository.updateProfile(user.fullName, user.phone, user.city, user.bio).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                        onSuccess()
                    },
                    onFailure = {
                        _uiState.update { it.copy(isSaving = false) }
                    }
                )
            }
        }
    }

    fun uploadAvatar(uri: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            profileRepository.updateAvatar(uri).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isSaving = false, user = it.user.copy(avatarUrl = uri)) }
                        onSuccess()
                    },
                    onFailure = {
                        _uiState.update { it.copy(isSaving = false) }
                    }
                )
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut().collect {
                onSignedOut()
            }
        }
    }

    class Factory(
        private val profileRepository: ProfileRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(profileRepository, authRepository) as T
        }
    }
}
