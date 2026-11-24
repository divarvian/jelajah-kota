package com.arvian.jelajahkota.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.arvian.jelajahkota.core.common.dispatcher.DefaultDispatcherProvider
import com.arvian.jelajahkota.core.common.dispatcher.DispatcherProvider
import com.arvian.jelajahkota.core.common.location.AndroidLocationProvider
import com.arvian.jelajahkota.core.common.location.LocationProvider
import com.arvian.jelajahkota.core.data.preferences.UserPreferencesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.AuthRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.FavoritesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.GalleryRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.PlansRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ProfileRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.ReviewsRepositoryImpl
import com.arvian.jelajahkota.core.data.repository.UserSessionStore
import com.arvian.jelajahkota.core.data.supabase.SupabaseClientProvider
import com.arvian.jelajahkota.core.data.supabase.SupabaseConfig
import com.arvian.jelajahkota.core.data.supabase.repository.SupabaseAuthRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.repository.SupabaseFavoritesRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.repository.SupabaseGalleryRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.repository.SupabasePlacesRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.repository.SupabasePlansRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.repository.SupabaseProfileRepositoryImpl
import com.arvian.jelajahkota.core.data.supabase.repository.SupabaseReviewsRepositoryImpl
import com.arvian.jelajahkota.core.domain.repository.AuthRepository
import com.arvian.jelajahkota.core.domain.repository.FavoritesRepository
import com.arvian.jelajahkota.core.domain.repository.GalleryRepository
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import com.arvian.jelajahkota.core.domain.repository.PlansRepository
import com.arvian.jelajahkota.core.domain.repository.ProfileRepository
import com.arvian.jelajahkota.core.domain.repository.ReviewsRepository
import com.arvian.jelajahkota.core.domain.repository.UserPreferencesRepository
import com.arvian.jelajahkota.navigation.MainShellCoordinator

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "jelajahkota_preferences")

/**
 * Centralized manual Dependency Injection container for JelajahKota.
 * Automatically activates Supabase-backed repositories when `SUPABASE_URL` and
 * `SUPABASE_PUBLISHABLE_KEY` are configured in `.env` / `local.properties`,
 * falling back cleanly to local demo repositories otherwise.
 */
interface AppContainer {
    val dispatchers: DispatcherProvider
    val isSupabaseEnabled: Boolean
    val mainShellCoordinator: MainShellCoordinator
    val locationProvider: LocationProvider
    val preferencesRepository: UserPreferencesRepository
    val placesRepository: PlacesRepository
    val authRepository: AuthRepository
    val plansRepository: PlansRepository
    val favoritesRepository: FavoritesRepository
    val reviewsRepository: ReviewsRepository
    val profileRepository: ProfileRepository
    val galleryRepository: GalleryRepository
}

class DefaultAppContainer(context: Context) : AppContainer {

    private val appContext = context.applicationContext
    private val supabaseConfig = SupabaseConfig.fromBuildConfig()
    private val supabaseProvider = SupabaseClientProvider(supabaseConfig)
    private val localSessionStore = UserSessionStore()

    override val isSupabaseEnabled: Boolean = supabaseProvider.isConfigured

    override val mainShellCoordinator: MainShellCoordinator = MainShellCoordinator()

    override val locationProvider: LocationProvider by lazy {
        AndroidLocationProvider(appContext)
    }

    private val supabaseAuthRepo: SupabaseAuthRepositoryImpl by lazy {
        SupabaseAuthRepositoryImpl(supabaseProvider.client)
    }

    override val dispatchers: DispatcherProvider = DefaultDispatcherProvider()

    override val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(appContext.userDataStore)
    }

    override val placesRepository: PlacesRepository by lazy {
        if (isSupabaseEnabled) {
            SupabasePlacesRepositoryImpl(supabaseProvider.client)
        } else {
            PlacesRepositoryImpl()
        }
    }

    override val authRepository: AuthRepository by lazy {
        if (isSupabaseEnabled) {
            supabaseAuthRepo
        } else {
            AuthRepositoryImpl(localSessionStore)
        }
    }

    override val plansRepository: PlansRepository by lazy {
        if (isSupabaseEnabled) {
            SupabasePlansRepositoryImpl(supabaseProvider.client, supabaseAuthRepo)
        } else {
            PlansRepositoryImpl()
        }
    }

    override val favoritesRepository: FavoritesRepository by lazy {
        if (isSupabaseEnabled) {
            SupabaseFavoritesRepositoryImpl(supabaseProvider.client, placesRepository, supabaseAuthRepo)
        } else {
            FavoritesRepositoryImpl(placesRepository)
        }
    }

    override val reviewsRepository: ReviewsRepository by lazy {
        if (isSupabaseEnabled) {
            SupabaseReviewsRepositoryImpl(
                client = supabaseProvider.client,
                authRepository = supabaseAuthRepo,
                onReviewsMutated = { placesRepository.refresh() }
            )
        } else {
            ReviewsRepositoryImpl()
        }
    }

    override val profileRepository: ProfileRepository by lazy {
        if (isSupabaseEnabled) {
            SupabaseProfileRepositoryImpl(supabaseProvider.client, supabaseAuthRepo)
        } else {
            ProfileRepositoryImpl(localSessionStore, appContext)
        }
    }

    override val galleryRepository: GalleryRepository by lazy {
        if (isSupabaseEnabled) {
            SupabaseGalleryRepositoryImpl(supabaseProvider.client, supabaseAuthRepo)
        } else {
            GalleryRepositoryImpl(localSessionStore, appContext)
        }
    }
}
