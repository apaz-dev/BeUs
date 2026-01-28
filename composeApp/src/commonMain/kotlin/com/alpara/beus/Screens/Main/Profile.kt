package com.alpara.beus.Screens.Main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alpara.beus.resources.Res
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.BackgroundColor
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.ico_home
import com.alpara.beus.resources.ico_rightarrow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onHomeBack: () -> Unit = {},
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Español") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(20.dp) // Reducir el padding general
    ) {

        // Flecha de retroceso y el texto "Perfil"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), // Ajustar top padding
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ico_arrowleft),
                contentDescription = "Flecha",
                modifier = Modifier
                    .size(30.dp) // Ajustar el tamaño del icono
                    .clickable(onClick = onHomeBack)
            )

            Spacer(Modifier.width(10.dp)) // Reducir espacio

            Text(
                text = "Perfil",
                style = AppTypo.heading(),
                fontSize = 22.sp, // Reducir tamaño de texto
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp)) // Reducir espacio general

        // Imagen de perfil (círculo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ico_home),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(90.dp) // Reducir tamaño de la imagen
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        Spacer(Modifier.height(10.dp)) // Reducir espacio

        // Nombre del usuario
        Text(
            text = "PEPE",
            style = AppTypo.heading(),
            fontSize = 26.sp, // Reducir tamaño del texto
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(6.dp)) // Reducir espacio

        // Correo electrónico del usuario
        Text(
            text = "pepe_email@correo.com",
            style = AppTypo.body(),
            fontSize = 14.sp, // Reducir tamaño del texto
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(14.dp)) // Reducir espacio

        // Agrupar opciones de "Editar datos" en un Box más compacto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp, horizontal = 12.dp)  // Ajustar padding
        ) {
            ProfileCard(
                text = "Editar datos",
                onClick = onEditClick
            )
        }

        Spacer(Modifier.height(12.dp)) // Aumentar espacio entre las cajas

        // Cambiar contraseña
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp, horizontal = 12.dp)
        ) {
            ProfileCard(
                text = "Cambiar contraseña",
                onClick = onChangePasswordClick
            )
        }

        Spacer(Modifier.height(12.dp)) // Reducir espacio

        // Opciones de configuración con menor espacio entre ellas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
                .padding(6.dp) // Ajustar padding
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileCard(
                    text = "Configuración",
                    onClick = { /* Acción de configuración */ }
                )

                Spacer(Modifier.height(2.dp)) // Reducir espacio entre elementos

                ProfileCard(
                    text = "Idioma: $selectedLanguage",
                    onClick = { /* Lógica para cambiar idioma */ }
                )

                Spacer(Modifier.height(2.dp)) // Reducir espacio entre elementos

                ProfileCardSwitch(
                    text = "Notificaciones",
                    isChecked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )

                Spacer(Modifier.height(2.dp)) // Reducir espacio entre elementos

                ProfileCardSwitch(
                    text = "Modo oscuro",
                    isChecked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }
        }

        Spacer(Modifier.height(14.dp)) // Reducir espacio

        // Cerrar sesión y cerrar cuenta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileCard(
                    text = "Cerrar sesión",
                    onClick = onLogout
                )

                Spacer(Modifier.height(4.dp))

                ProfileCard(
                    text = "Cerrar cuenta",
                    onClick = onDeleteAccount
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 1.dp),  // Eliminar borde interno
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF0F0F0))  // Eliminar borde interno
    ) {
        Row(
            modifier = Modifier
                .padding(9.dp),  // Reducir el padding dentro de las tarjetas
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = AppTypo.body(),
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(Res.drawable.ico_rightarrow),  // Usar icono adecuado
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProfileCardSwitch(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF0F0F0))  // Eliminar borde interno
    ) {
        Row(
            modifier = Modifier
                .padding(2.dp),  // Reducir el padding dentro de las tarjetas
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = AppTypo.body(),
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}






















