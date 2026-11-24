package com.arvian.jelajahkota.core.data.supabase

data class SupabaseConfig(
    val url: String,
    val publishableKey: String
) {
    companion object {
        // Safe default placeholder for local Supabase emulator development (10.0.2.2 points to host machine from Android emulator)
        val DEFAULT_LOCAL = SupabaseConfig(
            url = "http://10.0.2.2:54321",
            publishableKey = "sbp_placeholder_publishable_key_local"
        )
    }
}
