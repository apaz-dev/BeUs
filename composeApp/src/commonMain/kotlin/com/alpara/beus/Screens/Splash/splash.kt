package com.alpara.beus.Screens.Splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Models.View.AuthViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_logo
import com.alpara.beus.resources.ico_logo2
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToMain: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAuthStatus()

        // Esperar en paralelo: mínimo 3 s de splash Y que termine la verificación
        coroutineScope {
            val timerJob = async { delay(3000L) }
            val authJob = async { viewModel.isCheckingAuth.first { !it } }
            timerJob.await()
            authJob.await()
        }

        if (isAuthenticated) {
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp)
    ) {
        // Tarjeta principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 40.dp, horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo de la aplicación
                Image(
                    painter = painterResource(if (isDark) Res.drawable.ico_logo2 else Res.drawable.ico_logo),
                    contentDescription = null,
                    modifier = Modifier.size(140.dp)
                )

                Spacer(Modifier.height(12.dp))

                // Nombre de la app
                Text(
                    text = "BeUs",
                    style = AppTypo.heading(),

                    )

                Spacer(Modifier.height(20.dp))
            }
        }

        val versionText = "Versión 1.0.0"
        // Aquí es donde colocamos la versión, alineada abajo de todo
        Text(
            text = versionText,
            style = AppTypo.body(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}