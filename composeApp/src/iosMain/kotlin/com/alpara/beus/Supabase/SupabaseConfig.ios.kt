package com.alpara.beus.Supabase

import platform.Foundation.NSBundle

actual object SupabaseConfig {
    actual val supabaseUrl: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_URL") as? String ?: ""
    actual val supabaseAnonKey: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_ANON_KEY") as? String ?: ""
}
