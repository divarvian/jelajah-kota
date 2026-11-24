package com.arvian.jelajahkota.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.arvian.jelajahkota.core.common.dispatcher.DefaultDispatcherProvider
import com.arvian.jelajahkota.core.common.dispatcher.DispatcherProvider
import com.arvian.jelajahkota.core.data.preferences.UserPreferencesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.AuthRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.FavoritesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlansRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ProfileRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ReviewsRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.UserSessionStore
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "jelajahkota_preferences")

/**
 * Centralized manual Dependency Injection container for JelajahKota.
 * Keeps dependency graph wiring out of Application and UI composables.
 */
interface AppContainer {
    val dispatchers: DispatcherProvider
    val preferencesRepository: UserPreferencesRepository
    val placesRepository: PlacesRepository
    val authRepository: AuthRepository
    val plansRepository: PlansRepository
    val favoritesRepository: FavoritesRepository
    val reviewsRepository: ReviewsRepository
    val profileRepository: ProfileRepository
}

class DefaultAppContainer(context: Context) : AppContainer {

    private val appContext = context.applicationContext
    private val userSessionStore = UserSessionStore()

    override val dispatchers: DispatcherProvider = DefaultDispatcherProvider()

    override val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(appContext.userDataStore)
    }

    override val placesRepository: PlacesRepository by lazy {
        PlacesRepositoryImpl()
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(userSessionStore)
    }

    override val plansRepository: PlansRepository by lazy {
        PlansRepositoryImpl()
    }

    override val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepositoryImpl(placesRepository)
    }

    override val reviewsRepository: ReviewsRepository by lazy {
        ReviewsRepositoryImpl()
    }

    override val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(userSessionStore)
    }
}
