package com.alpara.beus


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpara.beus.BarNav.MainNav
import com.alpara.beus.Models.AuthViewModel
import com.alpara.beus.Network.ApiClient
import com.alpara.beus.Screens.Auth.LoginScreen
import com.alpara.beus.Screens.Auth.SignUpScreen
import com.alpara.beus.Security.createTokenManager

@Composable
@Preview
fun App() {
    val tokenManager = remember { createTokenManager() }
    
    // Initialize ApiClient immediately, not in LaunchedEffect
    remember {
        ApiClient.initialize(tokenManager)
    }
    
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(tokenManager) }
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Check auth status on app start
    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus()
    }

    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) "main" else "login"
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
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
                    viewModel = authViewModel,
                    onSignupSuccess = {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onLoginBack = {
                        navController.navigate("login"){
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            }
        }
    }
