package com.alpara.beus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import beus.composeapp.generated.resources.Res
import beus.composeapp.generated.resources.ico_add
import beus.composeapp.generated.resources.ico_bet
import beus.composeapp.generated.resources.ico_calendar
import beus.composeapp.generated.resources.ico_event
import beus.composeapp.generated.resources.ico_home
import beus.composeapp.generated.resources.ico_profile
import beus.composeapp.generated.resources.ico_rank
import beus.composeapp.generated.resources.compose_multiplatform
import com.alpara.beus.Screens.BetScreen
import com.alpara.beus.Screens.CalendarScreen
import com.alpara.beus.Screens.EventScreen
import com.alpara.beus.Screens.HomeScreen
import com.alpara.beus.Screens.LoginScreen
import com.alpara.beus.Screens.ProfileScreen
import com.alpara.beus.Screens.RankScreen
import org.jetbrains.compose.resources.DrawableResource


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Rank : Screen("rank")
    object Profile : Screen("profile")
    object Event : Screen("event")
    object Bet : Screen("bet")
}
data class BottomNavItem(
    val route: String,
    val icon: DrawableResource,
    val label: String
)

@Composable
fun AddFloatingMenu(
    onEventClick: () -> Unit,
    onBetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(bottom = 80.dp)
            .clip(RoundedCornerShape(20.dp))
            //.padding(8.dp),
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuButton(
            icon = Res.drawable.ico_event,
            label = "Event",
            onClick = onEventClick
        )
        MenuButton(
            icon = Res.drawable.ico_bet,
            label = "Bet",
            onClick = onBetClick
        )
    }
}


@Composable
fun MenuButton(
    icon: DrawableResource,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .shadow(3.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun NavigationBarItem(
    icon: DrawableResource,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(26.dp),
            tint = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FloatingBottomNavigationBar(
    currentRoute: String,
    showAddMenu: Boolean,
    onNavigate: (String) -> Unit,
    onMenuItemClick: (String) -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, Res.drawable.ico_home, "Home"),
        BottomNavItem(Screen.Calendar.route, Res.drawable.ico_calendar, "Calendar"),
        BottomNavItem("add", Res.drawable.ico_add, "Add"),
        BottomNavItem(Screen.Rank.route, Res.drawable.ico_rank, "Rank"),
        BottomNavItem(Screen.Profile.route, Res.drawable.ico_profile, "Profile")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = showAddMenu,
            enter = fadeIn(animationSpec = tween(200)) +
                    scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200)) +
                    scaleOut(targetScale = 0.8f, animationSpec = tween(200)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AddFloatingMenu(
                onEventClick = { onMenuItemClick(Screen.Event.route) },
                onBetClick = { onMenuItemClick(Screen.Bet.route) }
            )
        }
        Surface(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(55.dp)),
            shape = RoundedCornerShape(55.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    if (item.route == "add") {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .clickable { onNavigate(item.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(32.dp),
                                tint = if (showAddMenu) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    } else {
                        NavigationBarItem(
                            icon = item.icon,
                            label = item.label,
                            selected = currentRoute == item.route,
                            onClick = { onNavigate(item.route) }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun MainNav(navController: NavHostController){
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
    ) {
        paddingValues ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier
                //.padding(paddingValues)
                //.safeContentPadding()
        )
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
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
            ProfileScreen()
        }
        composable(Screen.Event.route) {
            EventScreen()
        }
        composable(Screen.Bet.route) {
            BetScreen()
        }
    }
}

@Composable
@Preview
fun App() {
    val authState = remember { AuthState() }

    MaterialTheme {
        if (authState.isLoggedIn) {
            val navController = rememberNavController()
            MainNav(navController = navController)
        } else {
            LoginScreen(
                onLoginSuccess = {
                    authState.login()
                }
            )
        }
    }
}