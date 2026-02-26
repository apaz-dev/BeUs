package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Models.ProfilePrivate
import com.alpara.beus.Models.ProfileTeam
import com.alpara.beus.Models.View.ProfileState
import com.alpara.beus.Models.View.ProfileViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.account_section
import com.alpara.beus.resources.cancel
import com.alpara.beus.resources.confirm_password
import com.alpara.beus.resources.dark_mode
import com.alpara.beus.resources.delete
import com.alpara.beus.resources.delete_account
import com.alpara.beus.resources.delete_account_message
import com.alpara.beus.resources.delete_account_title
import com.alpara.beus.resources.edit_data
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.ico_eye
import com.alpara.beus.resources.ico_eyeoff
import com.alpara.beus.resources.leave_password_empty
import com.alpara.beus.resources.logout
import com.alpara.beus.resources.name
import com.alpara.beus.resources.new_password
import com.alpara.beus.resources.new_password_optional
import com.alpara.beus.resources.passwordnomatch
import com.alpara.beus.resources.preferences_section
import com.alpara.beus.resources.retry
import com.alpara.beus.resources.save
import com.alpara.beus.resources.session_section
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun ConfigurationScreen(
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onHomeBack: () -> Unit = {},
    darkModeEnabled: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    viewModel: ProfileViewModel = remember { ProfileViewModel() }
) {
    val profileState by viewModel.profileState.collectAsState()

    // Glassmorphism palette
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val accentColor  = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val glassBase    = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass  = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)

    when (profileState) {
        is ProfileState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        }
        is ProfileState.Error -> {
            val message = (profileState as ProfileState.Error).message
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, borderGlass, RoundedCornerShape(20.dp))
                        .background(glassBase.copy(alpha = 0.7f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠ $message", color = Color(0xFFFF6B6B), style = AppTypo.body(), fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.loadProfile() }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text("Reintentar", color = accentColor, style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
        is ProfileState.Success -> {
            val profile = (profileState as ProfileState.Success).profile
            ConfigurationScreenContent(
                profile = profile,
                viewModel = viewModel,
                onLogout = onLogout,
                onDeleteAccount = onDeleteAccount,
                onHomeBack = onHomeBack,
                darkModeEnabled = darkModeEnabled,
                onDarkModeChange = onDarkModeChange
            )
        }
    }
}

@Composable
fun ConfigurationScreenContent(
    profile: ProfilePrivate,
    viewModel: ProfileViewModel,
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onHomeBack: () -> Unit = {},
    darkModeEnabled: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val isUpdating by viewModel.isUpdating.collectAsState()
    val updateError by viewModel.updateError.collectAsState()

    // Glassmorphism palette
    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor     = MaterialTheme.colorScheme.background
    val onSurface   = MaterialTheme.colorScheme.onSurface

    // ── Dialog de confirmación glass ───────────────────────────────────────
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            containerColor = glassBase,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    stringResource(Res.string.delete_account_title),
                    style = AppTypo.heading().copy(
                        brush = Brush.horizontalGradient(colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF9966)))
                    ),
                    fontSize = 22.sp
                )
            },
            text = {
                Text(
                    stringResource(Res.string.delete_account_message),
                    color = textSecondary,
                    style = AppTypo.body(),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF6B6B).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { showDeleteAccountDialog = false; onDeleteAccount() }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(stringResource(Res.string.delete), color = Color(0xFFFF6B6B), style = AppTypo.body().copy(fontWeight = FontWeight.Bold), fontSize = 14.sp)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.10f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { showDeleteAccountDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(stringResource(Res.string.cancel), color = accentColor, style = AppTypo.body().copy(fontWeight = FontWeight.Medium), fontSize = 14.sp)
                }
            }
        )
    }

    // ── Dialog de edición de datos glass ──────────────────────────────────
    if (showEditDialog) {
        EditDataDialog(
            currentUsername = profile.username,
            isUpdating = isUpdating,
            updateError = updateError,
            onDismiss = { showEditDialog = false },
            onSave = { newUsername, newPassword ->
                viewModel.updateProfile(newUsername, newPassword)
                // El modal se cerrará cuando isUpdating vuelva a false y no haya error
            },
            glassBase = glassBase,
            borderGlass = borderGlass,
            accentColor = accentColor,
            accentColor2 = accentColor2,
            onSurface = onSurface,
            textSecondary = textSecondary
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // ── Header con gradiente glass ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (isDark) 0.55f else 0.35f),
                            accentColor2.copy(alpha = if (isDark) 0.45f else 0.25f),
                            glassBase.copy(alpha = if (isDark) 0.3f else 0.6f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            // Línea decorativa inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, borderGlass, Color.Transparent)
                        )
                    )
            )

            // Botón back glass
            Box(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(glassBase.copy(alpha = 0.5f))
                    .border(1.dp, borderGlass, CircleShape)
                    .clickable { onHomeBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ico_arrowleft),
                    contentDescription = "Volver",
                    tint = if (isDark) Color.White.copy(alpha = 0.85f) else accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Avatar con gradiente centrado en el header
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 45.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.linearGradient(colors = listOf(accentColor, accentColor2)))
                    .border(3.dp, bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initials = profile.username.take(2).uppercase()
                Text(
                    text = initials,
                    style = AppTypo.heading(),
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Espaciado para el avatar que sobresale
        Spacer(Modifier.height(52.dp))

        // ── Nombre y email ─────────────────────────────────────────────────
        Text(
            text = profile.username,
            style = AppTypo.heading().copy(
                brush = Brush.horizontalGradient(colors = listOf(accentColor, accentColor2))
            ),
            fontSize = 26.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = profile.email,
            style = AppTypo.body(),
            fontSize = 13.sp,
            color = textSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(28.dp))

        // ── Secciones ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cuenta
            GlassSectionLabel(stringResource(Res.string.account_section), accentColor)
            GlassSection(glassBase, borderGlass) {
                GlassRow(
                    icon = Icons.Filled.Edit,
                    text = stringResource(Res.string.edit_data),
                    accentColor = accentColor,
                    onSurface = onSurface,
                    showChevron = true,
                    onClick = { showEditDialog = true }
                )
            }

            Spacer(Modifier.height(4.dp))

            // Preferencias
            GlassSectionLabel(stringResource(Res.string.preferences_section), accentColor)
            GlassSection(glassBase, borderGlass) {
                GlassSwitchRow(
                    icon = Icons.Filled.DarkMode,
                    text = stringResource(Res.string.dark_mode),
                    accentColor = accentColor,
                    onSurface = onSurface,
                    checked = darkModeEnabled,
                    onCheckedChange = onDarkModeChange,
                    isDark = isDark
                )
            }

            Spacer(Modifier.height(4.dp))

            // Peligro
            GlassSectionLabel(stringResource(Res.string.session_section), accentColor)
            GlassSection(glassBase, borderGlass) {
                GlassRow(
                    icon = Icons.Filled.Logout,
                    text = stringResource(Res.string.logout),
                    accentColor = accentColor,
                    onSurface = onSurface,
                    showChevron = false,
                    onClick = onLogout
                )
                GlassDivider(borderGlass)
                GlassRow(
                    icon = Icons.Filled.Delete,
                    text = stringResource(Res.string.delete_account),
                    accentColor = Color(0xFFFF6B6B),
                    onSurface = Color(0xFFFF6B6B),
                    showChevron = false,
                    onClick = { showDeleteAccountDialog = true }
                )
            }
        }
    }
}

// ─── Componentes Glass reutilizables ──────────────────────────────────────────

@Composable
private fun GlassSectionLabel(text: String, accentColor: Color) {
    Text(
        text = text.uppercase(),
        color = accentColor.copy(alpha = 0.7f),
        style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp),
        fontSize = 11.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
    )
}

@Composable
private fun GlassSection(
    glassBase: Color,
    borderGlass: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(colors = listOf(borderGlass, Color.Transparent, borderGlass)),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(glassBase.copy(alpha = 0.75f), glassBase.copy(alpha = 0.55f))
                )
            ),
        content = content
    )
}

@Composable
private fun GlassRow(
    icon: ImageVector,
    text: String,
    accentColor: Color,
    onSurface: Color,
    showChevron: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono con fondo glass
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = onSurface
        )

        if (showChevron) {
            Text(
                text = "›",
                fontSize = 22.sp,
                color = onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun GlassSwitchRow(
    icon: ImageVector,
    text: String,
    accentColor: Color,
    onSurface: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }

        Spacer(Modifier.width(12.dp))

        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), color = onSurface)

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.9f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                checkedBorderColor = accentColor,
                uncheckedThumbColor = if (isDark) Color(0xFFAAAAAA) else Color(0xFF888888),
                uncheckedTrackColor = if (isDark) Color(0xFF2C2F3A) else Color(0xFFE5E7EB),
                uncheckedBorderColor = if (isDark) Color(0xFF44475A) else Color(0xFFD1D5DB)
            )
        )
    }
}

@Composable
private fun GlassDivider(borderGlass: Color) {
    HorizontalDivider(
        thickness = 1.dp,
        color = borderGlass,
        modifier = Modifier.padding(start = 62.dp, end = 16.dp)
    )
}

// ─── Modal de edición de datos ────────────────────────────────────────────────

@Composable
private fun EditDataDialog(
    currentUsername: String,
    isUpdating: Boolean,
    updateError: String?,
    onDismiss: () -> Unit,
    onSave: (username: String, password: String) -> Unit,
    glassBase: Color,
    borderGlass: Color,
    accentColor: Color,
    accentColor2: Color,
    onSurface: Color,
    textSecondary: Color
) {
    var username by remember { mutableStateOf(currentUsername) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordsMatch by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Cerrar el modal cuando la actualización sea exitosa
    LaunchedEffect(isUpdating, updateError) {
        if (!isUpdating && updateError == null && username != currentUsername) {
            // Esperar un momento antes de cerrar para que el usuario vea que se guardó
            kotlinx.coroutines.delay(300)
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = glassBase,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                stringResource(Res.string.edit_data),
                style = AppTypo.heading().copy(
                    brush = Brush.horizontalGradient(colors = listOf(accentColor, accentColor2))
                ),
                fontSize = 22.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Username
                GlassSectionLabel(stringResource(Res.string.name), accentColor)
                GlassTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    placeholder = stringResource(Res.string.name),
                    accentColor = if (errorMessage.isNotEmpty()) Color(0xFFFF6B6B) else accentColor,
                    borderGlass = if (errorMessage.isNotEmpty()) Color(0xFFFF6B6B).copy(alpha = 0.4f) else borderGlass,
                    glassBase = glassBase,
                    onSurface = onSurface
                )

                Spacer(Modifier.height(4.dp))

                // Nueva contraseña
                GlassSectionLabel(stringResource(Res.string.new_password_optional), accentColor)
                GlassTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        passwordsMatch = confirmPassword.isEmpty() || it == confirmPassword
                        errorMessage = ""
                    },
                    placeholder = stringResource(Res.string.new_password),
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    accentColor = if (!passwordsMatch && newPassword.isNotEmpty()) Color(0xFFFF6B6B) else accentColor,
                    borderGlass = if (!passwordsMatch && newPassword.isNotEmpty()) Color(0xFFFF6B6B).copy(alpha = 0.4f) else borderGlass,
                    glassBase = glassBase,
                    onSurface = onSurface
                )

                // Confirmar contraseña
                if (newPassword.isNotEmpty()) {
                    GlassTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            passwordsMatch = newPassword == it
                            errorMessage = ""
                        },
                        placeholder = stringResource(Res.string.confirm_password),
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible },
                        accentColor = if (!passwordsMatch && confirmPassword.isNotEmpty()) Color(0xFFFF6B6B) else accentColor,
                        borderGlass = if (!passwordsMatch && confirmPassword.isNotEmpty()) Color(0xFFFF6B6B).copy(alpha = 0.4f) else borderGlass,
                        glassBase = glassBase,
                        onSurface = onSurface
                    )

                    if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.passwordnomatch),
                            style = AppTypo.body(),
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        style = AppTypo.body(),
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp
                    )
                }

                // Mostrar error del ViewModel si existe
                if (updateError != null) {
                    Text(
                        text = updateError,
                        style = AppTypo.body(),
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = stringResource(Res.string.leave_password_empty),
                    style = AppTypo.body(),
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        },
        confirmButton = {
            val canSave = username.isNotBlank() &&
                         (newPassword.isEmpty() || (passwordsMatch && confirmPassword.isNotEmpty())) &&
                         !isUpdating

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = if (canSave && !isUpdating)
                            Brush.linearGradient(colors = listOf(accentColor, accentColor2))
                        else
                            Brush.linearGradient(colors = listOf(
                                onSurface.copy(alpha = 0.3f),
                                onSurface.copy(alpha = 0.2f)
                            ))
                    )
                    .clickable(enabled = canSave && !isUpdating) {
                        if (username.isBlank()) {
                            errorMessage = "El nombre de usuario no puede estar vacío"
                        } else if (newPassword.isNotEmpty() && !passwordsMatch) {
                            errorMessage = "Las contraseñas no coinciden"
                        } else {
                            onSave(username, newPassword)
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        stringResource(Res.string.save),
                        color = Color.White,
                        style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                        fontSize = 14.sp
                    )
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.10f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { onDismiss() }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(stringResource(Res.string.cancel), color = accentColor, style = AppTypo.body().copy(fontWeight = FontWeight.Medium), fontSize = 14.sp)
            }
        }
    )
}

// ─── GlassTextField para este archivo ─────────────────────────────────────────

@Composable
private fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    accentColor: Color,
    borderGlass: Color,
    glassBase: Color,
    onSurface: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = AppTypo.body(),
                fontSize = 14.sp,
                color = onSurface.copy(alpha = 0.4f)
            )
        },
        textStyle = AppTypo.body().copy(fontSize = 14.sp),
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) Res.drawable.ico_eyeoff else Res.drawable.ico_eye
                        ),
                        contentDescription = null,
                        tint = accentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = borderGlass,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledBorderColor = borderGlass.copy(alpha = 0.4f),
            disabledContainerColor = Color.Transparent,
            focusedTextColor = onSurface,
            unfocusedTextColor = onSurface,
            cursorColor = accentColor
        )
    )
}

@Preview(name = "ConfigurationScreen Preview")
@Composable
fun ConfigurationScreenPreview() {
    val fakeProfile = ProfilePrivate(
        username = "paz",
        email = "paz@correo.com",
        avatar_url = "default",
        teams = listOf(
            ProfileTeam(name = "Frontend", join_code = "FE001"),
            ProfileTeam(name = "Backend", join_code = "BE002")
        )
    )
    ConfigurationScreenContent(
        profile = fakeProfile,
        viewModel = remember { ProfileViewModel() },
        darkModeEnabled = false
    )
}