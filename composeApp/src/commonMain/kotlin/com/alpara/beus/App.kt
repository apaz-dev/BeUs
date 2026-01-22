package com.alpara.beus


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpara.beus.BarNav.MainNav
import com.alpara.beus.Models.AuthViewModel
import com.alpara.beus.Screens.Auth.LoginScreen
import com.alpara.beus.Screens.Auth.SignUpScreen

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
