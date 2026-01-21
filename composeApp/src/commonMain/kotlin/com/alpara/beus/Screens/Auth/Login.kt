package com.alpara.beus.Screens.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alpara.beus.Models.AuthViewModel
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_home
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.alpara.beus.resources.ico_eye
import com.alpara.beus.resources.ico_eyeoff
import com.alpara.beus.Themes.*


@Preview
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel? = null,
    onLoginSuccess: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onSignup: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val isLoading by (authViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) })
    val errorMessage by (authViewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf<String?>(null) })
    
    val emailpasswornoblind = email.isNotBlank() && password.isNotBlank()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                )
        )
        {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
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

                    Spacer(Modifier.height(28.dp))

                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            authViewModel?.clearError()
                        },
                        placeholder = { Text("Correo electrónico", style = AppTypo.body()) },
                        textStyle = AppTypo.body(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(14.dp))


                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            authViewModel?.clearError()
                        },
                        placeholder = { Text("Contraseña", style = AppTypo.body()) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisible = !passwordVisible
                            }) {
                                Icon(
                                    painter = painterResource(
                                        if (passwordVisible)
                                            Res.drawable.ico_eyeoff
                                        else
                                            Res.drawable.ico_eye
                                    ),
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(6.dp))

                    // Mostrar mensaje de error si existe
                    errorMessage?.let {
                        Text(
                            text = it,
                            style = AppTypo.body().copy(color = Color.Red),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))



                    Button(
                        onClick = {
                            if (emailpasswornoblind && !isLoading) {
                                authViewModel?.login(email.trim(), password, onLoginSuccess) ?: onLoginSuccess()
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        enabled = emailpasswornoblind && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                "Entrar",
                                style = AppTypo.body()
                                    .copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Divider(modifier = Modifier.weight(1f))
                        Text(
                            text = " O ",
                            style = AppTypo.body().copy(color = Color.Gray)
                        )
                        Divider(modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(22.dp))

                    // GOOGLE COMO TEXTO
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable { onGoogleClick() }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Continuar con Google",
                            style = AppTypo.body()
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable { onSignup() }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "¿No tienes cuenta?",
                            style = AppTypo.body()
                        )
                    }

                }
            }
        }
    }
}