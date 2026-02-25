package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.alpara.beus.Models.TeamDetail
import com.alpara.beus.Models.TeamMember
import com.alpara.beus.Models.View.TeamDetailState
import com.alpara.beus.Models.View.TeamDetailViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textPrimary
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_arrowleft
import org.jetbrains.compose.resources.painterResource

@Composable
fun TeamDetailScreen(
    teamId: String,
    onBack: () -> Unit,
    viewModel: TeamDetailViewModel = remember { TeamDetailViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val actionResult by viewModel.actionResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar detalles al entrar
    LaunchedEffect(teamId) { viewModel.loadTeamDetail(teamId) }

    // Mostrar resultados de acciones
    LaunchedEffect(actionResult) {
        actionResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionResult()
        }
    }

    // Paleta glass (igual que Profile)
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val accentColor  = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2 = if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase    = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass  = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor      = MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(innerPadding)
                .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
        ) {
            // ── Top bar ────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(glassBase.copy(alpha = 0.55f))
                        .border(1.dp, borderGlass, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ico_arrowleft),
                        contentDescription = "Volver",
                        tint = if (isDark) Color.White.copy(alpha = 0.85f) else accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = when (val s = state) {
                        is TeamDetailState.Success -> s.detail.name.uppercase()
                        else -> "Equipo"
                    },
                    style = AppTypo.heading().copy(
                        brush = Brush.horizontalGradient(listOf(accentColor, accentColor2))
                    ),
                    fontSize = 22.sp
                )
            }

            // ── Contenido ──────────────────────────────────────────────
            when (val s = state) {
                is TeamDetailState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                }

                is TeamDetailState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⚠ ${s.message}",
                                color = Color(0xFFFF6B6B),
                                style = AppTypo.body(),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.loadTeamDetail(teamId) }
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "Reintentar",
                                    color = accentColor,
                                    style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                is TeamDetailState.Success -> {
                    TeamDetailContent(
                        detail = s.detail,
                        isDark = isDark,
                        accentColor = accentColor,
                        accentColor2 = accentColor2,
                        glassBase = glassBase,
                        borderGlass = borderGlass,
                        onKick = { member -> viewModel.kickMember(member.userId) {} },
                        onLeave = { viewModel.leaveTeam { onBack() } },
                        onDissolve = { viewModel.dissolveTeam { onBack() } }
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamDetailContent(
    detail: TeamDetail,
    isDark: Boolean,
    accentColor: Color,
    accentColor2: Color,
    glassBase: Color,
    borderGlass: Color,
    onKick: (TeamMember) -> Unit,
    onLeave: () -> Unit,
    onDissolve: () -> Unit
) {
    val isOwner = detail.currentUserRole == "OWNER"

    // Diálogos de confirmación
    var memberToKick by remember { mutableStateOf<TeamMember?>(null) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showDissolveDialog by remember { mutableStateOf(false) }

    // Cabecera de sección (código de equipo)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(accentColor.copy(alpha = 0.10f), accentColor2.copy(alpha = 0.05f))
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(borderGlass, Color.Transparent)),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Código",
            style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
            color = textSecondary,
            fontSize = 12.sp
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = detail.joinCode,
                style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                color = accentColor,
                fontSize = 14.sp,
                letterSpacing = 1.5.sp
            )
        }
    }

    Spacer(Modifier.height(14.dp))

    // Etiqueta "Miembros"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "MIEMBROS",
            style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp),
            color = accentColor,
            fontSize = 11.sp
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${detail.members.size}",
                style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                color = accentColor,
                fontSize = 11.sp
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    // Lista de miembros
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(
            items = detail.members,
            key = { it.userId }
        ) { member ->
            MemberRow(
                member = member,
                isCurrentUser = member.userId == detail.currentUserId,
                isOwner = isOwner,
                isDark = isDark,
                accentColor = accentColor,
                glassBase = glassBase,
                borderGlass = borderGlass,
                onKick = { memberToKick = member },
                onLeave = { showLeaveDialog = true },
                onDissolve = { showDissolveDialog = true }
            )
        }
    }

    // ── Diálogo: confirmar expulsión ──────────────────────────────────
    memberToKick?.let { target ->
        ConfirmDialog(
            title = "Expulsar miembro",
            message = "¿Seguro que quieres expulsar a ${target.username} del equipo?",
            confirmText = "Expulsar",
            confirmColor = Color(0xFFFF6B6B),
            isDark = isDark,
            accentColor = accentColor,
            glassBase = glassBase,
            borderGlass = borderGlass,
            onConfirm = { onKick(target); memberToKick = null },
            onDismiss = { memberToKick = null }
        )
    }

    // ── Diálogo: confirmar salida ─────────────────────────────────────
    if (showLeaveDialog) {
        ConfirmDialog(
            title = "Salir del equipo",
            message = "¿Seguro que quieres salir de ${detail.name}?",
            confirmText = "Salir",
            confirmColor = Color(0xFFFF6B6B),
            isDark = isDark,
            accentColor = accentColor,
            glassBase = glassBase,
            borderGlass = borderGlass,
            onConfirm = { showLeaveDialog = false; onLeave() },
            onDismiss = { showLeaveDialog = false }
        )
    }

    // ── Diálogo: confirmar disolución ─────────────────────────────────
    if (showDissolveDialog) {
        ConfirmDialog(
            title = "Disolver equipo",
            message = "¿Seguro que quieres disolver ${detail.name}? Esta acción es irreversible.",
            confirmText = "Disolver",
            confirmColor = Color(0xFFFF6B6B),
            isDark = isDark,
            accentColor = accentColor,
            glassBase = glassBase,
            borderGlass = borderGlass,
            onConfirm = { showDissolveDialog = false; onDissolve() },
            onDismiss = { showDissolveDialog = false }
        )
    }
}

@Composable
private fun MemberRow(
    member: TeamMember,
    isCurrentUser: Boolean,
    isOwner: Boolean,          // ¿El usuario actual es OWNER?
    isDark: Boolean,
    accentColor: Color,
    glassBase: Color,
    borderGlass: Color,
    onKick: () -> Unit,
    onLeave: () -> Unit,
    onDissolve: () -> Unit
) {
    val roleLabel = when (member.role) {
        "OWNER" -> "Dueño"
        else -> "Miembro"
    }
    val roleColor = if (member.role == "OWNER") accentColor else textSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                Brush.linearGradient(listOf(borderGlass, Color.Transparent, borderGlass)),
                RoundedCornerShape(16.dp)
            )
            .background(
                Brush.linearGradient(
                    listOf(glassBase.copy(alpha = 0.75f), glassBase.copy(alpha = 0.55f)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        // Barra lateral de color según rol
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(roleColor, roleColor.copy(alpha = 0.3f))
                    )
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
        ) {
            // Avatar iniciales
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (member.role == "OWNER")
                                listOf(accentColor, accentColor.copy(alpha = 0.6f))
                            else
                                listOf(
                                    glassBase.copy(alpha = 0.9f),
                                    glassBase.copy(alpha = 0.7f)
                                )
                        )
                    )
                    .border(
                        1.5.dp,
                        if (member.role == "OWNER") accentColor.copy(alpha = 0.6f)
                        else borderGlass,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.username.take(2).uppercase(),
                    style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                    fontSize = 14.sp,
                    color = if (member.role == "OWNER") Color.White
                    else (if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF4F5BFF))
                )
            }

            Spacer(Modifier.width(12.dp))

            // Nombre + rol
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.username,
                    style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                    color = textPrimary,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = roleLabel,
                    style = AppTypo.body(),
                    color = roleColor,
                    fontSize = 11.sp
                )
            }

            // ── Iconos de acción ──────────────────────────────────────
            if (isCurrentUser) {
                // Es el propio usuario
                if (isOwner) {
                    // Dueño ve botón "Disolver"
                    ActionIconButton(
                        icon = { Icon(Icons.Filled.Delete, contentDescription = "Disolver equipo", tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp)) },
                        tooltip = "Disolver",
                        onClick = onDissolve
                    )
                } else {
                    // Miembro ve botón "Salir"
                    ActionIconButton(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir del equipo", tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp)) },
                        tooltip = "Salir",
                        onClick = onLeave
                    )
                }
            } else if (isOwner && member.role != "OWNER") {
                // Dueño viendo a otro miembro → puede expulsar
                ActionIconButton(
                    icon = { Icon(Icons.Filled.PersonRemove, contentDescription = "Expulsar", tint = Color(0xFFFF9966), modifier = Modifier.size(20.dp)) },
                    tooltip = "Expulsar",
                    onClick = onKick
                )
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: @Composable () -> Unit,
    tooltip: String,
    onClick: () -> Unit
) {
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val glassBase = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(glassBase.copy(alpha = 0.6f))
            .border(1.dp, borderGlass, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    isDark: Boolean,
    accentColor: Color,
    glassBase: Color,
    borderGlass: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, borderGlass, RoundedCornerShape(24.dp))
                .background(glassBase.copy(alpha = 0.95f))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = AppTypo.heading(),
                    fontSize = 18.sp,
                    color = if (isDark) Color.White else Color(0xFF1A1A2E)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    style = AppTypo.body(),
                    fontSize = 14.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF4A4A6A)
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancelar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.10f))
                            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Cancelar",
                            style = AppTypo.body().copy(fontWeight = FontWeight.Medium),
                            color = accentColor,
                            fontSize = 14.sp
                        )
                    }
                    // Confirmar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(confirmColor.copy(alpha = 0.15f))
                            .border(1.dp, confirmColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onConfirm)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            confirmText,
                            style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                            color = confirmColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
