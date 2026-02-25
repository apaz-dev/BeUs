package com.alpara.beus.Themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.inter_medium
import com.alpara.beus.resources.poppins_medium
import org.jetbrains.compose.resources.Font

object AppTypo {
    @Composable
    fun heading() = TextStyle(
        fontSize = 60.sp,
        fontFamily = FontFamily(Font(Res.font.poppins_medium, FontWeight.ExtraBold)),
        color = MaterialTheme.colorScheme.onBackground
    )

    @Composable
    fun body() = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.inter_medium, FontWeight.Normal)),
        color = MaterialTheme.colorScheme.onBackground
    )
}