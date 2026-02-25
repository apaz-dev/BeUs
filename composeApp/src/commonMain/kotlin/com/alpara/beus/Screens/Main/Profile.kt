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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Models.ProfilePrivate
import com.alpara.beus.Models.View.ProfileViewModel
import com.alpara.beus.Models.View.ProfileState
import com.alpara.beus.Models.ProfileTeam
import com.alpara.beus.Screens.Add.TeamModal
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.BackgroundColor
import com.alpara.beus.Themes.cardColor
import com.alpara.beus.Themes.textPrimary
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_gear
import com.alpara.beus.resources.ico_plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview(name = "Profile Success")
@Composable
fun ProfileScreenSuccessPreview() {
    ProfileScreenContent(
        profileState = ProfileState.Success(
            ProfilePrivate(
                username = "Ana Torres",
                email = "ana@example.com",
                avatar_url = "default",
                teams = listOf(
                    ProfileTeam(name = "Frontend", join_code = "FE001", members_count = 5),
                    ProfileTeam(name = "Frontend2", join_code = "FE002", members_count = 1),
                    ProfileTeam(name = "Frontend3", join_code = "FE003", members_count = 12),
                    ProfileTeam(name = "Frontend4", join_code = "FE004", members_count = 0),
                    ProfileTeam(name = "Backend", join_code = "BE002", members_count = 3)
                )
            )
        ),
        onOpenConfiguration = {},
        onRetry = {},
        onAddteam = {},
        onClickTeam = {}
    )
}

//@Preview(name = "Profile Loading")
@Composable
fun ProfileScreenLoadingPreview() {
    ProfileScreenContent(
        profileState = ProfileState.Loading,
        onOpenConfiguration = { },
        onRetry = {},
        onAddteam = {},
        onClickTeam = {}
    )
}

//@Preview(name = "Profile Error")
@Composable
fun ProfileScreenErrorPreview() {
    ProfileScreenContent(
        profileState = ProfileState.Error("No se pudo cargar el perfil"),
        onOpenConfiguration = {},
        onRetry = {},
        onAddteam = {},
        onClickTeam = {}
    )
}

//@Preview(name = "Profile Sin Equipos")
@Composable
fun ProfileScreenNoTeamsPreview() {
    ProfileScreenContent(
        profileState = ProfileState.Success(
            ProfilePrivate(
                username = "Usuario Nuevo",
                email = "nuevo@example.com",
                avatar_url = "default",
                teams = emptyList()
            )
        ),
        onOpenConfiguration = {},
        onRetry = {},
        onAddteam = {},
        onClickTeam = {}
    )
}

@Composable
fun ProfileScreen(
    onOpenConfiguration: () -> Unit,
    viewModel: ProfileViewModel = remember { ProfileViewModel() }
) {
    val profileState by viewModel.profileState.collectAsState()
    var showTeamModal by remember { mutableStateOf(false) }
    val teamViewModel = remember { com.alpara.beus.Models.View.TeamViewModel() }
    var teamResultMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar el mensaje en el Snackbar cuando cambie
    LaunchedEffect(teamResultMessage) {
        teamResultMessage?.let {
            snackbarHostState.showSnackbar(it)
            teamResultMessage = null
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        ProfileScreenContent(
            profileState = profileState,
            onOpenConfiguration = onOpenConfiguration,
            onRetry = { viewModel.loadProfile() },
            onAddteam = { showTeamModal = true },
            onClickTeam = { /* Acción para abrir detalles del equipo */ }
        )
    }

    TeamModal(
        showDialog = showTeamModal,
        onDismiss = { showTeamModal = false },
        onCreateTeam = { teamName ->
            teamViewModel.createTeam(teamName) { result ->
                result.fold(
                    onSuccess = { teamResultMessage = "Equipo creado correctamente"; showTeamModal = false; viewModel.loadProfile() },
                    onFailure = { teamResultMessage = "Error al crear equipo: ${it.message}" }
                )
            }
        },
        onJoinTeam = { joinCode ->
            teamViewModel.joinTeam(joinCode) { result ->
                result.fold(
                    onSuccess = { teamResultMessage = "Te uniste al equipo correctamente"; showTeamModal = false; viewModel.loadProfile() },
                    onFailure = { teamResultMessage = "Error al unirse: ${it.message}" }
                )
            }
        }
    )
}

@Composable
fun ProfileScreenContent(
    profileState: ProfileState,
    onOpenConfiguration: () -> Unit,
    onRetry: () -> Unit,
    onAddteam: () -> Unit,
    onClickTeam: () -> Unit
) {
    val headerHeight = 150.dp
    val avatarSize = 92.dp
    val avatarRadius = avatarSize / 2
    val avatarLeft = 16.dp
    val avatarCenterX = avatarLeft + avatarRadius

    // Glassmorphism palette (igual que TeamCard)
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val accentColor   = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2  = if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase     = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass   = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor       = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Box(
            modifier = Modifier
                .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
                .background(bgColor)
        ) {
            when (val state = profileState) {
                // ── Loading ──────────────────────────────────────────────
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = accentColor
                    )
                }

                // ── Error ────────────────────────────────────────────────
                is ProfileState.Error -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, borderGlass, RoundedCornerShape(20.dp))
                            .background(glassBase.copy(alpha = 0.7f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⚠ ${state.message}",
                                color = Color(0xFFFF6B6B),
                                style = AppTypo.body(),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable { onRetry() }
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Reintentar",
                                    color = accentColor,
                                    style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // ── Success ──────────────────────────────────────────────
                is ProfileState.Success -> {
                    val profile = state.profile

                    // ── Header con gradiente glass ────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
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
                        // Líneas decorativas sutiles
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            borderGlass,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // ── Botón configuración ───────────────────────────────
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(glassBase.copy(alpha = 0.55f))
                            .border(1.dp, borderGlass, CircleShape)
                            .clickable { onOpenConfiguration() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ico_gear),
                            contentDescription = "Configuración",
                            tint = if (isDark) Color.White.copy(alpha = 0.85f)
                            else accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // ── Body con mordisco para el avatar ─────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = headerHeight)
                            .background(bgColor)
                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                            .drawWithContent {
                                drawContent()
                                drawCircle(
                                    color = Color.Transparent,
                                    radius = avatarRadius.toPx() + 10.dp.toPx(),
                                    center = Offset(avatarCenterX.toPx(), 0f),
                                    blendMode = BlendMode.Clear
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = avatarRadius + 12.dp,
                                    bottom = 16.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // ── Nombre ────────────────────────────────────
                            Text(
                                text = profile.username,
                                style = AppTypo.heading().copy(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(accentColor, accentColor2)
                                    )
                                ),
                                fontSize = 30.sp
                            )
                            // ── Email ─────────────────────────────────────
                            Text(
                                text = profile.email,
                                style = AppTypo.body(),
                                color = textSecondary,
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.height(20.dp))

                            // ── Sección equipos ───────────────────────────
                            // Cabecera de sección glass
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                accentColor.copy(alpha = 0.12f),
                                                accentColor2.copy(alpha = 0.06f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(borderGlass, Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Group,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Grupos",
                                    color = accentColor,
                                    style = AppTypo.body().copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    ),
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.weight(1f))
                                if (profile.teams.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(accentColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${profile.teams.size}",
                                            color = accentColor,
                                            style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Divisor
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(borderGlass)
                            )

                            Spacer(Modifier.height(6.dp))

                            // Lista de equipos + botón añadir
                            if (profile.teams.isEmpty()) {
                                AddTeamCard(add = true, onAddTeam = onAddteam, onClickTeam = onClickTeam)
                            } else {
                                AddTeamCard(
                                    add = false,
                                    profile = profile,
                                    onClickTeam = onClickTeam,
                                    onAddTeam = onAddteam
                                )
                                Spacer(Modifier.height(6.dp))
                                AddTeamCard(add = true, onAddTeam = onAddteam, onClickTeam = onClickTeam)
                            }
                        }
                    }

                    // ── Avatar ────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .offset(x = avatarLeft, y = headerHeight - avatarRadius)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(accentColor, accentColor2)
                                )
                            )
                            .border(3.dp, bgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = profile.username.take(2).uppercase()
                        Text(
                            text = initials,
                            style = AppTypo.heading(),
                            fontSize = 26.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTeamCard(add: Boolean,
                profile: ProfilePrivate? = null,
                onClickTeam: () -> Unit,
                onAddTeam: () -> Unit
) {
    if (!add && profile != null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp), // Limita la altura máxima para permitir scroll
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(
                items = profile.teams,
                key = { team -> team.join_code }
            ) { team ->
                TeamCard(team = team, cardColor = cardColor, onClick = onClickTeam)
            }
        }
    } else {
        AddCard(cardColor, onClick = onAddTeam)
    }
}
@Composable
private fun TeamCard(
    team: ProfileTeam,
    cardColor: Color,
    onClick: () -> Unit
) {
    // Detectar modo oscuro: si el fondo es oscuro, el canal rojo es bajo
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val glassBase = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderGlass, Color.Transparent, borderGlass)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        glassBase.copy(alpha = 0.75f),
                        glassBase.copy(alpha = 0.55f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        // Acento decorativo en la parte izquierda
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor, accentColor.copy(alpha = 0.3f))
                    )
                )
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 14.dp)
        ) {
            // Nombre e icono de grupo
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.name.uppercase(),
                    color = textPrimary,
                    style = AppTypo.body().copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Spacer(Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = "Participantes",
                        tint = textSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (team.members_count > 0)
                            "${team.members_count} participante${if (team.members_count != 1) "s" else ""}"
                        else
                            "Sin participantes",
                        color = textSecondary,
                        style = AppTypo.body(),
                        fontSize = 11.sp
                    )
                }
            }

            // Código de unión — chip glass
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = team.join_code,
                    style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 13.sp,
                    color = accentColor,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun AddCard(cardColor: Color, onClick: () -> Unit) {
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val glassBase = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x33FFFFFF) else Color(0x44FFFFFF)
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderGlass, Color.Transparent, borderGlass)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        glassBase.copy(alpha = 0.55f),
                        glassBase.copy(alpha = 0.35f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ico_plus),
                    contentDescription = "Añadir equipo",
                    tint = accentColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Unirse o crear equipo",
                    style = AppTypo.body().copy(fontWeight = FontWeight.Medium),
                    fontSize = 13.sp,
                    color = accentColor.copy(alpha = 0.9f),
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}