package com.alpara.beus.Utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray?) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            onImagePicked(null)
            return@rememberLauncherForActivityResult
        }
        val bytes = context.readUriAsBytes(uri)
        onImagePicked(bytes)
    }

    return ImagePickerLauncher { launcher.launch("image/*") }
}

private fun Context.readUriAsBytes(uri: Uri): ByteArray? {
    return try {
        contentResolver.openInputStream(uri)?.use { it.readBytes() }
    } catch (e: Exception) {
        null
    }
}

actual class ImagePickerLauncher(private val launchFn: () -> Unit) {
    actual fun launch() = launchFn()
}
