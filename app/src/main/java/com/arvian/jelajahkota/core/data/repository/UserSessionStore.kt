package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single in-memory source of truth for the active user session across Auth, Profile, and Features
 * when running in local offline/demo mode.
 * `null` explicitly represents Guest mode (unauthenticated user).
 */
class UserSessionStore(
    initialUser: User? = null
) {
    private val _currentUser = MutableStateFlow<User?>(initialUser)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun setUser(user: User?) {
        _currentUser.value = user
    }

    fun updateUser(transform: (User) -> User) {
        _currentUser.update { existing ->
            existing?.let(transform)
        }
    }

    companion object {
        val DEFAULT_USER = User(
            id = "user_arvian",
            fullName = "Arvian Diva",
            email = "arvian@jelajahkota.id",
            phone = "+6281234567890",
            city = "Kota Malang",
            bio = "Pecinta sejarah dan destinasi heritage di Jawa Timur.",
            birthDate = "2000-09-06",
            gender = "male",
            avatarResId = 0
        )
    }
}
