package com.alpara.beus.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Add : Screen("add")
    object Rank : Screen("rank")
    object Profile : Screen("profile")
    object Bet : Screen("bet")
    object Event : Screen("event")

}