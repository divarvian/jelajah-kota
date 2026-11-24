package com.arvian.jelajahkota

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.navigation.JelajahKotaAppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as JelajahKotaApp).container

        setContent {
            val appSettings by container.preferencesRepository
                .observeAppSettings()
                .collectAsStateWithLifecycle(initialValue = null)

            LaunchedEffect(appSettings?.darkModeEnabled) {
                val darkModeEnabled = appSettings?.darkModeEnabled ?: return@LaunchedEffect
                if (darkModeEnabled) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
                    )
                } else {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    )
                }
            }

            val resolvedSettings = appSettings ?: return@setContent
            JelajahKotaAppRoot(
                container = container,
                appSettings = resolvedSettings
            )
        }
    }
}
