package com.alpara.beus.Screens

sealed class Screen(val route: String) {
    // Rutas raíz
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Main : Screen("main")

    // Rutas internas de la navegación principal
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Rank : Screen("rank")
    object Profile : Screen("profile")
    object Configuration : Screen("configuration")
    object Event : Screen("event/{teamId}") {
        fun createRoute(teamId: String) = "event/$teamId"
    }
    object Bet : Screen("bet")
    // Galería de fotos de un evento
    object PhotoGallery : Screen("photo_gallery/{teamId}/{eventId}/{eventName}") {
        fun createRoute(teamId: String, eventId: String, eventName: String) =
            "photo_gallery/$teamId/$eventId/$eventName"
    }
}