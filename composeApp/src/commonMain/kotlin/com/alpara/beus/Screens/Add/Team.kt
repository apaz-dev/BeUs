package com.alpara.beus.Screens.Add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Themes.AppTypo
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme

@Preview
@Composable
fun TeamModalPreview() {
    TeamModal(
        showDialog = true,
        onDismiss = {},
        onCreateTeam = { },
        onJoinTeam = { }
    )
}

@Composable
fun TeamModal(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCreateTeam: (String) -> Unit,
    onJoinTeam: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Crear, 1 = Unirse
    var teamName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Limpiar campos al cerrar
                teamName = ""
                joinCode = ""
                errorMessage = ""
                selectedTab = 0
                onDismiss()
            },
            title = {
                Text(
                    text = "Gestionar Equipo",
                    style = AppTypo.heading(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tabs para cambiar entre Crear y Unirse
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                errorMessage = ""
                            },
                            text = {
                                Text(
                                    text = "Crear",
                                    style = AppTypo.body(),
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                errorMessage = ""
                            },
                            text = {
                                Text(
                                    text = "Unirse",
                                    style = AppTypo.body(),
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contenido según la pestaña seleccionada
                    when (selectedTab) {
                        0 -> {
                            // Crear equipo
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Nombre del equipo",
                                    style = AppTypo.body(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                OutlinedTextField(
                                    value = teamName,
                                    onValueChange = {
                                        teamName = it
                                        errorMessage = ""
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Ej: Los Invencibles",
                                            style = AppTypo.body()
                                        )
                                    },
                                    textStyle = AppTypo.body(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    isError = errorMessage.isNotEmpty() && selectedTab == 0
                                )

                                if (errorMessage.isNotEmpty() && selectedTab == 0) {
                                    Text(
                                        text = errorMessage,
                                        color = Color.Red,
                                        style = AppTypo.body(),
                                        fontSize = 12.sp
                                    )
                                }

                                Text(
                                    text = "Se generará un código automáticamente para que otros puedan unirse",
                                    style = AppTypo.body(),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        1 -> {
                            // Unirse a equipo
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Código de equipo",
                                    style = AppTypo.body(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                OutlinedTextField(
                                    value = joinCode,
                                    onValueChange = {
                                        joinCode = it.uppercase()
                                        errorMessage = ""
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Ej: ABC123",
                                            style = AppTypo.body()
                                        )
                                    },
                                    textStyle = AppTypo.body(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    isError = errorMessage.isNotEmpty() && selectedTab == 1
                                )

                                if (errorMessage.isNotEmpty() && selectedTab == 1) {
                                    Text(
                                        text = errorMessage,
                                        color = Color.Red,
                                        style = AppTypo.body(),
                                        fontSize = 12.sp
                                    )
                                }

                                Text(
                                    text = "Introduce el código que te compartió el administrador del equipo",
                                    style = AppTypo.body(),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (selectedTab) {
                            0 -> {
                                // Validar nombre del equipo
                                if (teamName.isBlank()) {
                                    errorMessage = "El nombre del equipo no puede estar vacío"
                                } else if (teamName.length < 3) {
                                    errorMessage = "El nombre debe tener al menos 3 caracteres"
                                } else {
                                    onCreateTeam(teamName)
                                    teamName = ""
                                    errorMessage = ""
                                    onDismiss()
                                }
                            }
                            1 -> {
                                // Validar código de equipo
                                if (joinCode.isBlank()) {
                                    errorMessage = "El código no puede estar vacío"
                                } else if (joinCode.length < 6) {
                                    errorMessage = "El código debe tener al menos 6 caracteres"
                                } else {
                                    onJoinTeam(joinCode)
                                    joinCode = ""
                                    errorMessage = ""
                                    onDismiss()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (selectedTab == 0) "Crear" else "Unirse",
                        style = AppTypo.body(),
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        teamName = ""
                        joinCode = ""
                        errorMessage = ""
                        selectedTab = 0
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        style = AppTypo.body(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
