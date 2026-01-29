package com.alpara.beus.Screens

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Rank : Screen("rank")
    object Profile : Screen("profile")
    object Event : Screen("event")
    object Bet : Screen("bet")
}