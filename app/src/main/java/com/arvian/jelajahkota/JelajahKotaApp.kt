package com.arvian.jelajahkota

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.arvian.jelajahkota.core.data.preferences.UserPreferencesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.AuthRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.FavoritesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlansRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ProfileRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ReviewsRepositoryImpl
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "jelajahkota_preferences")

class JelajahKotaApp : Application() {

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var placesRepository: PlacesRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var plansRepository: PlansRepository
        private set

    lateinit var favoritesRepository: FavoritesRepository
        private set

    lateinit var reviewsRepository: ReviewsRepository
        private set

    lateinit var profileRepository: ProfileRepository
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = UserPreferencesRepositoryImpl(userDataStore)
        placesRepository = PlacesRepositoryImpl()
        authRepository = AuthRepositoryImpl()
        plansRepository = PlansRepositoryImpl()
        favoritesRepository = FavoritesRepositoryImpl(placesRepository)
        reviewsRepository = ReviewsRepositoryImpl()
        profileRepository = ProfileRepositoryImpl()
    }
}
