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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.alpara.beus.BarNav.ActiveTeamArgs
import com.alpara.beus.Models.EventData
import com.alpara.beus.Models.View.EventListViewModel
import com.alpara.beus.Models.View.ProfileViewModel
import com.alpara.beus.Models.View.ProfileState
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenGallery: (teamId: String, eventId: String, eventName: String) -> Unit = { _, _, _ -> },
    profileViewModel: ProfileViewModel = remember { ProfileViewModel() },
    eventListViewModel: EventListViewModel = remember { EventListViewModel() }
) {
    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    val uiState by eventListViewModel.uiState.collectAsStateWithLifecycle()

    val teams = remember(profileState) {
        if (profileState is ProfileState.Success)
            (profileState as ProfileState.Success).profile.teams
        else emptyList()
    }
    var selectedTeam by remember(teams) { mutableStateOf(teams.firstOrNull()) }
    val teamId = remember(selectedTeam) {
        selectedTeam?.team_id?.takeIf { it.isNotBlank() } ?: selectedTeam?.join_code ?: ""
    }
    var teamDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(teamId) {
        if (teamId.isNotBlank()) {
            ActiveTeamArgs.teamId = teamId
            eventListViewModel.switchTeam(teamId)
        }
    }
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            eventListViewModel.clearMessages()
        }
    }

    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor     = MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = bgColor,
        topBar = {
            // ── TopBar glass ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                accentColor.copy(alpha = if (isDark) 0.45f else 0.28f),
                                accentColor2.copy(alpha = if (isDark) 0.35f else 0.18f),
                                glassBase.copy(alpha = if (isDark) 0.25f else 0.5f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título con gradiente
                    Text(
                        text = "Inicio",
                        style = AppTypo.heading().copy(
                            brush = Brush.horizontalGradient(
                                colors = listOf(accentColor, accentColor2)
                            )
                        ),
                        fontSize = 26.sp
                    )

                    // Selector de equipo glass
                    if (teams.isNotEmpty()) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accentColor.copy(alpha = 0.12f))
                                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .clickable { teamDropdownExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = selectedTeam?.name ?: "Sin equipo",
                                        color = accentColor,
                                        style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = accentColor.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Dropdown glass
                            DropdownMenu(
                                expanded = teamDropdownExpanded,
                                onDismissRequest = { teamDropdownExpanded = false },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBase)
                                    .border(1.dp, borderGlass, RoundedCornerShape(14.dp))
                            ) {
                                teams.forEach { team ->
                                    val isSelected = team == selectedTeam
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = team.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) accentColor
                                                else MaterialTheme.colorScheme.onSurface,
                                                fontSize = 14.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Groups,
                                                contentDescription = null,
                                                tint = if (isSelected) accentColor
                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        onClick = {
                                            selectedTeam = team
                                            teamDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

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
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            when {
                // ── Loading ───────────────────────────────────────────────
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = accentColor
                    )
                }

                // ── Empty state ───────────────────────────────────────────
                uiState.events.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, borderGlass, RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        glassBase.copy(alpha = 0.75f),
                                        glassBase.copy(alpha = 0.55f)
                                    )
                                )
                            )
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.12f))
                                    .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = accentColor
                                )
                            }
                            Text(
                                "Sin eventos aún",
                                style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Pulsa + en la barra para crear el primero",
                                style = AppTypo.body(),
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // ── Lista de eventos ──────────────────────────────────────
                else -> {
                    val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
                    val bottomPadding = 12.dp + 70.dp + 12.dp + navBarInsets.calculateBottomPadding()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 16.dp, bottom = bottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Cabecera de sección glass
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(accentColor, accentColor2)
                                            )
                                        )
                                )
                                Text(
                                    "Últimos eventos",
                                    style = AppTypo.body().copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        items(uiState.events) { event ->
                            EventCard(
                                event = event,
                                onClick = { onOpenGallery(event.teamId, event.id, event.name) },
                                accentColor = accentColor,
                                accentColor2 = accentColor2,
                                glassBase = glassBase,
                                borderGlass = borderGlass,
                                isDark = isDark
                            )
                        }
                    }
                }
            }

            // ── Snackbar error ─────────────────────────────────────────────
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .background(
                            if (isDark) Color(0xFF2A1A1A).copy(alpha = 0.9f)
                            else Color(0xFFFFF0F0).copy(alpha = 0.95f)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(uiState.error!!, color = Color(0xFFFF6B6B), fontSize = 13.sp, style = AppTypo.body())
                }
            }

            // ── Snackbar éxito ─────────────────────────────────────────────
            if (uiState.successMessage != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = if (isDark) 0.2f else 0.1f))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(uiState.successMessage!!, color = accentColor, fontSize = 13.sp, style = AppTypo.body())
                }
            }
        }
    }
}
@Composable
fun EventCard(
    event: EventData,
    onClick: () -> Unit,
    accentColor: Color = Color(0xFF4F5BFF),
    accentColor2: Color = Color(0xFF8B5CF6),
    glassBase: Color = Color(0xFFFFFFFF),
    borderGlass: Color = Color(0x55FFFFFF),
    isDark: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = event.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "${event.previewPhotos.size} fotos",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (event.previewPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    event.previewPhotos.take(3).forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // Rellenar huecos si hay menos de 3 fotos para mantener tamaño uniforme
                    repeat(3 - event.previewPhotos.size) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sin fotos aún — toca para añadir",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

