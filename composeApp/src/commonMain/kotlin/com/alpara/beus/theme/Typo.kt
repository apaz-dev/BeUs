package com.alpara.beus.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.inter_bold
import com.alpara.beus.resources.inter_medium
import com.alpara.beus.resources.poppins_medium
import org.jetbrains.compose.resources.Font

object AppTypography {
    @Composable
    fun heading() = TextStyle(
        fontSize = 60.sp,
        fontFamily = FontFamily(Font(Res.font.poppins_medium, FontWeight.Bold)),
        color = Color.Black
    )

    @Composable
    fun body() = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.inter_medium, FontWeight.Normal)),
        color = Color.Black
    )
}