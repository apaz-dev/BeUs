package com.alpara.beus.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Cliente singleton de Supabase para la aplicación.
 * 
 * Configuración requerida:
 * - Reemplazar TU_SUPABASE_URL con la URL de tu proyecto Supabase
 * - Reemplazar TU_SUPABASE_ANON_KEY con tu clave anon/public de Supabase
 */
object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "",
        supabaseKey = ""
    ) {
        install(Auth)
        install(Postgrest)
    }
}
