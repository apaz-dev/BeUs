package com.alpara.beus.Screens.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alpara.beus.Models.View.AuthViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.sp
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_eye
import com.alpara.beus.resources.ico_eyeoff
import com.alpara.beus.resources.ico_home
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.resources.email
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.ico_home
import com.alpara.beus.resources.passwordnomatch
import com.alpara.beus.resources.privacy_policy
import com.alpara.beus.resources.repeat_password
import com.alpara.beus.resources.signup
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SignUpScreen(
    onSignupSuccess: () -> Unit = {},
    viewModel: AuthViewModel,
    onLoginBack: () -> Unit = {},
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisible2 by remember { mutableStateOf(false) }
    var passwordsMatch by remember { mutableStateOf(true) }
    var chekbox1 by remember { mutableStateOf(false) }
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) onSignupSuccess()
    }

    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-70).dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor2.copy(alpha = if (isDark) 0.22f else 0.13f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 50.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (isDark) 0.18f else 0.10f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )


        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 12.dp,
                    start = 16.dp
                )
                .size(38.dp)
                .clip(CircleShape)
                .background(glassBase.copy(alpha = 0.5f))
                .border(1.dp, borderGlass, CircleShape)
                .clickable { onLoginBack() }
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(Res.drawable.ico_arrowleft),
                contentDescription = "Volver",
                tint = if (isDark) Color.White.copy(alpha = 0.85f) else accentColor,
                modifier = androidx.compose.ui.Modifier.size(18.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                    start = 28.dp,
                    end = 28.dp,
                    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 16.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(64.dp))

            Image(
                painter = painterResource(Res.drawable.ico_home),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "BeUs",
                style = AppTypo.heading().copy(
                    brush = Brush.horizontalGradient(colors = listOf(accentColor, accentColor2))
                ),
                fontSize = 34.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Crea tu cuenta",
                style = AppTypo.body(),
                fontSize = 14.sp,
                color = textSecondary
            )

            Spacer(Modifier.height(28.dp))

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

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

                GlassTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = "Nombre",
                    enabled = !isLoading,
                    accentColor = accentColor,
                    borderGlass = borderGlass,
                    glassBase = glassBase,
                    onSurface = MaterialTheme.colorScheme.onSurface
                )

                GlassTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    enabled = !isLoading,
                    accentColor = accentColor,
                    borderGlass = borderGlass,
                    glassBase = glassBase,
                    onSurface = MaterialTheme.colorScheme.onSurface
                )

                        GlassTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordsMatch = it == repeatPassword || repeatPassword.isEmpty()
                            },
                            placeholder = "Contraseña",
                            enabled = !isLoading,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible },
                            accentColor = accentColor,
                            borderGlass = borderGlass,
                            glassBase = glassBase,
                            onSurface = MaterialTheme.colorScheme.onSurface
                        )

                        GlassTextField(
                            value = repeatPassword,
                            onValueChange = {
                                repeatPassword = it
                                passwordsMatch = password == it
                            },
                            placeholder = "Repetir contraseña",
                            enabled = !isLoading,
                            isPassword = true,
                            passwordVisible = passwordVisible2,
                            onTogglePassword = { passwordVisible2 = !passwordVisible2 },
                            accentColor = if (!passwordsMatch && repeatPassword.isNotEmpty())
                                Color(0xFFFF6B6B) else accentColor,
                            borderGlass = if (!passwordsMatch && repeatPassword.isNotEmpty())
                                Color(0xFFFF6B6B).copy(alpha = 0.4f) else borderGlass,
                            glassBase = glassBase,
                            onSurface = MaterialTheme.colorScheme.onSurface
                        )

                        if (!passwordsMatch && repeatPassword.isNotEmpty()) {
                            Text(
                                text = stringResource(Res.string.passwordnomatch),
                                style = AppTypo.body(),
                                color = Color(0xFFFF6B6B),
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = chekbox1,
                                onCheckedChange = { chekbox1 = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = accentColor,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    checkmarkColor = Color.White
                                )
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(Res.string.privacy_policy),
                                style = AppTypo.body(),
                                fontSize = 13.sp,
                                color = textSecondary,
                                modifier = Modifier.clickable { chekbox1 = !chekbox1 }
                            )
                        }
                        val canSignup = !isLoading && email.isNotBlank() && password.isNotBlank()
                                && nombre.isNotBlank() && repeatPassword.isNotBlank()
                                && passwordsMatch && chekbox1

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    brush = if (canSignup)
                                        Brush.linearGradient(colors = listOf(accentColor, accentColor2))
                                    else
                                        Brush.linearGradient(colors = listOf(
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        ))
                                )
                                .clickable(enabled = canSignup) {
                                    viewModel.register(nombre, email, password)
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
                                    text = stringResource(Res.string.signup),
                                    style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }

                Spacer(Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable(enabled = !isLoading) { onLoginBack() }
                        .padding(8.dp)
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        style = AppTypo.body(),
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Inicia sesión",
                        style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                        color = if (isLoading) textSecondary else accentColor,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

            }
        }
    }
}





