package com.alpara.beus.Screens.Main


import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
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
                    ProfileTeam(name = "Frontend", join_code = "FE001"),
                    ProfileTeam(name = "Frontend2", join_code = "FE002"),
                    ProfileTeam(name = "Frontend3", join_code = "FE003"),
                    ProfileTeam(name = "Frontend4", join_code = "FE004"),
                    ProfileTeam(name = "Backend", join_code = "BE002")

                )
            )
        ),
        onOpenConfiguration = {},
        onRetry = {},
        onAddteam = {},
        onClickTeam = {}
    )
}

@Preview(name = "Profile Loading")
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

@Preview(name = "Profile Error")
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

@Preview(name = "Profile Sin Equipos")
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

    // Medidas
    val headerHeight = 150.dp
    val avatarSize = 92.dp
    val avatarRadius = avatarSize / 2
    val avatarLeft = 16.dp
    val avatarCenterX = avatarLeft + avatarRadius

    // Colores del nuevo tema
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val accentColor   = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2  = if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase     = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass   = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor       = MaterialTheme.colorScheme.background


    val headerColor = MaterialTheme.colorScheme.surfaceVariant
    val bodyColor   = MaterialTheme.colorScheme.background
    val outlineColor = MaterialTheme.colorScheme.outline


    val avatarBg = Color(0xFF3A3D44)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                )
                .fillMaxSize()
                .background(bgColor)
        ) {
            when (val state = profileState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = accentColor
                    )
                }

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

                is ProfileState.Success -> {
                    val profile = state.profile

                    // Header gris
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .background(headerColor)
                    )

                    // Icono configuración MÁS GRANDE y NEGRO
                    IconButton(
                        onClick = { onOpenConfiguration() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ico_gear),
                            contentDescription = "Configuración",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Body con mordisco
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = headerHeight)
                            .background(MaterialTheme.colorScheme.background)
                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                            .drawWithContent {
                                drawContent()

                                val cutoutRadius = avatarRadius.toPx() + 10.dp.toPx()
                                val cx = avatarCenterX.toPx()
                                val cy = 0f

                                drawCircle(
                                    color = Color.Transparent,
                                    radius = cutoutRadius,
                                    center = Offset(cx, cy),
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
                                    top = avatarRadius + 10.dp,
                                    bottom = 16.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = profile.username,
                                style = AppTypo.heading(),
                                fontSize = 32.sp,
                                color = textPrimary
                            )
                            Text(
                                text = profile.email,
                                style = AppTypo.body(),
                                color = textSecondary
                            )

                            Spacer(Modifier.height(6.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                // Grupos
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = cardColor)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Grupos a los que perteneces",
                                            color = textPrimary
                                        )
                                    }
                                }

                                Spacer(Modifier.height(6.dp))

                                // Mostrar cada equipo en una card separada
                                if (profile.teams.isEmpty()) {
                                    AddTeamCard(
                                        add = true,
                                        onAddTeam = onAddteam,
                                        onClickTeam = onClickTeam
                                    )
                                } else {
                                    AddTeamCard(
                                        add = false,
                                        profile = profile,
                                        onClickTeam = onClickTeam,
                                        onAddTeam = onAddteam
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    AddTeamCard(
                                        add = true,
                                        onAddTeam = onAddteam,
                                        onClickTeam = onClickTeam
                                    )
                                }
                            }
                        }
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .offset(x = avatarLeft, y = headerHeight - avatarRadius)
                            .clip(CircleShape)
                            .background(avatarBg)
                            .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Mostrar las iniciales del username
                        val initials = if (profile.username.length >= 2) {
                            profile.username.take(2).uppercase()
                        } else {
                            profile.username.uppercase()
                        }
                        Text(initials, style = AppTypo.heading(), color = Color.White)
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
private fun TeamCard(team: ProfileTeam,
                     cardColor: Color,
                     onClick: () -> Unit
                    ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "• ${team.name}",
                color = textPrimary,
                style = AppTypo.body(),
                fontSize = 16.sp
            )
            Text(
                text = "Código: ${team.join_code}",
                color = textSecondary,
                style = AppTypo.body(),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AddCard(cardColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(75.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Icon(
                painter = painterResource(Res.drawable.ico_plus),
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(75.dp)
            )
        }
    }
}