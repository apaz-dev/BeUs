package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.alpara.beus.BarNav.ActiveTeamArgs
import com.alpara.beus.Models.EventData
import com.alpara.beus.Models.View.EventListViewModel
import com.alpara.beus.Models.View.ProfileViewModel
import com.alpara.beus.Models.View.ProfileState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenGallery: (teamId: String, eventId: String, eventName: String) -> Unit = { _, _, _ -> },
    profileViewModel: ProfileViewModel = remember { ProfileViewModel() },
    eventListViewModel: EventListViewModel = remember { EventListViewModel() }
) {
    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    val uiState by eventListViewModel.uiState.collectAsStateWithLifecycle()

    // Lista de equipos del usuario
    val teams = remember(profileState) {
        if (profileState is ProfileState.Success) {
            (profileState as ProfileState.Success).profile.teams
        } else emptyList()
    }

    // Equipo actualmente seleccionado (por defecto el primero)
    var selectedTeam by remember(teams) { mutableStateOf(teams.firstOrNull()) }

    // Obtener el teamId del equipo seleccionado
    val teamId = remember(selectedTeam) {
        selectedTeam?.team_id?.takeIf { it.isNotBlank() }
            ?: selectedTeam?.join_code ?: ""
    }

    // Estado del dropdown de equipos
    var teamDropdownExpanded by remember { mutableStateOf(false) }

    // Cargar eventos al obtener el teamId (limpiando caché siempre al cambiar de equipo)
    LaunchedEffect(teamId) {
        if (teamId.isNotBlank()) {
            ActiveTeamArgs.teamId = teamId
            eventListViewModel.switchTeam(teamId)
        }
    }

    // Limpiar mensajes tras mostrarlos
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            eventListViewModel.clearMessages()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    // Dropdown de selección de equipo
                    if (teams.isNotEmpty()) {
                        Box {
                            AssistChip(
                                onClick = { teamDropdownExpanded = true },
                                label = {
                                    Text(
                                        text = selectedTeam?.name ?: "Sin equipo",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Groups,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Seleccionar equipo",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            DropdownMenu(
                                expanded = teamDropdownExpanded,
                                onDismissRequest = { teamDropdownExpanded = false }
                            ) {
                                teams.forEach { team ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = team.name,
                                                fontWeight = if (team == selectedTeam) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Groups,
                                                contentDescription = null,
                                                tint = if (team == selectedTeam)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        },
                                        onClick = {
                                            selectedTeam = team
                                            teamDropdownExpanded = false
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = if (team == selectedTeam)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.events.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Text(
                            "No hay eventos todavía",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            "Pulsa + en la barra para crear el primero",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                else -> {
                    val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
                    val bottomPadding = 12.dp + 70.dp + 12.dp + navBarInsets.calculateBottomPadding()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = bottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Últimos eventos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(uiState.events) { event ->
                            EventCard(
                                event = event,
                                onClick = {
                                    onOpenGallery(event.teamId, event.id, event.name)
                                }
                            )
                        }
                    }
                }
            }

            // Snackbar de error / éxito
            if (uiState.error != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) { Text(uiState.error!!) }
            }
            if (uiState.successMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        uiState.successMessage!!,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
@Composable
fun EventCard(
    event: EventData,
    onClick: () -> Unit
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

