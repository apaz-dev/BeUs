package com.alpara.beus.Screens.Main

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.alpara.beus.Models.PhotoModel
import com.alpara.beus.Models.View.PhotoViewModel
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

    // Estado para el diálogo de confirmación de borrado
    var photoToDelete by remember { mutableStateOf<PhotoModel?>(null) }
    // Estado para el diálogo de descripción al subir
    var showCaptionDialog by remember { mutableStateOf(false) }
    var pendingImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var captionText by remember { mutableStateOf("") }

    // Image picker
    val imagePicker = rememberImagePickerLauncher { bytes ->
        if (bytes != null) {
            pendingImageBytes = bytes
            showCaptionDialog = true
        }
    }

    // Cargar fotos al entrar
    LaunchedEffect(teamId, eventId) {
        viewModel.loadPhotos(teamId, eventId)
    }

    // Limpiar mensajes tras mostrarlos
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(eventName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { imagePicker.launch() },
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Subir foto")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
