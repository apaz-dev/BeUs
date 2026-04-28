package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.alpara.beus.BarNav.ActiveTeamArgs
import com.alpara.beus.Models.PhotoModel
import com.alpara.beus.Models.View.CalendarViewModel
import com.alpara.beus.Models.View.DayEvents
import com.alpara.beus.Screens.Auth.GlassTextField
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Utils.rememberImagePickerLauncher
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.calendar
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    onOpenEvent: (teamId: String, eventId: String, eventName: String, endDate: String?) -> Unit = { _, _, _, _ -> }
) {
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2 = if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val bgColor = MaterialTheme.colorScheme.background

    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = remember { calendarViewModel.getCurrentUserId() }

    var showDayDetail by remember { mutableStateOf(false) }
    var selectedDateForDetail by remember { mutableStateOf<LocalDate?>(null) }
    var pendingPhotoDate by remember { mutableStateOf<LocalDate?>(null) }
    var pendingImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showCaptionDialog by remember { mutableStateOf(false) }
    var captionText by remember { mutableStateOf("") }

    val imagePicker = rememberImagePickerLauncher { bytes ->
        if (bytes != null) {
            pendingImageBytes = bytes
            showCaptionDialog = true
        }
    }

    LaunchedEffect(Unit) {
        calendarViewModel.initializeCurrentMonth()
    }
    LaunchedEffect(ActiveTeamArgs.teamId) {
        calendarViewModel.refreshForActiveTeam(ActiveTeamArgs.teamId)
    }
    LaunchedEffect(uiState.successMessage) {
        if (!uiState.successMessage.isNullOrBlank()) {
            kotlinx.coroutines.delay(2000)
            calendarViewModel.clearMessages()
        }
    }

    val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
    val calendarBottomMargin = 12.dp + 70.dp + 12.dp + navBarInsets.calculateBottomPadding()
    val currentMonthIndex = remember(uiState.currentYear, uiState.currentMonth) {
        getMonthIndex(uiState.currentYear, uiState.currentMonth)
    }
    val previousMonthsCount = remember(currentMonthIndex, uiState.dayEvents) {
        val oldestEventMonthIndex = uiState.dayEvents.keys
            .minOfOrNull { date -> getMonthIndex(date.year, date.monthNumber) }
            ?: currentMonthIndex

        maxOf(12, currentMonthIndex - oldestEventMonthIndex)
    }
    val nextMonthsCount = 18
    val visibleMonths = remember(uiState.currentYear, uiState.currentMonth, previousMonthsCount) {
        List(previousMonthsCount + 1 + nextMonthsCount) { index ->
            getMonthStartFromOffset(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                offset = index - previousMonthsCount
            )
        }
    }
    val calendarListState = rememberLazyListState(initialFirstVisibleItemIndex = previousMonthsCount)
    var returnToCurrentMonthRequest by remember { mutableStateOf(0) }

    LaunchedEffect(uiState.currentYear, uiState.currentMonth, previousMonthsCount) {
        calendarListState.scrollToItem(previousMonthsCount)
    }
    LaunchedEffect(returnToCurrentMonthRequest) {
        if (returnToCurrentMonthRequest > 0) {
            calendarListState.animateScrollToItem(previousMonthsCount)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.calendar),
                    style = AppTypo.heading().copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accentColor, accentColor2)
                        )
                    ),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )

                FilledTonalButton(
                    onClick = { returnToCurrentMonthRequest++ },
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = accentColor.copy(alpha = if (isDark) 0.18f else 0.12f),
                        contentColor = accentColor
                    )
                ) {
                    Text(
                        text = "Hoy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (!uiState.error.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .background(
                        Color.Red.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.error ?: "Error desconocido",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        if (uiState.isLoading && uiState.dayEvents.isEmpty() && uiState.calendarDayPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = accentColor)
            }
        } else {
            val calendarSurface = if (isDark) Color(0xFF1C1E26) else Color.White
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        start = 20.dp,
                        top = 4.dp,
                        end = 20.dp,
                        bottom = calendarBottomMargin
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 440.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(28.dp))
                        .background(calendarSurface)
                ) {
                    LazyColumn(
                        state = calendarListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 18.dp,
                            top = 12.dp,
                            end = 18.dp,
                            bottom = 18.dp
                        )
                    ) {
                        items(
                            items = visibleMonths,
                            key = { monthStart -> "${monthStart.year}-${monthStart.monthNumber}" }
                        ) { monthStart ->
                            CalendarMonthView(
                                modifier = Modifier.fillMaxWidth(),
                                year = monthStart.year,
                                month = monthStart.monthNumber,
                                today = uiState.today,
                                onDateSelected = { date ->
                                    selectedDateForDetail = date
                                    showDayDetail = true
                                    calendarViewModel.loadPhotosForDate(date)
                                },
                                hasEventsOnDate = { date -> uiState.dayEvents.containsKey(date) },
                                hasPhotosOnDate = { date -> !uiState.calendarDayPhotos[date].isNullOrEmpty() },
                                accentColor = accentColor,
                                accentColor2 = accentColor2
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDayDetail && selectedDateForDetail != null) {
        val selectedDate = selectedDateForDetail!!

        DayDetailDialog(
            date = selectedDate,
            dayEvents = uiState.dayEvents[selectedDate],
            calendarPhotos = uiState.calendarDayPhotos[selectedDate] ?: emptyList(),
            currentUserId = currentUserId,
            accentColor = accentColor,
            onAddPhoto = {
                pendingPhotoDate = selectedDate
                imagePicker.launch()
            },
            onDeletePhoto = { photo ->
                calendarViewModel.deletePhotoFromDate(selectedDate, photo)
            },
            onOpenEvent = { event ->
                val teamId = event.teamId.ifBlank { uiState.activeTeamId }
                showDayDetail = false
                onOpenEvent(teamId, event.id, event.name, event.endDate)
            },
            onDismiss = { showDayDetail = false }
        )
    }

    if (showCaptionDialog) {
        AlertDialog(
            onDismissRequest = {
                showCaptionDialog = false
                pendingImageBytes = null
                captionText = ""
                pendingPhotoDate = null
            },
            containerColor = bgColor,
            title = {
                Text(
                    text = "Añadir foto al día",
                    color = if (isDark) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val selected = pendingPhotoDate
                    Text(
                        text = if (selected != null) {
                            "Fecha: ${selected.dayOfMonth}/${selected.monthNumber}/${selected.year}"
                        } else {
                            "Selecciona una fecha"
                        },
                        color = if (isDark) Color(0xFFB0B0B0) else Color(0xFF666666),
                        fontSize = 13.sp
                    )
                    GlassTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        placeholder = "Descripción opcional",
                        accentColor = accentColor,
                        borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x22000000),
                        glassBase = if (isDark) Color(0xFF1C1E26) else Color.White,
                        onSurface = if (isDark) Color.White else Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = pendingImageBytes != null && pendingPhotoDate != null && !uiState.isUploadingPhoto,
                    onClick = {
                        val date = pendingPhotoDate
                        val bytes = pendingImageBytes
                        if (date != null && bytes != null) {
                            calendarViewModel.uploadPhotoToDate(
                                date = date,
                                imageBytes = bytes,
                                caption = captionText
                            )
                            calendarViewModel.loadPhotosForDate(date)
                        }
                        showCaptionDialog = false
                        pendingImageBytes = null
                        captionText = ""
                        pendingPhotoDate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Subir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCaptionDialog = false
                        pendingImageBytes = null
                        captionText = ""
                        pendingPhotoDate = null
                    }
                ) {
                    Text("Cancelar", color = accentColor)
                }
            }
        )
    }
}

@Composable
fun CalendarMonthView(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    hasEventsOnDate: (LocalDate) -> Boolean,
    hasPhotosOnDate: (LocalDate) -> Boolean,
    accentColor: Color,
    accentColor2: Color
) {
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color(0xFFB0B0B0) else Color(0xFF666666)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 8.dp)
    ) {
        Text(
            text = getMonthYearString(year, month),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(start = 2.dp, bottom = 14.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")
            for (day in daysOfWeek) {
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = subTextColor,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        val daysInMonth = getDaysInMonth(year, month)
        val firstDayOfWeek = getFirstDayOfMonth(year, month)

        val weeks = mutableListOf<List<Int?>>()
        var currentWeek = MutableList<Int?>(7) { null }

        for (day in 1..daysInMonth) {
            val absolutePosition = firstDayOfWeek + (day - 1)
            val dayIndex = absolutePosition % 7

            currentWeek[dayIndex] = day

            if (dayIndex == 6 || day == daysInMonth) {
                weeks.add(currentWeek.toList())
                currentWeek = MutableList<Int?>(7) { null }
            }
        }

        for (week in weeks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in week) {
                    if (day != null) {
                        val date = LocalDate(year, month, day)
                        val hasEvents = hasEventsOnDate(date)
                        val hasPhotos = hasPhotosOnDate(date)
                        val isToday = date == today
                        val hasActivity = hasEvents || hasPhotos

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> accentColor
                                        hasActivity -> accentColor.copy(alpha = if (isDark) 0.18f else 0.12f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    onDateSelected(date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 17.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isToday) Color.White else textColor
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.height(5.dp)
                                ) {
                                    if (hasEvents) {
                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(if (isToday) Color.White else accentColor)
                                        )
                                    }
                                    if (hasPhotos) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isToday) Color.White else accentColor2)
                                        )
                                    }
                                    if (!hasActivity) {
                                        Spacer(modifier = Modifier.size(4.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 10.dp),
            color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f)
        )
    }
}

@Composable
fun DayDetailDialog(
    date: LocalDate,
    dayEvents: DayEvents?,
    calendarPhotos: List<PhotoModel>,
    currentUserId: String?,
    accentColor: Color,
    onAddPhoto: () -> Unit,
    onDeletePhoto: (PhotoModel) -> Unit,
    onOpenEvent: (com.alpara.beus.Models.EventData) -> Unit,
    onDismiss: () -> Unit
) {
    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color(0xFFB0B0B0) else Color(0xFF666666)
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = if (isDark) Color(0xFF2A2A2A) else Color(0xFFFAFAFA)
    var photoToDelete by remember { mutableStateOf<PhotoModel?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(16.dp)),
        containerColor = bgColor,
        icon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = accentColor,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(4.dp)
            )
        },
        title = {
            Text(
                text = formatDate(date),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = onAddPhoto,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = accentColor.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = accentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Añadir foto",
                            color = accentColor
                        )
                    }
                }

                Text(
                    text = "Eventos del día",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                if (dayEvents == null || dayEvents.events.isEmpty()) {
                    Text(
                        text = "No hay eventos este día",
                        color = subTextColor,
                        fontSize = 14.sp
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = dayEvents.events,
                            key = { event -> event.id }
                        ) { event ->
                            EventDetailCard(
                                event = event,
                                photos = dayEvents.photos[event.id] ?: emptyList(),
                                accentColor = accentColor,
                                surfaceColor = surfaceColor,
                                textColor = textColor,
                                subTextColor = subTextColor,
                                onClick = { onOpenEvent(event) }
                            )
                        }
                    }
                }

                Text(
                    text = "Fotos del día",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                if (calendarPhotos.isEmpty()) {
                    Text(
                        text = "Todavía no hay fotos guardadas en este día",
                        color = subTextColor,
                        fontSize = 14.sp
                    )
                } else {
                    DayPhotosGrid(
                        photos = calendarPhotos,
                        currentUserId = currentUserId,
                        onDeletePhoto = { photo ->
                            photoToDelete = photo
                        },
                        accentColor = accentColor
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor
                )
            ) {
                Text("Cerrar")
            }
        }
    )

    photoToDelete?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            containerColor = bgColor,
            title = {
                Text(
                    text = "Borrar foto",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Text(
                    text = "Esta acción no se puede deshacer.",
                    color = subTextColor
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePhoto(photo)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                ) {
                    Text("Borrar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("Cancelar", color = accentColor)
                }
            }
        )
    }
}

@Composable
fun EventDetailCard(
    event: com.alpara.beus.Models.EventData,
    photos: List<com.alpara.beus.Models.PhotoModel>,
    accentColor: Color,
    surfaceColor: Color,
    textColor: Color,
    subTextColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColor)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    text = displayEventType(event.type),
                    fontSize = 12.sp,
                    color = subTextColor
                )
            }
        }

        if (photos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Fotos del evento (${photos.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = photos,
                    key = { photo -> photo.id }
                ) { photo ->
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = photo.publicUrl,
                            contentDescription = photo.caption,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

private fun displayEventType(type: String): String {
    return if (type.equals("PERSONALIZADO", ignoreCase = true)) {
        "Personalizado"
    } else {
        type
    }
}

@Composable
private fun DayPhotosGrid(
    photos: List<PhotoModel>,
    currentUserId: String? = null,
    onDeletePhoto: ((PhotoModel) -> Unit)? = null,
    accentColor: Color = Color(0xFF4F5BFF)
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        photos.chunked(2).forEach { rowPhotos ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowPhotos.forEach { photo ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = photo.publicUrl,
                            contentDescription = photo.caption,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        if (currentUserId != null && onDeletePhoto != null && currentUserId == photo.uploadedBy) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(5.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .clickable { onDeletePhoto(photo) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Borrar foto",
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
                if (rowPhotos.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Helpers
private fun getMonthYearString(year: Int, month: Int): String {
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    return "${months[month - 1]} $year"
}

private fun getMonthStartFromOffset(year: Int, month: Int, offset: Int): LocalDate {
    val totalMonths = getMonthIndex(year, month) + offset
    val targetYear = totalMonths / 12
    val targetMonth = (totalMonths % 12) + 1
    return LocalDate(targetYear, targetMonth, 1)
}

private fun getMonthIndex(year: Int, month: Int): Int {
    return (year * 12) + (month - 1)
}

private fun formatDate(date: LocalDate): String {
    val dayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val dayOfWeek = date.dayOfWeek.ordinal
    val monthIndex = date.month.ordinal
    return "${dayNames[dayOfWeek]} ${date.dayOfMonth} de ${monthNames[monthIndex]} de ${date.year}"
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 31
    }
}

private fun getFirstDayOfMonth(year: Int, month: Int): Int {
    val firstDay = LocalDate(year, month, 1)
    return firstDay.dayOfWeek.ordinal // Monday=0 ... Sunday=6
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}
