package com.alpara.beus.BarNav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alpara.beus.Screens.Add.BetScreen
import com.alpara.beus.Screens.Add.EventScreenCall
import com.alpara.beus.Screens.Main.CalendarScreen
import com.alpara.beus.Screens.Main.ConfigurationScreen
import com.alpara.beus.Screens.Main.HomeScreen
import com.alpara.beus.Screens.Main.PhotoGalleryScreen
import com.alpara.beus.Screens.Main.ProfileScreen
import com.alpara.beus.Screens.Main.RankScreen
import com.alpara.beus.Screens.Main.TeamDetailScreen
import com.alpara.beus.Screens.Screen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onLogout: () -> Unit = {},
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenGallery = { teamId, eventId, eventName ->
                    GalleryNavArgs.teamId = teamId
                    GalleryNavArgs.eventId = eventId
                    GalleryNavArgs.eventName = eventName
                    navController.navigate(
                        Screen.PhotoGallery.createRoute(teamId, eventId, eventName)
                    )
                }
            )
        }
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }
        composable(Screen.Rank.route) {
            RankScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onOpenConfiguration = {
                    navController.navigate(Screen.Configuration.route)
                },
                onOpenTeamDetail = { teamId ->
                    TeamDetailNavArgs.teamId = teamId
                    navController.navigate(Screen.TeamDetail.createRoute(teamId))
                }
            )
        }
        composable(Screen.Configuration.route) {
            ConfigurationScreen(
                onHomeBack = { navController.popBackStack() },
                onLogout = onLogout,
                onEditClick = { /* TODO */ },
                onChangePasswordClick = { /* TODO */ },
                onDeleteAccount = { /* TODO */ },
                darkModeEnabled = darkMode,
                onDarkModeChange = onDarkModeChange
            )
        }
        composable(
            route = Screen.Event.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            EventScreenCall(
                teamId = ActiveTeamArgs.teamId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Bet.route) {
            BetScreen()
        }
        composable(
            route = Screen.TeamDetail.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            TeamDetailScreen(
                teamId = TeamDetailNavArgs.teamId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.PhotoGallery.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType; defaultValue = "" },
                navArgument("eventId") { type = NavType.StringType; defaultValue = "" },
                navArgument("eventName") { type = NavType.StringType; defaultValue = "Galería" }
            )
        ) { _ ->
            PhotoGalleryScreen(
                teamId = GalleryNavArgs.teamId,
                eventId = GalleryNavArgs.eventId,
                eventName = GalleryNavArgs.eventName.ifBlank { "Galería" },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
