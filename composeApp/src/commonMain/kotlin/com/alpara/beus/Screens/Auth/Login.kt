package com.alpara.beus.Screens.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alpara.beus.Models.View.AuthViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.email
import com.alpara.beus.resources.password
import com.alpara.beus.resources.ico_eye
import com.alpara.beus.resources.ico_eyeoff
import com.alpara.beus.resources.ico_home
import com.alpara.beus.resources.login_title
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
        if (isAuthenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                start = 28.dp,
                end = 28.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.ico_home),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "BeUs",
            style = AppTypo.heading(),
        )

            Image(
                painter = painterResource(Res.drawable.ico_home),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

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
                            colors = listOf(
                                glassBase.copy(alpha = 0.78f),
                                glassBase.copy(alpha = 0.55f)
                            )
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
                            .border(
                                1.dp,
                                Color(0xFFFF6B6B).copy(alpha = 0.35f),
                                RoundedCornerShape(10.dp)
                            )
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
                    placeholder = stringResource(Res.string.email),
                    enabled = !isLoading,
                    accentColor = accentColor,
                    borderGlass = borderGlass,
                    glassBase = glassBase,
                    onSurface = MaterialTheme.colorScheme.onSurface
                )

                GlassTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    placeholder = stringResource(Res.string.password),
                    enabled = !isLoading,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    accentColor = accentColor,
                    borderGlass = borderGlass,
                    glassBase = glassBase,
                    onSurface = MaterialTheme.colorScheme.onSurface
                )

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
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                )
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
                            stringResource(Res.string.login_title),
                            style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
            }

            Spacer(Modifier.height(16.dp))

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
        )

        Spacer(Modifier.height(14.dp))

        // PASSWORD
        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            placeholder = { Text(text = "Contraseña", style = AppTypo.body()) },
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                if (!isLoading) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) Res.drawable.ico_eyeoff else Res.drawable.ico_eye
                            ),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (emailText.isNotBlank() && passwordText.isNotBlank() && !isLoading) {
                    viewModel.login(emailText, passwordText)
                }
            },
            enabled = emailText.isNotBlank() && passwordText.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.background
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.background,
                )
            } else {
                Text(
                    "Entrar",
                    style = AppTypo.body().copy(
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = " O ",
                style = AppTypo.body().copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable(enabled = !isLoading) { onSignup() }
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.no_account),
                style = AppTypo.body().copy(
                    color = if (isLoading) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}