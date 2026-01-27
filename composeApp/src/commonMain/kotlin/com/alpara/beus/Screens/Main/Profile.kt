package com.alpara.beus.Screens.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alpara.beus.resources.Res
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.BackgroundColor
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.ico_home
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onHomeBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {

        // Flecha de retroceso y el texto "Perfil" al lado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),  // Espacio desde la parte superior
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flecha de retroceso
            Icon(
                painter = painterResource(Res.drawable.ico_arrowleft),
                contentDescription = "Flecha",
                modifier = Modifier
                    .size(35.dp)
                    .clickable(onClick = onHomeBack)  // Llama al método onHomeBack al hacer clic
            )

            Spacer(Modifier.width(8.dp))  // Espacio entre la flecha y el texto "Perfil"

            // Texto "Perfil"
            Text(
                text = "Perfil",
                style = AppTypo.heading(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Espacio adicional en la parte superior
        Spacer(Modifier.height(32.dp))

        // Imagen de perfil (círculo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ico_home),  // Asegúrate de usar el icono correcto
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(100.dp)  // Tamaño del icono
                    .clip(CircleShape)  // Redondear el icono
                    .background(Color.Gray)  // Fondo gris mientras se carga el ícono
            )
        }

        Spacer(Modifier.height(16.dp))

        // Nombre del usuario
        Text(
            text = "PEPE",
            style = AppTypo.heading(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(8.dp))

        // Correo electrónico del usuario
        Text(
            text = "pepe_email@correo.com",
            style = AppTypo.body(),
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        // Botón de Editar datos
        ProfileButton(
            icon = painterResource(Res.drawable.ico_home),
            text = "Editar datos",
            onClick = onEditClick
        )

        Spacer(Modifier.height(12.dp))

        // Botón de Cambiar contraseña
        ProfileButton(
            icon = painterResource(Res.drawable.ico_home),
            text = "Cambiar contraseña",
            onClick = onChangePasswordClick
        )

        Spacer(Modifier.height(12.dp))

        // Configuración
        ProfileButton(
            icon = painterResource(Res.drawable.ico_home),
            text = "Configuración",
            onClick = { /* Acción de configuración */ }
        )

        Spacer(Modifier.height(12.dp))

        // Botón de Cerrar sesión
        ProfileButton(
            icon = painterResource(Res.drawable.ico_home),
            text = "Cerrar sesión",
            onClick = onLogout
        )

        Spacer(Modifier.height(12.dp))

        // Botón de Cerrar cuenta
        ProfileButton(
            icon = painterResource(Res.drawable.ico_home),
            text = "Cerrar cuenta",
            onClick = onDeleteAccount
        )
    }
}

@Composable
fun ProfileButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 16.dp)
        )

        Text(
            text = text,
            style = AppTypo.body(),
            modifier = Modifier.weight(1f)
        )
    }
}



