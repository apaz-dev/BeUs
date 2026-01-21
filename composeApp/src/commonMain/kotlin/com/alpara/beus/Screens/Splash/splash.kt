package com.alpara.beus.Screens.Splash
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.BackgroundColor
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_home
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SplashScreen(onNextScreen: () -> Unit = {}){
    // Tres segunditos buenos de espera en el splash

    LaunchedEffect(Unit) {
        delay(3000) // Espera 3 segundos
        onNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // Gris suave
            .padding(horizontal = 28.dp)
    ) {
        // Tarjeta principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .background(BackgroundColor)
                .padding(vertical = 40.dp, horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo de la aplicación
                Image(
                    painter = painterResource(Res.drawable.ico_home),
                    contentDescription = null,
                    modifier = Modifier.size(96.dp)
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
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Aquí ya no hay conflicto
                .padding(bottom = 40.dp) // Ajustamos el espacio inferior
        )
    }
}
