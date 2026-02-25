package com.alpara.beus.Screens.Main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import com.alpara.beus.Models.ProfilePrivate
import com.alpara.beus.Models.ProfileTeam
import com.alpara.beus.Models.View.ProfileState
import com.alpara.beus.Models.View.ProfileViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.ico_profile
import com.alpara.beus.resources.ico_rightarrow
import com.alpara.beus.resources.ico_pencil
import com.alpara.beus.resources.ico_moon
import com.alpara.beus.resources.ico_notes
import com.alpara.beus.resources.ico_bin
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun ConfigurationScreen(
    onEditClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onHomeBack: () -> Unit = {},

    // viene desde arriba (App)
    darkModeEnabled: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},

    viewModel: ProfileViewModel = remember { ProfileViewModel() }
) {
    val profileState by viewModel.profileState.collectAsState()

    when (profileState) {
        is ProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Error -> {
            val message = (profileState as ProfileState.Error).message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { viewModel.loadProfile() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        is ProfileState.Success -> {
            val profile = (profileState as ProfileState.Success).profile
            ConfigurationScreenContent(
                profile = profile,
                onEditClick = onEditClick,
                onChangePasswordClick = onChangePasswordClick,
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
    onEditClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onHomeBack: () -> Unit = {},
    darkModeEnabled: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Confirmación", color = onSurface) },
            text = { Text("Estas apunto de eliminar tu cuenta estas seguro de esto?", color = onSurface) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    }
                ) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(12.dp)
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ico_arrowleft),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onHomeBack),
                tint = onSurface
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Perfil",
                style = AppTypo.heading(),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        }

        Spacer(Modifier.height(25.dp))

        // Foto perfil
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ico_profile),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = profile.username,
            style = AppTypo.heading(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = onSurface
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = profile.email,
            style = AppTypo.body(),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(25.dp))

        // Editar datos
        ProfileSingleBox(
            borderColor = outline,
            backgroundColor = surface
        ) {
            ProfileRowLikeReference(
                text = "Editar datos",
                leftIconRes = Res.drawable.ico_pencil,
                onClick = onEditClick,
                trailingText = null,
                showChevron = true
            )
        }

        Spacer(Modifier.height(12.dp))

        // Modo oscuro
        ProfileSingleBox(
            borderColor = outline,
            backgroundColor = surface
        ) {

            ProfileRowSwitchLikeReference(
                text = "Modo oscuro",
                leftIconRes = Res.drawable.ico_moon,
                checked = darkModeEnabled,
                onCheckedChange = onDarkModeChange
            )
        }

        Spacer(Modifier.height(14.dp))

        // Cerrar sesión / Borrar cuenta
        ProfileSectionLikeReference(
            borderColor = outline,
            backgroundColor = surface
        ) {
            ProfileRowLikeReference(
                text = "Cerrar sesion",
                leftIconRes = Res.drawable.ico_notes,
                onClick = onLogout,
                showChevron = false
            )
            SectionDividerLikeReference()

            ProfileRowLikeReference(
                text = "Borrar cuenta",
                leftIconRes = Res.drawable.ico_bin,
                onClick = { showDeleteAccountDialog = true },
                showChevron = false
            )
        }
    }
}

@Composable
fun ProfileSingleBox(
    borderColor: Color,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .border(BorderStroke(1.6.dp, borderColor), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
    ) { content() }
}

@Composable
fun ProfileSectionLikeReference(
    borderColor: Color,
    backgroundColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .border(BorderStroke(1.6.dp, borderColor), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
fun ProfileRowLikeReference(
    text: String,
    leftIconRes: DrawableResource,
    onClick: () -> Unit,
    trailingText: String? = null,
    showChevron: Boolean = true
) {
    val leftSlotWidth = 46.dp
    val rowMinHeight = 54.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = rowMinHeight)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(leftSlotWidth),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(leftIconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = if (showChevron) 10.dp else 0.dp)
            )
        }

        if (showChevron) {
            Icon(
                painter = painterResource(Res.drawable.ico_rightarrow),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ProfileRowSwitchLikeReference(
    text: String,
    leftIconRes: DrawableResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val leftSlotWidth = 46.dp
    val rowMinHeight = 54.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = rowMinHeight)
            .padding(start = 16.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(leftSlotWidth),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(leftIconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Switch blanco/negro como lo tenías
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(1.05f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Black,
                checkedBorderColor = Color.Black,
                uncheckedThumbColor = Color.Black,
                uncheckedTrackColor = Color.White,
                uncheckedBorderColor = Color.Black
            )
        )
    }
}

@Composable
fun SectionDividerLikeReference() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(start = 62.dp)
    )
}

@Preview(name = "ConfigurationScreen Preview")
@Composable
fun ConfigurationScreenPreview() {
    val fakeProfile = ProfilePrivate(
        username = "alvaro_dev",
        email = "alvaro@correo.com",
        avatar_url = "default",
        teams = listOf(
            ProfileTeam(name = "Frontend", join_code = "FE001"),
            ProfileTeam(name = "Backend", join_code = "BE002")
        )
    )
    ConfigurationScreenContent(
        profile = fakeProfile,
        darkModeEnabled = false
    )
}

