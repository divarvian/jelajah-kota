package com.arvian.jelajahkota

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.arvian.jelajahkota.core.data.preferences.UserPreferencesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "jelajahkota_preferences")

class JelajahKotaApp : Application() {

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var placesRepository: PlacesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = UserPreferencesRepositoryImpl(userDataStore)
        placesRepository = PlacesRepositoryImpl()
    }
}
