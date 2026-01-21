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
        supabaseUrl = "https://gugmuwomahpqrdritgtx.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd1Z211d29tYWhwcXJkcml0Z3R4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4OTczOTAsImV4cCI6MjA4NDQ3MzM5MH0.-vj_JH-sodsZDbuZ-P-dpFGns-EwwZWo5TvYn6ihb3Q"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
