package com.alpara.beus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alpara.beus.Screens.Add.BetScreen
import com.alpara.beus.Screens.Main.CalendarScreen
import com.alpara.beus.Screens.Add.EventScreen
import com.alpara.beus.Screens.Main.HomeScreen
import com.alpara.beus.Screens.Auth.LoginScreen
import com.alpara.beus.Screens.Main.ProfileScreen
import com.alpara.beus.Screens.Main.RankScreen
import com.alpara.beus.Screens.Auth.SignUpScreen
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_add
import com.alpara.beus.resources.ico_bet
import com.alpara.beus.resources.ico_calendar
import com.alpara.beus.resources.ico_event
import com.alpara.beus.resources.ico_home
import com.alpara.beus.resources.ico_profile
import com.alpara.beus.resources.ico_rank

import org.jetbrains.compose.resources.DrawableResource

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel() }
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) "main" else "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        authViewModel.login()
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onSignup = {
                        navController.navigate("signup")
                    }
                )
            }

            composable("main") {
                val mainNavController = rememberNavController()
                MainNav(
                    navController = mainNavController,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("signup") {
                SignUpScreen(
                     onSignupSuccess = {
                        navController.navigate("main") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            }
        }
    }
