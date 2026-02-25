package com.alpara.beus


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpara.beus.BarNav.MainNav
import com.alpara.beus.Models.View.AuthViewModel
import com.alpara.beus.Backend.ApiClient
import com.alpara.beus.Screens.Add.EventScreenCall
import com.alpara.beus.Screens.Auth.LoginScreen
import com.alpara.beus.Screens.Auth.SignUpScreen
import com.alpara.beus.Screens.Screen
import com.alpara.beus.Screens.Splash.SplashScreen
import com.alpara.beus.Security.createTokenManager
import com.alpara.beus.Themes.AppTheme

@Composable
@Preview
fun App() {
    val tokenManager = remember { createTokenManager() }

    LaunchedEffect(tokenManager) {
        ApiClient.initialize(tokenManager)
    }

    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(tokenManager) }

    var darkMode by remember { mutableStateOf(false) }

    AppTheme(darkMode = darkMode) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    viewModel = authViewModel,
                    onNavigateToMain = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onSignup = {
                        navController.navigate(Screen.Signup.route)
                    }
                )
            }

            composable(Screen.Main.route) {
                val mainNavController = rememberNavController()
                MainNav(
                    navController = mainNavController,
                    darkMode = darkMode,
                    onDarkModeChange = { darkMode = it },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Signup.route) {
                SignUpScreen(
                    viewModel = authViewModel,
                    onSignupSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLoginBack = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
