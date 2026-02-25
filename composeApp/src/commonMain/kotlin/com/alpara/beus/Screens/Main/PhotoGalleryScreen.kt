package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.alpara.beus.Models.PhotoModel
import com.alpara.beus.Models.View.PhotoViewModel
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Utils.rememberImagePickerLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    teamId: String,
    eventId: String,
    eventName: String = "Evento",
    onBack: () -> Unit,
    viewModel: PhotoViewModel = remember { PhotoViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = remember { viewModel.getCurrentUserId() }

    var photoToDelete by remember { mutableStateOf<PhotoModel?>(null) }
    var showCaptionDialog by remember { mutableStateOf(false) }
    var pendingImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var captionText by remember { mutableStateOf("") }

    val imagePicker = rememberImagePickerLauncher { bytes ->
        if (bytes != null) {
            pendingImageBytes = bytes
            showCaptionDialog = true
        }
    }

    LaunchedEffect(teamId, eventId) { viewModel.loadPhotos(teamId, eventId) }
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val bgColor     = MaterialTheme.colorScheme.background
    val onSurface   = MaterialTheme.colorScheme.onSurface

    Scaffold(
        containerColor = bgColor,
        topBar = {
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
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón back glass
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(glassBase.copy(alpha = 0.5f))
                            .border(1.dp, borderGlass, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = if (isDark) Color.White.copy(alpha = 0.85f) else accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Título con gradiente
                    Text(
                        text = eventName,
                        style = AppTypo.heading().copy(
                            brush = Brush.horizontalGradient(
                                colors = listOf(accentColor, accentColor2)
                            )
                        ),
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    // Badge de fotos
                    if (uiState.photos.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(accentColor.copy(alpha = 0.12f))
                                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${uiState.photos.size} fotos",
                                color = accentColor,
                                style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
                                fontSize = 12.sp
                            )
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
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(accentColor, accentColor2)
                        )
                    )
                    .border(1.dp, borderGlass, CircleShape)
                    .clickable { imagePicker.launch() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Subir foto",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            when {
                uiState.isLoading || uiState.isUploading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.photos.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No hay fotos todavía",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Pulsa + para subir la primera",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.photos) { photo ->
                            PhotoGridItem(
                                photo = photo,
                                isOwner = currentUserId == photo.uploadedBy,
                                onDeleteClick = { photoToDelete = photo }
                            )
                        }
                    }
                }
            }

            // Snackbar de error
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearMessages() }) {
                            Text("Cerrar")
                        }
                    }
                ) { Text(error) }
            }

            // Snackbar de éxito
            uiState.successMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) { Text(msg) }
            }
        }
    }

    // Diálogo: añadir descripción antes de subir
    if (showCaptionDialog) {
        AlertDialog(
            onDismissRequest = {
                showCaptionDialog = false
                pendingImageBytes = null
                captionText = ""
            },
            title = { Text("Añadir descripción") },
            text = {
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Descripción (opcional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    pendingImageBytes?.let { bytes ->
                        viewModel.uploadPhoto(bytes, teamId, eventId, captionText)
                    }
                    showCaptionDialog = false
                    pendingImageBytes = null
                    captionText = ""
                }) {
                    Text("Subir")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCaptionDialog = false
                    pendingImageBytes = null
                    captionText = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo: confirmar borrado
    photoToDelete?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("Borrar foto") },
            text = { Text("¿Seguro que quieres eliminar esta foto? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePhoto(photo, teamId, eventId)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun PhotoGridItem(
    photo: PhotoModel,
    isOwner: Boolean,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = photo.publicUrl,
            contentDescription = photo.caption.ifBlank { "Foto" },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Botón de borrar visible solo para el autor
        if (isOwner) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(28.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Borrar foto",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Caption si la tiene
        if (photo.caption.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = photo.caption,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}
