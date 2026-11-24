package com.arvian.jelajahkota.core.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.android.Android
import kotlinx.serialization.json.Json

class SupabaseClientProvider(
    private val config: SupabaseConfig = SupabaseConfig.fromBuildConfig()
) {
    val isConfigured: Boolean get() = config.isConfigured

    val client: SupabaseClient by lazy {
        require(config.isConfigured) {
            "Supabase URL and publishable key must be configured via .env or local.properties."
        }
        createSupabaseClient(
            supabaseUrl = config.url,
            supabaseKey = config.publishableKey
        ) {
            defaultSerializer = KotlinXSerializer(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    encodeDefaults = true
                }
            )
            install(Postgrest)
            install(Auth)
            install(Storage)
            httpEngine = Android.create()
        }
    }
}
