package com.alpara.beus.BarNav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alpara.beus.NavigationGraph
import com.alpara.beus.Screens.Add.BetScreen
import com.alpara.beus.Screens.Add.EventScreen
import com.alpara.beus.Screens.Main.CalendarScreen
import com.alpara.beus.Screens.Main.HomeScreen
import com.alpara.beus.Screens.Main.ProfileScreen
import com.alpara.beus.Screens.Main.RankScreen


@Composable
fun MainNav(
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
    var showAddMenu by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            FloatingBottomNavigationBar(
                currentRoute = currentRoute ?: Screen.Home.route,
                showAddMenu = showAddMenu,
                onNavigate = { route ->
                    if (route == "add") {
                        showAddMenu = !showAddMenu
                    } else {
                        showAddMenu = false
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onMenuItemClick = { route ->
                    showAddMenu = false
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavigationGraph(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier
        )
    }
}