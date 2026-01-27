package com.alpara.beus

import androidx.compose.ui.window.ComposeUIViewController
import com.alpara.beus.Security.TokenManager

fun MainViewController() = ComposeUIViewController {
    TokenManager.getInstance()
    App()
}