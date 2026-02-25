package com.alpara.beus.Themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores estáticos (legados - no usan tema)
val ColorYellow = Color(0xFFFFD764)

// Aliases composable → usan MaterialTheme y reaccionan al modo oscuro
val BackgroundColor: Color
    @Composable get() = MaterialTheme.colorScheme.background

val textPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

val textSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val cardColor: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val ColorBlack: Color
    @Composable get() = MaterialTheme.colorScheme.onBackground

val ColorWhite: Color
    @Composable get() = MaterialTheme.colorScheme.background
