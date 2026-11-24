package com.arvian.jelajahkota.core.data.supabase

import com.arvian.jelajahkota.BuildConfig

data class SupabaseConfig(
    val url: String,
    val publishableKey: String
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() &&
            publishableKey.isNotBlank() &&
            !url.contains("your-project-ref", ignoreCase = true) &&
            !publishableKey.contains("replace_me", ignoreCase = true) &&
            !publishableKey.contains("placeholder", ignoreCase = true)

    companion object {
        fun fromBuildConfig(): SupabaseConfig = SupabaseConfig(
            url = BuildConfig.SUPABASE_URL.trim(),
            publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY.trim()
        )
    }
}
