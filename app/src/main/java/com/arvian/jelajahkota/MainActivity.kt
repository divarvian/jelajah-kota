package com.arvian.jelajahkota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arvian.jelajahkota.navigation.JelajahKotaAppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as JelajahKotaApp).container

        setContent {
            JelajahKotaAppRoot(container = container)
        }
    }
}
