package com.alpara.beus.BarNav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alpara.beus.Screens.Add.BetScreen
import com.alpara.beus.Screens.Add.EventScreen
import com.alpara.beus.Screens.Main.CalendarScreen
import com.alpara.beus.Screens.Main.HomeScreen
import com.alpara.beus.Screens.Main.ProfileScreen
import com.alpara.beus.Screens.Main.RankScreen
import com.alpara.beus.Screens.Screen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }
        composable(Screen.Rank.route) {
            RankScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onLogout = onLogout)
        }
        composable(Screen.Event.route) {
            EventScreen()
        }
        composable(Screen.Bet.route) {
            BetScreen()
        }
    }
}