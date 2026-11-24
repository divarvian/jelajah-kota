package com.arvian.jelajahkota.feature.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.domain.model.AppSettings
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ProfileUiState(
    val user: User = User(
        id = "user_arvian",
        fullName = "Arvian Diva",
        email = "arvian@jelajahkota.id",
        phone = "+62 812-3456-7890",
        city = "Kota Malang",
        bio = "Penjelajah Kota, Pecinta Sejarah & Kuliner Malang",
        birthDate = "06/09/2000",
        gender = "Laki-laki"
    ),
    val settings: AppSettings = AppSettings(),
    val favoritesCount: Int = 0,
    val plansCount: Int = 0,
    val reviewsCount: Int = 0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val avatarUpdated: Boolean = false,
    val signedOut: Boolean = false,
    val errorMessage: String? = null
) {
    val locationEnabled: Boolean get() = settings.locationEnabled
    val darkModeEnabled: Boolean get() = settings.darkModeEnabled
    val pushNotificationsEnabled: Boolean get() = settings.pushNotificationsEnabled
    val emailNotificationsEnabled: Boolean get() = settings.emailNotificationsEnabled
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val plansRepository: PlansRepository,
    private val reviewsRepository: ReviewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeState()
    }

    private fun observeState() {
        viewModelScope.launch {
            combine(
                profileRepository.getProfile(),
                preferencesRepository.observeAppSettings(),
                favoritesRepository.getFavoritePlaceIds(),
                plansRepository.getPlans(),
                reviewsRepository.getAllReviews()
            ) { user, settings, favoriteIds, plans, reviews ->
                ProfileSnapshot(
                    user = user,
                    settings = settings,
                    favoritesCount = favoriteIds.size,
                    plansCount = plans.size,
                    reviewsCount = reviews.size
                )
            }.collect { snapshot ->
                _uiState.update { current ->
                    current.copy(
                        user = snapshot.user,
                        settings = snapshot.settings,
                        favoritesCount = snapshot.favoritesCount,
                        plansCount = snapshot.plansCount,
                        reviewsCount = snapshot.reviewsCount
                    )
                }
            }
        }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(user = it.user.copy(fullName = name), errorMessage = null) }
    }

    fun onBioChange(bio: String) {
        _uiState.update { it.copy(user = it.user.copy(bio = bio), errorMessage = null) }
    }

    fun onCityChange(city: String) {
        _uiState.update { it.copy(user = it.user.copy(city = city), errorMessage = null) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(user = it.user.copy(phone = phone), errorMessage = null) }
    }

    fun onBirthDateChange(birthDate: String) {
        _uiState.update { it.copy(user = it.user.copy(birthDate = birthDate), errorMessage = null) }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { it.copy(user = it.user.copy(gender = gender), errorMessage = null) }
    }

    fun toggleLocation(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(locationEnabled = enabled)) }
        viewModelScope.launch {
            preferencesRepository.setLocationEnabled(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(darkModeEnabled = enabled)) }
        viewModelScope.launch {
            preferencesRepository.setDarkModeEnabled(enabled)
        }
    }

    fun togglePushNotifications(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(pushNotificationsEnabled = enabled)) }
        viewModelScope.launch {
            preferencesRepository.setPushNotificationsEnabled(enabled)
        }
    }

    fun toggleEmailNotifications(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(emailNotificationsEnabled = enabled)) }
        viewModelScope.launch {
            preferencesRepository.setEmailNotificationsEnabled(enabled)
        }
    }

    fun saveProfile() {
        val user = _uiState.value.user
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = profileRepository.updateProfile(
                fullName = user.fullName,
                phone = user.phone,
                city = user.city,
                bio = user.bio,
                birthDate = user.birthDate,
                gender = user.gender
            )
            result.fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(isSaving = false, user = updated, saveSuccess = true)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Gagal menyimpan profil.")
                    }
                }
            )
        }
    }

    fun uploadAvatar(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = profileRepository.updateAvatar(uri)
            result.fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(isSaving = false, user = updated, avatarUpdated = true)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Gagal memperbarui foto.")
                    }
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update { it.copy(signedOut = true) }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun consumeAvatarUpdated() {
        _uiState.update { it.copy(avatarUpdated = false) }
    }

    fun consumeSignedOut() {
        _uiState.update { it.copy(signedOut = false) }
    }

    private data class ProfileSnapshot(
        val user: User,
        val settings: AppSettings,
        val favoritesCount: Int,
        val plansCount: Int,
        val reviewsCount: Int
    )

    class Factory(
        private val profileRepository: ProfileRepository,
        private val authRepository: AuthRepository,
        private val preferencesRepository: UserPreferencesRepository,
        private val favoritesRepository: FavoritesRepository,
        private val plansRepository: PlansRepository,
        private val reviewsRepository: ReviewsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                profileRepository = profileRepository,
                authRepository = authRepository,
                preferencesRepository = preferencesRepository,
                favoritesRepository = favoritesRepository,
                plansRepository = plansRepository,
                reviewsRepository = reviewsRepository
            ) as T
        }
    }
}
