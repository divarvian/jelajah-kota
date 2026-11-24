package com.arvian.jelajahkota

import android.app.Application
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.di.DefaultAppContainer

class JelajahKotaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
