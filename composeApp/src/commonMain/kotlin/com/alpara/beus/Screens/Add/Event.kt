package com.alpara.beus.Screens.Add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alpara.beus.Models.View.EventListViewModel
import com.alpara.beus.Themes.AppTypo.body
import com.alpara.beus.Themes.AppTypo.heading
import com.alpara.beus.Themes.BackgroundColor
import com.alpara.beus.Themes.ColorBlack
import com.alpara.beus.Themes.ColorWhite
import com.alpara.beus.Utils.EventType
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_arrowleft
import com.alpara.beus.resources.ico_calendar
import com.alpara.beus.resources.ico_search
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun EventScreenPreview() {
    EventScreen(
        onHomeBack = {},
        search = {},
        onAddEvent = { _, _ -> },
    )
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
    // Estado para el diálogo de nombre
    var pendingEventType by remember { mutableStateOf<EventType?>(null) }
    var eventName by remember { mutableStateOf("") }

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
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Transparent
            ) {

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    IconButton(
                        onClick = onHomeBack,
                        modifier = Modifier.align(Alignment.TopStart).padding(top = 30.dp)
                            .padding(horizontal = 25.dp)
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.ico_arrowleft),
                            contentDescription = "Flecha",
                            modifier = Modifier.size(35.dp),
                        )
                    }
                    IconButton(
                        onClick = search,
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 30.dp)
                            .padding(horizontal = 25.dp)
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.ico_search),
                            contentDescription = "Lupa",
                            modifier = Modifier.size(35.dp),
                        )
                    }

                    Text(
                        text = "BeUs",
                        style = heading(),
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                    )

                    Text(
                        text = "¿Que evento vais a elegir hoy?",
                        style = body(),
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 96.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 150.dp, bottom = 140.dp).background(ColorBlack)
                ) {
                    item {
                        EventOption(
                            onClick = { pendingEventType = EventType.FIESTA; eventName = "" },
                            icon = Res.drawable.ico_calendar,
                            text = "¿Fiesta?"
                        )
                    }
                    item {
                        EventOption(
                            onClick = { pendingEventType = EventType.BAR; eventName = "" },
                            icon = Res.drawable.ico_calendar,
                            text = "Quedada con amigos",
                        )
                    }
                    item {
                        EventOption(
                            onClick = { pendingEventType = EventType.MONTANA; eventName = "" },
                            icon = Res.drawable.ico_calendar,
                            text = "¿Día de montaña?"
                        )
                    }
                    item {
                        EventOption(
                            onClick = { pendingEventType = EventType.CENA; eventName = "" },
                            icon = Res.drawable.ico_calendar,
                            text = "¿Tomando algo?"
                        )
                    }
                    item {
                        EventOption(
                            onClick = { pendingEventType = EventType.VIAJE; eventName = "" },
                            icon = Res.drawable.ico_calendar,
                            text = "¿Vacaciones de playa?"
                        )
                    }
                }
            }
        }
    }

    // Diálogo para introducir el nombre del evento
    pendingEventType?.let { selectedType ->
        AlertDialog(
            onDismissRequest = { pendingEventType = null },
            title = { Text("Nombre del evento") },
            text = {
                Column {
                    Text(
                        text = "Dale un nombre a tu evento:",
                        style = body()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        placeholder = { Text("Ej: Cumple de Carlos") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = eventName.trim().ifBlank { selectedType.name }
                        onAddEvent(selectedType, finalName)
                        pendingEventType = null
                    }
                ) {
                    Text("Crear evento")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingEventType = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EventOption(
    onClick: () -> Unit = {},
    icon: DrawableResource,
    text: String
) {
    Card (
        modifier = Modifier
            .fillMaxWidth().background(ColorWhite)
            .padding(vertical = 24.dp, horizontal = 32.dp)
            .border(2.dp, Color.Black, shape = RoundedCornerShape(6.dp))
            .height(56.dp),
        onClick = onClick
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .background(ColorWhite),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = text,
                style = body(),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}