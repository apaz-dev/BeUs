package com.alpara.beus.BarNav

import org.jetbrains.compose.resources.DrawableResource

data class BottomNavItem(
    val route: String,
    val icon: DrawableResource,
    val label: String
)