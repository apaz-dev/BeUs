package com.alpara.beus.Themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    background         = Color(0xFFF4F5F7),
    surface            = Color(0xFFFFFFFF),
    onSurface          = Color(0xFF111318),
    onSurfaceVariant   = Color(0xFF6B7280),
    outline            = Color(0xFFD1D5DB),
    outlineVariant     = Color(0xFFE5E7EB),
    surfaceVariant     = Color(0xFFE5E7EB),
    primary            = Color(0xFF111318),
    onPrimary          = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
    background         = Color(0xFF0F1117),
    surface            = Color(0xFF1C1E26),
    onSurface          = Color(0xFFF0F0F0),
    onSurfaceVariant   = Color(0xFF9CA3AF),
    outline            = Color(0xFF374151),
    outlineVariant     = Color(0xFF374151),
    surfaceVariant     = Color(0xFF2C2F3A),
    primary            = Color(0xFFE5E7EB),
    onPrimary          = Color(0xFF111318),
)

@Composable
fun AppTheme(
    darkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkMode) DarkColors else LightColors,
        content = content
    )
}

object AppColors {
    val accentColor: Color
        @Composable get() {
            val isDark = MaterialTheme.colorScheme.background.red < 0.5f
            return if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
        }

    val accentColor2: Color
        @Composable get() {
            val isDark = MaterialTheme.colorScheme.background.red < 0.5f
            return if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
        }

    val glassBase: Color
        @Composable get() {
            val isDark = MaterialTheme.colorScheme.background.red < 0.5f
            return if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
        }

    val borderGlass: Color
        @Composable get() {
            val isDark = MaterialTheme.colorScheme.background.red < 0.5f
            return if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
        }

    val bgColor: Color
        @Composable get() = MaterialTheme.colorScheme.background
}

