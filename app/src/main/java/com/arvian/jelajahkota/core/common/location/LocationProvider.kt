package com.arvian.jelajahkota.core.common.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserCoordinates(
    val latitude: Double,
    val longitude: Double
)

/**
 * Privacy-friendly, optional coarse location provider using Android framework [LocationManager].
 * Never requests permissions automatically; callers trigger [refreshLocation] only after explicit
 * user consent (Just-in-Time in Settings or "Terdekat" sort).
 */
interface LocationProvider {
    val userLocation: StateFlow<UserCoordinates?>
    fun hasLocationPermission(): Boolean
    fun refreshLocation()
    fun clearLocation()
}

class NoOpLocationProvider(
    private val initialLocation: UserCoordinates? = null
) : LocationProvider {
    private val _userLocation = MutableStateFlow(initialLocation)
    override val userLocation: StateFlow<UserCoordinates?> = _userLocation.asStateFlow()
    override fun hasLocationPermission(): Boolean = initialLocation != null
    override fun refreshLocation() = Unit
    override fun clearLocation() {
        _userLocation.value = null
    }
}

class AndroidLocationProvider(
    context: Context
) : LocationProvider {

    private val appContext = context.applicationContext
    private val _userLocation = MutableStateFlow<UserCoordinates?>(null)
    override val userLocation: StateFlow<UserCoordinates?> = _userLocation.asStateFlow()

    override fun hasLocationPermission(): Boolean {
        val coarse = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarse || fine
    }

    @SuppressLint("MissingPermission")
    override fun refreshLocation() {
        if (!hasLocationPermission()) {
            _userLocation.value = null
            return
        }
        val manager = appContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return

        val candidateProviders = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLastKnown: Location? = null
        for (provider in candidateProviders) {
            val loc = runCatching {
                if (manager.isProviderEnabled(provider)) manager.getLastKnownLocation(provider) else null
            }.getOrNull()
            if (loc != null && (bestLastKnown == null || loc.time > bestLastKnown.time)) {
                bestLastKnown = loc
            }
        }
        if (bestLastKnown != null) {
            _userLocation.value = UserCoordinates(bestLastKnown.latitude, bestLastKnown.longitude)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activeProvider = candidateProviders.firstOrNull { provider ->
                runCatching { manager.isProviderEnabled(provider) }.getOrDefault(false)
            } ?: return

            runCatching {
                manager.getCurrentLocation(
                    activeProvider,
                    CancellationSignal(),
                    appContext.mainExecutor
                ) { fresh ->
                    if (fresh != null) {
                        _userLocation.value = UserCoordinates(fresh.latitude, fresh.longitude)
                    }
                }
            }
        }
    }

    override fun clearLocation() {
        _userLocation.value = null
    }
}
