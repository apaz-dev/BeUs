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
            .padding(16.dp)
    ) {

        // Flecha de retroceso y el texto "Perfil" al lado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ico_arrowleft),
                contentDescription = "Flecha",
                modifier = Modifier
                    .size(35.dp)
                    .clickable(onClick = onHomeBack)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Perfil",
                style = AppTypo.heading(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

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
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
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

        Spacer(Modifier.height(16.dp))

        // Agrupar opciones de "Editar datos" en un Box más compacto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))  // Solo borde exterior
                .padding(vertical = 4.dp, horizontal = 8.dp)  // Reducir el padding
        ) {
            ProfileCard(
                text = "Editar datos",
                onClick = onEditClick
            )
        }

        Spacer(Modifier.height(8.dp))

        // Agrupar opciones de "Cambiar contraseña" en un Box más compacto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))  // Solo borde exterior
                .padding(vertical = 4.dp, horizontal = 8.dp)  // Reducir el padding
        ) {
            ProfileCard(
                text = "Cambiar contraseña",
                onClick = onChangePasswordClick
            )
        }

        Spacer(Modifier.height(16.dp))

        // Agrupar opciones de Configuración, Idioma, Notificaciones y Modo Oscuro dentro de un Box con bordes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))  // Solo borde exterior
                .padding(8.dp)  // Mantener el padding para que no se pegue al texto
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileCard(
                    text = "Configuración",
                    onClick = { /* Acción de configuración */ }
                )

                Spacer(Modifier.height(8.dp))

                ProfileCard(
                    text = "Idioma: $selectedLanguage",
                    onClick = { /* Lógica para cambiar idioma */ }
                )

                Spacer(Modifier.height(8.dp))

                ProfileCardSwitch(
                    text = "Notificaciones",
                    isChecked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )

                Spacer(Modifier.height(8.dp))

                ProfileCardSwitch(
                    text = "Modo oscuro",
                    isChecked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Agrupar opciones de Cerrar sesión y Cerrar cuenta en Card individuales
        ProfileCard(
            text = "Cerrar sesión",
            onClick = onLogout
        )

        Spacer(Modifier.height(8.dp))

        ProfileCard(
            text = "Cerrar cuenta",
            onClick = onDeleteAccount
        )
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
                .padding(12.dp),  // Reducir el padding dentro de las tarjetas
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = AppTypo.body(),
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(Res.drawable.ico_home),  // Usar icono adecuado
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
                .padding(8.dp),  // Reducir el padding dentro de las tarjetas
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

















