package com.arvian.jelajahkota

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.allowHardware
import coil3.request.crossfade
import com.arvian.jelajahkota.di.AppContainer
import com.arvian.jelajahkota.di.DefaultAppContainer

class JelajahKotaApp : Application(), SingletonImageLoader.Factory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .allowHardware(true)
            .crossfade(false)
            .build()
}
