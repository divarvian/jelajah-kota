package com.arvian.jelajahkota.feature.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arvian.jelajahkota.core.common.location.LocationProvider
import com.arvian.jelajahkota.core.common.location.NoOpLocationProvider
import com.arvian.jelajahkota.core.domain.model.AppSettings
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.core.domain.model.UserGender
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import com.arvian.jelajahkota.core.domain.validation.ProfileFieldErrors
import com.arvian.jelajahkota.core.domain.validation.ProfileValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ProfileUiState(
    val user: User? = null,
    val draftFullName: String = "",
    val draftPhone: String = "",
    val draftCity: String = "",
    val draftBio: String = "",
    val draftBirthDate: String = "",
    val draftGender: String = "",
    val fieldErrors: ProfileFieldErrors = ProfileFieldErrors(),
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
    val isLoggedIn: Boolean get() = user != null
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
    private val reviewsRepository: ReviewsRepository,
    private val locationProvider: LocationProvider = NoOpLocationProvider()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var hasSeededDraft = false

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
                val userReviewsCount = if (user != null) {
                    reviews.count { it.userId == user.id }
                } else {
                    0
                }
                ProfileSnapshot(
                    user = user,
                    settings = settings,
                    favoritesCount = if (user != null) favoriteIds.size else 0,
                    plansCount = if (user != null) plans.size else 0,
                    reviewsCount = userReviewsCount
                )
            }.collect { snapshot ->
                _uiState.update { current ->
                    val shouldSeed = !hasSeededDraft || current.user?.id != snapshot.user?.id
                    if (snapshot.user != null && shouldSeed) {
                        hasSeededDraft = true
                    }
                    if (snapshot.user == null) {
                        hasSeededDraft = false
                    }
                    val normalizedGender = UserGender.fromRaw(snapshot.user?.gender)?.code.orEmpty()
                    current.copy(
                        user = snapshot.user,
                        draftFullName = if (shouldSeed) snapshot.user?.fullName.orEmpty() else current.draftFullName,
                        draftPhone = if (shouldSeed) snapshot.user?.phone.orEmpty() else current.draftPhone,
                        draftCity = if (shouldSeed) snapshot.user?.city.orEmpty() else current.draftCity,
                        draftBio = if (shouldSeed) snapshot.user?.bio.orEmpty() else current.draftBio,
                        draftBirthDate = if (shouldSeed) snapshot.user?.birthDate.orEmpty() else current.draftBirthDate,
                        draftGender = if (shouldSeed) normalizedGender else current.draftGender,
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
        if (name.length > ProfileValidator.MAX_NAME_LENGTH) return
        _uiState.update {
            it.copy(
                draftFullName = name,
                fieldErrors = it.fieldErrors.copy(fullNameError = null),
                errorMessage = null
            )
        }
    }

    fun onBioChange(bio: String) {
        if (bio.length > ProfileValidator.MAX_BIO_LENGTH) return
        _uiState.update {
            it.copy(
                draftBio = bio,
                fieldErrors = it.fieldErrors.copy(bioError = null),
                errorMessage = null
            )
        }
    }

    fun onCityChange(city: String) {
        if (city.length > ProfileValidator.MAX_CITY_LENGTH) return
        _uiState.update {
            it.copy(
                draftCity = city,
                fieldErrors = it.fieldErrors.copy(cityError = null),
                errorMessage = null
            )
        }
    }

    fun onPhoneChange(phone: String) {
        val filtered = phone.filterIndexed { index, ch ->
            ch.isDigit() || (ch == '+' && index == 0) || ch == ' ' || ch == '-'
        }
        if (filtered.length > 20) return
        _uiState.update {
            it.copy(
                draftPhone = filtered,
                fieldErrors = it.fieldErrors.copy(phoneError = null),
                errorMessage = null
            )
        }
    }

    fun onBirthDateChange(birthDateIso: String) {
        _uiState.update {
            it.copy(
                draftBirthDate = birthDateIso,
                fieldErrors = it.fieldErrors.copy(birthDateError = null),
                errorMessage = null
            )
        }
    }

    fun onGenderChange(genderCode: String) {
        val normalized = UserGender.fromRaw(genderCode)?.code ?: genderCode
        _uiState.update {
            val next = if (it.draftGender == normalized && normalized.isNotBlank()) "" else normalized
            it.copy(
                draftGender = next,
                fieldErrors = it.fieldErrors.copy(genderError = null),
                errorMessage = null
            )
        }
    }

    fun hasLocationPermission(): Boolean = locationProvider.hasLocationPermission()

    fun toggleLocation(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(locationEnabled = enabled)) }
        if (enabled && locationProvider.hasLocationPermission()) {
            locationProvider.refreshLocation()
        } else if (!enabled) {
            locationProvider.clearLocation()
        }
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
        val state = _uiState.value
        val (validated, errors) = ProfileValidator.validate(
            fullName = state.draftFullName,
            phone = state.draftPhone,
            city = state.draftCity,
            bio = state.draftBio,
            birthDate = state.draftBirthDate,
            gender = state.draftGender
        )
        if (validated == null) {
            _uiState.update {
                it.copy(
                    fieldErrors = errors,
                    errorMessage = errors.firstErrorMessage
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    fieldErrors = ProfileFieldErrors(),
                    errorMessage = null
                )
            }
            val result = profileRepository.updateProfile(
                fullName = validated.fullName,
                phone = validated.phone,
                city = validated.city,
                bio = validated.bio,
                birthDate = validated.birthDateIso.orEmpty(),
                gender = validated.genderCode.orEmpty()
            )
            result.fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            user = updated,
                            draftFullName = updated.fullName,
                            draftPhone = updated.phone,
                            draftCity = updated.city,
                            draftBio = updated.bio,
                            draftBirthDate = updated.birthDate,
                            draftGender = updated.gender,
                            saveSuccess = true
                        )
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

    fun uploadAvatarBytes(bytes: ByteArray, declaredMimeType: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = profileRepository.uploadAvatar(bytes, declaredMimeType)
            result.fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(isSaving = false, user = updated, avatarUpdated = true)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Gagal mengunggah foto profil.")
                    }
                }
            )
        }
    }

    fun reportError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private data class ProfileSnapshot(
        val user: User?,
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
        private val reviewsRepository: ReviewsRepository,
        private val locationProvider: LocationProvider = NoOpLocationProvider()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                profileRepository = profileRepository,
                authRepository = authRepository,
                preferencesRepository = preferencesRepository,
                favoritesRepository = favoritesRepository,
                plansRepository = plansRepository,
                reviewsRepository = reviewsRepository,
                locationProvider = locationProvider
            ) as T
        }
    }
}
