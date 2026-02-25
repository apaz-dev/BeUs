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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_eye
import com.alpara.beus.resources.ico_eyeoff
import com.alpara.beus.resources.ico_home
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.resources.email
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.name
import com.alpara.beus.resources.password
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
        if (isAuthenticated) {
            onSignupSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Transparent
            ) {

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    IconButton(
                        onClick = onLoginBack,
                        modifier = Modifier.align(Alignment.TopStart).padding(top = 20.dp)
                            .padding(horizontal = 20.dp)
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.ico_arrowleft),
                            contentDescription = "Ícono personalizado",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }



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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "BeUs",
                            style = AppTypo.heading(),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            placeholder = { Text(text = stringResource(Res.string.name), style = AppTypo.body()) },
                            textStyle = AppTypo.body(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text(text = stringResource(Res.string.email), style = AppTypo.body()) },
                            textStyle = AppTypo.body(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordsMatch = password == repeatPassword
                            },
                            placeholder = { Text(text = stringResource(Res.string.password), style = AppTypo.body()) },
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
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
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
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = repeatPassword,
                            onValueChange = {
                                repeatPassword = it
                                passwordsMatch = password == repeatPassword
                            },
                            placeholder = { Text(text = stringResource(Res.string.repeat_password), style = AppTypo.body()) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible2)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisible2 = !passwordVisible2
                                }) {
                                    Icon(
                                        painter = painterResource(
                                            if (passwordVisible2)
                                                Res.drawable.ico_eyeoff
                                            else
                                                Res.drawable.ico_eye
                                        ),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
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
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = chekbox1,
                                onCheckedChange = { chekbox1 = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.onBackground,
                                    uncheckedColor = MaterialTheme.colorScheme.onBackground,
                                    checkmarkColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = stringResource(Res.string.privacy_policy),
                                style = AppTypo.body(),
                                modifier = Modifier.clickable {
                                    chekbox1 = !chekbox1
                                }
                            )
                        }



                        Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (!isLoading && email.isNotBlank() && password.isNotBlank() && nombre.isNotBlank() && repeatPassword.isNotBlank() && passwordsMatch && chekbox1) {
                                viewModel.register(nombre, email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background,
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.signup),
                                style = AppTypo.body()
                                    .copy(color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!passwordsMatch) {
                            Text(
                                text = stringResource(Res.string.passwordnomatch),
                                style = AppTypo.body().copy(color = Color.Red)
                            )
                        }

                        authError?.let { error ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = error,
                                    style = AppTypo.body().copy(
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }


                    }
                }
            }
        }
    }
}


