package com.alpara.beus.Supabase

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createSupabaseHttpEngine(): HttpClientEngine = Darwin.create()
