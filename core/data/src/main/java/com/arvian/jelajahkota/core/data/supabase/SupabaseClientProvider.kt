package com.arvian.jelajahkota.core.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.android.Android

class SupabaseClientProvider(
    private val config: SupabaseConfig = SupabaseConfig.DEFAULT_LOCAL
) {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = config.url,
            supabaseKey = config.publishableKey
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
            httpEngine = Android.create()
        }
    }
}
