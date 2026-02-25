package com.alpara.beus.Screens.Auth

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Models.View.AuthViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_eye
import com.alpara.beus.resources.ico_eyeoff
import com.alpara.beus.resources.no_account
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit = {},
    onSignup: () -> Unit = {}
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) onLoginSuccess()
    }

    // ── Glassmorphism palette ──────────────────────────────────────────────
    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor     = MaterialTheme.colorScheme.background
    val onSurface   = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // ── Fondo decorativo: orbes de gradiente ──────────────────────────
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (isDark) 0.25f else 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor2.copy(alpha = if (isDark) 0.2f else 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // ── Contenido centrado ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                    start = 28.dp,
                    end = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / icono con gradiente
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(accentColor, accentColor2),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .border(1.dp, borderGlass, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "B",
                    style = AppTypo.heading(),
                    fontSize = 38.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            // Título con gradiente
            Text(
                text = "BeUs",
                style = AppTypo.heading().copy(
                    brush = Brush.horizontalGradient(colors = listOf(accentColor, accentColor2))
                ),
                fontSize = 38.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Bienvenido de nuevo",
                style = AppTypo.body(),
                fontSize = 14.sp,
                color = textSecondary
            )

            Spacer(Modifier.height(32.dp))

            // ── Card glass contenedor de campos ────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(borderGlass, Color.Transparent, borderGlass)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(glassBase.copy(alpha = 0.78f), glassBase.copy(alpha = 0.55f))
                        )
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Error
                authError?.let { error ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFF6B6B).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = error,
                            style = AppTypo.body().copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp
                        )
                    }
                }

                // Email
                GlassTextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    placeholder = "Email",
                    enabled = !isLoading,
                    accentColor = accentColor,
                    borderGlass = borderGlass,
                    glassBase = glassBase,
                    onSurface = onSurface
                )

                // Contraseña
                GlassTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    placeholder = "Contraseña",
                    enabled = !isLoading,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    accentColor = accentColor,
                    borderGlass = borderGlass,
                    glassBase = glassBase,
                    onSurface = onSurface
                )

                // Botón entrar
                val canLogin = emailText.isNotBlank() && passwordText.isNotBlank() && !isLoading
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = if (canLogin)
                                Brush.linearGradient(colors = listOf(accentColor, accentColor2))
                            else
                                Brush.linearGradient(colors = listOf(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                ))
                        )
                        .clickable(enabled = canLogin) {
                            viewModel.login(emailText, passwordText)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Entrar",
                            style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Divisor
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = borderGlass
                )
                Text(
                    text = "  O  ",
                    style = AppTypo.body(),
                    color = textSecondary,
                    fontSize = 13.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = borderGlass
                )
            }

            Spacer(Modifier.height(16.dp))

            // Link registro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(enabled = !isLoading) { onSignup() }
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.no_account),
                    style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                    color = if (isLoading) textSecondary else accentColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Campo de texto glass reutilizable
// ─────────────────────────────────────────────────────────────
@Composable
fun GlassTextField(
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