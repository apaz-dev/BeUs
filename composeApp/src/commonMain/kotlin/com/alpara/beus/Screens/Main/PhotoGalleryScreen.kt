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
import androidx.compose.material.icons.filled.Image
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
import com.alpara.beus.Themes.textSecondary
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
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = accentColor)
                        if (uiState.isUploading) {
                            Text(
                                "Subiendo foto…",
                                style = AppTypo.body(),
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                uiState.photos.isEmpty() -> {
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
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Text(
                                "Sin fotos aún",
                                style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                                fontSize = 16.sp,
                                color = onSurface
                            )
                            Text(
                                "Pulsa + para subir la primera",
                                style = AppTypo.body(),
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.photos) { photo ->
                            PhotoGridItem(
                                photo = photo,
                                isOwner = currentUserId == photo.uploadedBy,
                                onDeleteClick = { photoToDelete = photo },
                                accentColor = accentColor,
                                borderGlass = borderGlass
                            )
                        }
                    }
                }
            }

            // Snackbar de error
            uiState.error?.let { error ->
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(error, color = Color(0xFFFF6B6B), fontSize = 13.sp, style = AppTypo.body(), modifier = Modifier.weight(1f))
                        Text(
                            "Cerrar",
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { viewModel.clearMessages() }
                        )
                    }
                }
            }

            // Snackbar de éxito
            uiState.successMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = if (isDark) 0.2f else 0.1f))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(msg, color = accentColor, fontSize = 13.sp, style = AppTypo.body())
                }
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
            containerColor = glassBase,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Añadir descripción",
                    color = onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Descripción (opcional)", fontSize = 13.sp) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = borderGlass,
                        focusedLabelColor = accentColor,
                        cursorColor = accentColor
                    )
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable {
                            pendingImageBytes?.let { bytes ->
                                viewModel.uploadPhoto(bytes, teamId, eventId, captionText)
                            }
                            showCaptionDialog = false
                            pendingImageBytes = null
                            captionText = ""
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Subir", color = accentColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(borderGlass.copy(alpha = 0.3f))
                        .clickable {
                            showCaptionDialog = false
                            pendingImageBytes = null
                            captionText = ""
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cancelar", color = textSecondary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        )
    }

    // Diálogo: confirmar borrado
    photoToDelete?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            containerColor = glassBase,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "¿Borrar foto?",
                    color = onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "Esta acción no se puede deshacer.",
                    color = textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFF6B6B).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.deletePhoto(photo, teamId, eventId)
                            photoToDelete = null
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Eliminar", color = Color(0xFFFF6B6B), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { photoToDelete = null }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cancelar", color = accentColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        )
    }
}

@Composable
private fun PhotoGridItem(
    photo: PhotoModel,
    isOwner: Boolean,
    onDeleteClick: () -> Unit,
    accentColor: Color = Color(0xFF4F5BFF),
    borderGlass: Color = Color(0x55FFFFFF)
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderGlass, RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.07f))
    ) {
        AsyncImage(
            model = photo.publicUrl,
            contentDescription = photo.caption.ifBlank { "Foto" },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Botón de borrar visible solo para el autor
        if (isOwner) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Borrar foto",
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Caption si la tiene
        if (photo.caption.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = photo.caption,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    fontSize = 10.sp
                )
            }
        }
    }
}
