package com.alpara.beus.Supabase

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createSupabaseHttpEngine(): HttpClientEngine = OkHttp.create()
