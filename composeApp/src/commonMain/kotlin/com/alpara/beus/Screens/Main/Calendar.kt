package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
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
    calendarViewModel: CalendarViewModel = remember { CalendarViewModel() }
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
    LaunchedEffect(uiState.successMessage) {
        if (!uiState.successMessage.isNullOrBlank()) {
            kotlinx.coroutines.delay(2000)
            calendarViewModel.clearMessages()
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
            Text(
                text = stringResource(Res.string.calendar),
                style = AppTypo.heading().copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(accentColor, accentColor2)
                    )
                ),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CalendarMonthView(
                    modifier = Modifier
                        .widthIn(max = 440.dp)
                        .padding(top = 6.dp),
                    year = uiState.currentYear,
                    month = uiState.currentMonth,
                    today = uiState.today,
                    onPreviousMonth = { calendarViewModel.goToPreviousMonth() },
                    onNextMonth = { calendarViewModel.goToNextMonth() },
                    onDateSelected = { date ->
                        selectedDateForDetail = date
                        showDayDetail = true
                        calendarViewModel.loadPhotosForDate(date)
                    },
                    hasEventsOnDate = { date -> calendarViewModel.hasEventsOnDate(date) },
                    hasPhotosOnDate = { date -> calendarViewModel.hasPhotosOnDate(date) },
                    accentColor = accentColor,
                    accentColor2 = accentColor2
                )
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
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
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
    val cardBase = if (isDark) Color(0xFF1C1E26) else Color.White
    val cardBorder = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = accentColor.copy(alpha = if (isDark) 0.28f else 0.16f),
                spotColor = accentColor2.copy(alpha = if (isDark) 0.32f else 0.18f)
            )
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        cardBorder,
                        accentColor.copy(alpha = 0.25f),
                        cardBorder
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        cardBase.copy(alpha = 0.95f),
                        accentColor.copy(alpha = if (isDark) 0.12f else 0.06f),
                        accentColor2.copy(alpha = if (isDark) 0.08f else 0.04f)
                    )
                )
            )
            .animateContentSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Mes anterior",
                        tint = accentColor
                    )
                }

                Text(
                    text = getMonthYearString(year, month),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )

                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mes siguiente",
                        tint = accentColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sab", "Dom")
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
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (day in week) {
                        if (day != null) {
                            val date = LocalDate(year, month, day)
                            val hasEvents = hasEventsOnDate(date)
                            val hasPhotos = hasPhotosOnDate(date)
                            val isToday = date == today

                            val baseBackground =
                                if (hasEvents || hasPhotos) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            accentColor.copy(alpha = 0.22f),
                                            accentColor2.copy(alpha = 0.2f)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            if (isDark) Color(0xFF2A2A2A) else Color(0xFFF6F6FA),
                                            if (isDark) Color(0xFF23252E) else Color(0xFFF8F8FC)
                                        )
                                    )
                                }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(baseBackground)
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
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isToday) accentColor.copy(alpha = 0.28f)
                                                else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            fontSize = 16.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isToday) accentColor else textColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (hasEvents) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(accentColor)
                                            )
                                        }
                                        if (hasPhotos) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(accentColor2)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                item {
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

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (dayEvents == null || dayEvents.events.isEmpty()) {
                    item {
                        Text(
                            text = "No hay eventos este día",
                            color = subTextColor,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    dayEvents.events.forEachIndexed { index, event ->
                        item {
                            EventDetailCard(
                                event = event,
                                photos = dayEvents.photos[event.id] ?: emptyList(),
                                accentColor = accentColor,
                                surfaceColor = surfaceColor,
                                textColor = textColor,
                                subTextColor = subTextColor
                            )
                            if (index < dayEvents.events.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fotos del día",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (calendarPhotos.isEmpty()) {
                    item {
                        Text(
                            text = "Todavía no hay fotos guardadas en este día",
                            color = subTextColor,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    item {
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
    subTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColor)
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
                    text = event.type,
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

            DayPhotosGrid(photos = photos)
        }
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