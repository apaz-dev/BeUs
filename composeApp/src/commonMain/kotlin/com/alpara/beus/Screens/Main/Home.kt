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
import com.alpara.beus.Themes.AppTheme
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.Utils.EventType
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.all
import com.alpara.beus.resources.home
import com.alpara.beus.resources.latest_events
import com.alpara.beus.resources.no_events
import com.alpara.beus.resources.no_events_hint
import com.alpara.beus.resources.no_photos_hint_card
import com.alpara.beus.resources.no_team
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


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
                        text = stringResource(Res.string.home),
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
                                        text = selectedTeam?.name ?: stringResource(Res.string.no_team),
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
                                stringResource(Res.string.no_events),
                                style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                stringResource(Res.string.no_events_hint),
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

                    // Estado del filtro de tipo de evento
                    var selectedEventType by remember { mutableStateOf<EventType?>(null) }
                    var filterDropdownExpanded by remember { mutableStateOf(false) }

                    val filteredEvents = remember(uiState.events, selectedEventType) {
                        if (selectedEventType == null) uiState.events
                        else uiState.events.filter { it.type.equals(selectedEventType!!.name, ignoreCase = true) }
                    }

                    // Etiquetas con emoji para cada tipo
                    val eventTypeLabels = mapOf(
                        EventType.FIESTA      to "🎉 Fiesta",
                        EventType.BAR         to "🍻 Quedada",
                        EventType.MONTANA     to "🏔️ Montaña",
                        EventType.CENA        to "🍽️ Cena",
                        EventType.VIAJE       to "✈️ Viaje",
                        EventType.COMPETICION to "🏆 Competición"
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 16.dp, bottom = bottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Cabecera de sección glass con filtro
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            ) {
                                // Título "Últimos eventos"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                        stringResource(Res.string.latest_events),
                                        style = AppTypo.body().copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // Desplegable de filtro por tipo
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (selectedEventType != null)
                                                    accentColor.copy(alpha = 0.18f)
                                                else
                                                    accentColor.copy(alpha = 0.08f)
                                            )
                                            .border(
                                                1.dp,
                                                if (selectedEventType != null)
                                                    accentColor.copy(alpha = 0.5f)
                                                else
                                                    accentColor.copy(alpha = 0.25f),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { filterDropdownExpanded = true }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = if (selectedEventType != null)
                                                    eventTypeLabels[selectedEventType] ?: selectedEventType!!.name
                                                else stringResource(Res.string.all),
                                                color = accentColor,
                                                style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                                                fontSize = 12.sp,
                                                maxLines = 1
                                            )
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = accentColor.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = filterDropdownExpanded,
                                        onDismissRequest = { filterDropdownExpanded = false },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(glassBase)
                                            .border(1.dp, borderGlass, RoundedCornerShape(14.dp))
                                    ) {
                                        // Opción "Todos"
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    stringResource(Res.string.all),
                                                    fontWeight = if (selectedEventType == null) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedEventType == null) accentColor
                                                    else MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 14.sp
                                                )
                                            },
                                            onClick = {
                                                selectedEventType = null
                                                filterDropdownExpanded = false
                                            }
                                        )
                                        // Opciones por tipo
                                        EventType.entries.forEach { type ->
                                            val isSelected = type == selectedEventType
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = eventTypeLabels[type] ?: type.name,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) accentColor
                                                        else MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                onClick = {
                                                    selectedEventType = type
                                                    filterDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        items(filteredEvents) { event ->
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
                        // Mensaje si no hay resultados para el filtro aplicado
                        if (filteredEvents.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay eventos de tipo \"${eventTypeLabels[selectedEventType] ?: selectedEventType?.name}\"",
                                        style = AppTypo.body(),
                                        fontSize = 13.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

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
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
                        glassBase.copy(alpha = 0.78f),
                        glassBase.copy(alpha = 0.55f)
                    )
                )
            )
            .clickable { onClick() }
    ) {
        // Barra de acento izquierda
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor, accentColor2.copy(alpha = 0.4f))
                    )
                )
        )

        Column(modifier = Modifier.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name,
                        style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = event.type,
                        style = AppTypo.body(),
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }

                // Chip de fotos glass
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${event.previewPhotos.size} fotos",
                        style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                        fontSize = 11.sp,
                        color = accentColor
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
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    repeat(3 - event.previewPhotos.size) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(accentColor.copy(alpha = 0.07f))
                                .border(1.dp, borderGlass, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = accentColor.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.07f))
                        .border(1.dp, borderGlass, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.no_photos_hint_card),
                        style = AppTypo.body(),
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }
        }
    }
}

private val previewEvents = listOf(
    EventData(
        id = "1", teamId = "t1",
        name = "Fiesta de cumpleaños",
        type = "FIESTA",
        previewPhotos = listOf(
            "https://picsum.photos/seed/a/200",
            "https://picsum.photos/seed/b/200"
        )
    ),
    EventData(
        id = "2", teamId = "t1",
        name = "Cena de Navidad",
        type = "CENA",
        previewPhotos = emptyList()
    ),
    EventData(
        id = "3", teamId = "t1",
        name = "Viaje a la montaña",
        type = "VIAJE",
        previewPhotos = listOf(
            "https://picsum.photos/seed/c/200",
            "https://picsum.photos/seed/d/200",
            "https://picsum.photos/seed/e/200"
        )
    )
)

@Preview(name = "Home — Con eventos (Light)")
@Composable
fun HomeEventCardLightPreview() {
    AppTheme(darkMode = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                previewEvents.forEach { event ->
                    EventCard(
                        event = event,
                        onClick = {},
                        isDark = false
                    )
                }
            }
        }
    }
}

@Preview(name = "Home — Con eventos (Dark)")
@Composable
fun HomeEventCardDarkPreview() {
    AppTheme(darkMode = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                previewEvents.forEach { event ->
                    EventCard(
                        event = event,
                        onClick = {},
                        isDark = true,
                        accentColor = Color(0xFF7C8BFF),
                        accentColor2 = Color(0xFFB06EFF),
                        glassBase = Color(0xFF1C1E26),
                        borderGlass = Color(0x44FFFFFF)
                    )
                }
            }
        }
    }
}

@Preview(name = "Home — Sin eventos (Light)")
@Composable
fun HomeEmptyLightPreview() {
    AppTheme(darkMode = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            val accentColor  = Color(0xFF4F5BFF)
            val accentColor2 = Color(0xFF8B5CF6)
            val glassBase    = Color(0xFFFFFFFF)
            val borderGlass  = Color(0x55FFFFFF)
            Box(
                modifier = Modifier
                    .padding(32.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, borderGlass, RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(glassBase.copy(alpha = 0.75f), glassBase.copy(alpha = 0.55f))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
