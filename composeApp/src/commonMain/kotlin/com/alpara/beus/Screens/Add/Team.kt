package com.alpara.beus.Screens.Add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpara.beus.Screens.Auth.GlassTextField
import com.alpara.beus.Themes.AppTypo
import com.alpara.beus.Themes.textSecondary
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.cancel
import com.alpara.beus.resources.code_required
import com.alpara.beus.resources.create
import com.alpara.beus.resources.join
import com.alpara.beus.resources.manage_team
import com.alpara.beus.resources.min_3_chars
import com.alpara.beus.resources.min_6_chars
import com.alpara.beus.resources.name_required
import com.alpara.beus.resources.team_code_label
import com.alpara.beus.resources.team_create_hint
import com.alpara.beus.resources.team_join_hint
import com.alpara.beus.resources.team_name_label
import com.alpara.beus.resources.team_tab_create
import com.alpara.beus.resources.team_tab_join
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun TeamModalPreview() {
    TeamModal(showDialog = true, onDismiss = {}, onCreateTeam = {}, onJoinTeam = {})
}

@Composable
fun TeamModal(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCreateTeam: (String) -> Unit,
    onJoinTeam: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var teamName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // ── Glassmorphism palette ──────────────────────────────────────────────
    val bgRed       = MaterialTheme.colorScheme.background.red
    val isDark      = bgRed < 0.5f
    val accentColor = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2= if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)
    val glassBase   = if (isDark) Color(0xFF1C1E26) else Color(0xFFFFFFFF)
    val borderGlass = if (isDark) Color(0x44FFFFFF) else Color(0x55FFFFFF)
    val onSurface   = MaterialTheme.colorScheme.onSurface

    fun dismiss() {
        teamName = ""; joinCode = ""; errorMessage = ""; selectedTab = 0
        onDismiss()
    }

    if (!showDialog) return

    AlertDialog(
        onDismissRequest = { dismiss() },
        containerColor = glassBase,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Título con gradiente
                Text(
                    text = stringResource(Res.string.manage_team),
                    style = AppTypo.heading().copy(
                        brush = Brush.horizontalGradient(colors = listOf(accentColor, accentColor2))
                    ),
                    fontSize = 22.sp
                )

                // ── Selector de pestaña glass ──────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.07f))
                        .border(1.dp, borderGlass, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(stringResource(Res.string.team_tab_create), stringResource(Res.string.team_tab_join)).forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    if (isSelected)
                                        Brush.linearGradient(colors = listOf(accentColor, accentColor2))
                                    else
                                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable { selectedTab = index; errorMessage = "" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = AppTypo.body().copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    // ── Tab: Crear equipo ──────────────────────────────────
                    0 -> {
                        GlassSectionLabel(stringResource(Res.string.team_name_label), accentColor)
                        GlassTextField(
                            value = teamName,
                            onValueChange = { teamName = it; errorMessage = "" },
                            placeholder = "Ej: Los Invencibles",
                            accentColor = if (errorMessage.isNotEmpty()) Color(0xFFFF6B6B) else accentColor,
                            borderGlass = if (errorMessage.isNotEmpty()) Color(0xFFFF6B6B).copy(alpha = 0.4f) else borderGlass,
                            glassBase = glassBase,
                            onSurface = onSurface
                        )
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFF6B6B),
                                style = AppTypo.body(),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = stringResource(Res.string.team_create_hint),
                            style = AppTypo.body(),
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                    // ── Tab: Unirse a equipo ───────────────────────────────
                    1 -> {
                        GlassSectionLabel(stringResource(Res.string.team_code_label), accentColor)
                        GlassTextField(
                            value = joinCode,
                            onValueChange = { joinCode = it.uppercase(); errorMessage = "" },
                            placeholder = "Ej: ABC123",
                            accentColor = if (errorMessage.isNotEmpty()) Color(0xFFFF6B6B) else accentColor,
                            borderGlass = if (errorMessage.isNotEmpty()) Color(0xFFFF6B6B).copy(alpha = 0.4f) else borderGlass,
                            glassBase = glassBase,
                            onSurface = onSurface
                        )
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFF6B6B),
                                style = AppTypo.body(),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = stringResource(Res.string.team_join_hint),
                            style = AppTypo.body(),
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            val strNameRequired = stringResource(Res.string.name_required)
            val strMin3 = stringResource(Res.string.min_3_chars)
            val strCodeRequired = stringResource(Res.string.code_required)
            val strMin6 = stringResource(Res.string.min_6_chars)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(colors = listOf(accentColor, accentColor2))
                    )
                    .clickable {
                        when (selectedTab) {
                            0 -> when {
                                teamName.isBlank() -> errorMessage = strNameRequired
                                teamName.length < 3 -> errorMessage = strMin3
                                else -> { onCreateTeam(teamName); dismiss() }
                            }
                            1 -> when {
                                joinCode.isBlank() -> errorMessage = strCodeRequired
                                joinCode.length < 6 -> errorMessage = strMin6
                                else -> { onJoinTeam(joinCode); dismiss() }
                            }
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) stringResource(Res.string.create) else stringResource(Res.string.join),
                    style = AppTypo.body().copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.10f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { dismiss() }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(Res.string.cancel),
                    style = AppTypo.body().copy(fontWeight = FontWeight.Medium),
                    color = accentColor,
                    fontSize = 14.sp
                )
            }
        }
    )
}

@Composable
private fun GlassSectionLabel(text: String, accentColor: Color) {
    Text(
        text = text,
        style = AppTypo.body().copy(fontWeight = FontWeight.SemiBold),
        fontSize = 13.sp,
        color = accentColor
    )
}