package com.alpara.beus.BarNav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alpara.beus.BarNav.Floating.FloatingBottomNavigationBar
import com.alpara.beus.BarNav.MiniFloating.AddFloatingMenu
import com.alpara.beus.Screens.Screen


@Composable
fun MainNav(
    navController: NavHostController,
    onLogout: () -> Unit = {},
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    var showAddMenu by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rutas donde NO debe mostrarse la barra de navegación inferior
    val routesWithoutBottomBar = listOf(
        Screen.PhotoGallery.route.substringBefore("/"),
        Screen.Event.route.substringBefore("/"),
        Screen.Configuration.route
    )
    val showBottomBar = routesWithoutBottomBar.none { prefix ->
        currentRoute?.startsWith(prefix) == true
    }

    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {}
    ) { paddingValues ->
        NavigationGraph(
            navController = navController,
            onLogout = onLogout,
            darkMode = darkMode,
            onDarkModeChange = onDarkModeChange,
            modifier = Modifier
                .padding(paddingValues)
                .haze(hazeState)
        )
    }

    if (showBottomBar) {
        FloatingBottomNavigationBar(
            currentRoute = currentRoute ?: Screen.Home.route,
            showAddMenu = showAddMenu,
            hazeState = hazeState,
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
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showBottomBar) {
        AnimatedVisibility(
            visible = showAddMenu,
            enter = fadeIn(animationSpec = tween(200)) +
                    scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200)) +
                    scaleOut(targetScale = 0.8f, animationSpec = tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            AddFloatingMenu(
                onEventClick = {
                    showAddMenu = false
                    val route = Screen.Event.createRoute(ActiveTeamArgs.teamId)
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBetClick = {
                    showAddMenu = false
                    navController.navigate(Screen.Bet.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
    } // cierra Box exterior
}