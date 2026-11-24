package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single in-memory source of truth for the active user profile across Auth, Profile, and Home
 * while running against local mock repositories (to be backed by Supabase Auth/Profiles in Phase 2).
 */
class UserSessionStore(
    initialUser: User = DEFAULT_USER
) {
    private val _currentUser = MutableStateFlow<User?>(initialUser)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun setUser(user: User?) {
        _currentUser.value = user
    }

    fun updateUser(transform: (User) -> User) {
        _currentUser.update { existing ->
            transform(existing ?: DEFAULT_USER)
        }
    }

    companion object {
        val DEFAULT_USER = User(
            id = "user_arvian",
            fullName = "Arvian Diva",
            email = "arvian@jelajahkota.id",
            phone = "+62 812-3456-7890",
            city = "Kota Malang",
            bio = "Pecinta sejarah dan destinasi heritage di Jawa Timur.",
            birthDate = "06/09/2000",
            gender = "Laki-laki",
            avatarResId = R.drawable.img_user_avatar
        )
    }
}
