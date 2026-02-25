package com.alpara.beus.Utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray?) -> Unit
): ImagePickerLauncher

expect class ImagePickerLauncher {
    fun launch()
}
