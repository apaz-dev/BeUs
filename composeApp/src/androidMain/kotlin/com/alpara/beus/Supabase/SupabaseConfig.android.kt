package com.alpara.beus.Supabase

import com.alpara.beus.BuildConfig

actual object SupabaseConfig {
    actual val supabaseUrl: String = BuildConfig.SUPABASE_URL
    actual val supabaseAnonKey: String = BuildConfig.SUPABASE_ANON_KEY
}
