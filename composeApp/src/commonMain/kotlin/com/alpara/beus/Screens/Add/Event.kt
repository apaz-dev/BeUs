package com.alpara.beus.Screens.Add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.alpara.beus.Models.View.EventListViewModel
import com.alpara.beus.Screens.Auth.GlassTextField
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.Utils.EventType
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.cancel
import com.alpara.beus.resources.create_event
import com.alpara.beus.resources.event_name_hint
import com.alpara.beus.resources.event_subtitle
import com.alpara.beus.resources.event_title_question
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.new_event
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Metadatos por tipo de evento
private data class EventOption(
    val type: EventType,
    val emoji: String,
    val title: String,
    val subtitle: String
)

private val eventOptions = listOf(
    EventOption(EventType.FIESTA,     "🎉", "Fiesta",              "Celebra con los tuyos"),
    EventOption(EventType.BAR,        "🍻", "Quedada",             "Salir con amigos"),
    EventOption(EventType.MONTANA,    "🏔️", "Día de montaña",      "Aventura al aire libre"),
    EventOption(EventType.CENA,       "🍽️", "Cena o tapas",        "Buena mesa, mejor compañía"),
    EventOption(EventType.VIAJE,      "✈️", "Viaje o vacaciones",  "Nuevos destinos, nuevos recuerdos"),
    EventOption(EventType.COMPETICION,"🏆", "Competición",         "Que gane el mejor"),
)

@Composable
@Preview
fun EventScreenPreview() {
    EventScreen(onHomeBack = {}, search = {}, onAddEvent = { _, _ -> })
}

@Composable
fun EventScreenCall(
    teamId: String,
    onBack: () -> Unit = {},
    viewModel: EventListViewModel = remember { EventListViewModel() }
) {
    EventScreen(
        onHomeBack = onBack,
        search = {},
        onAddEvent = { type, name ->
            viewModel.createEvent(teamId, name = name, type = type)
            onBack()
        }
    )
}

@Composable
fun EventScreen(
    onHomeBack: () -> Unit,
    search: () -> Unit = {},
    onAddEvent: (EventType, String) -> Unit
) {
    var pendingEventType by remember { mutableStateOf<EventType?>(null) }
    var eventName by remember { mutableStateOf("") }

    // ── Glassmorphism palette ──────────────────────────────────────────────
    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor     = MaterialTheme.colorScheme.background
    val onSurface   = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // ── TopBar glass ──────────────────────────────────────────────────
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
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón back glass
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(glassBase.copy(alpha = 0.5f))
                        .border(1.dp, borderGlass, CircleShape)
                        .clickable { onHomeBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ico_arrowleft),
                        contentDescription = "Volver",
                        tint = if (isDark) Color.White.copy(alpha = 0.85f) else accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                // Título con gradiente centrado
                Text(
                    text = stringResource(Res.string.new_event),
                    style = AppTypo.heading().copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accentColor, accentColor2)
                        )
                    ),
                    fontSize = 22.sp
                )

                Spacer(Modifier.weight(1f))
                // Espacio simétrico al botón back
                Spacer(Modifier.size(38.dp))
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

        // ── Contenido ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 66.dp
                )
        ) {
            // Subtítulo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.event_title_question),
                    style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                    fontSize = 18.sp,
                    color = onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.event_subtitle),
                    style = AppTypo.body(),
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }

            // Lista de opciones
            val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 4.dp,
                    bottom = 80.dp + navBarInsets.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(eventOptions) { index, option ->
                    // Acento alternado: accentColor y accentColor2
                    val itemAccent = if (index % 2 == 0) accentColor else accentColor2

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
                            .clickable {
                                pendingEventType = option.type
                                eventName = ""
                            }
                    ) {
                        // Barra de acento izquierda
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(4.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(itemAccent, itemAccent.copy(alpha = 0.3f))
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Emoji en caja glass
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(itemAccent.copy(alpha = 0.12f))
                                    .border(1.dp, itemAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option.emoji, fontSize = 22.sp)
                            }

                            // Título y subtítulo
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                                    fontSize = 15.sp,
                                    color = onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = option.subtitle,
                                    style = AppTypo.body(),
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }

                            // Chevron
                            Text(
                                text = "›",
                                fontSize = 24.sp,
                                color = itemAccent.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Diálogo: nombre del evento ─────────────────────────────────────────
    pendingEventType?.let { selectedType ->
        val meta = eventOptions.find { it.type == selectedType }

        AlertDialog(
            onDismissRequest = { pendingEventType = null },
            containerColor = glassBase,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (meta != null) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(accentColor.copy(alpha = 0.12f))
                                .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(meta.emoji, fontSize = 20.sp)
                        }
                    }
                    Text(
                        text = meta?.title ?: "Nuevo evento",
                        style = AppTypo.heading().copy(
                            brush = Brush.horizontalGradient(colors = listOf(accentColor, accentColor2))
                        ),
                        fontSize = 22.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.event_name_hint),
                        style = AppTypo.body(),
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                    GlassTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        placeholder = "Ej: Cumple de Carlos",
                        accentColor = accentColor,
                        borderGlass = borderGlass,
                        glassBase = glassBase,
                        onSurface = onSurface
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(colors = listOf(accentColor, accentColor2))
                        )
                        .clickable {
                            val finalName = eventName.trim().ifBlank { selectedType.name }
                            onAddEvent(selectedType, finalName)
                            pendingEventType = null
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        stringResource(Res.string.create_event),
                        color = Color.White,
                        style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.10f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { pendingEventType = null }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        stringResource(Res.string.cancel),
                        color = accentColor,
                        style = AppTypo.body().copy(fontWeight = FontWeight.Medium),
                        fontSize = 14.sp
                    )
                }
            }
        )
    }
}