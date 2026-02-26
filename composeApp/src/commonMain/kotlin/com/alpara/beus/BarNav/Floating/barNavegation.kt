package com.alpara.beus.BarNav.Floating

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.alpara.beus.BarNav.BottomNavItem
import com.alpara.beus.Screens.Screen
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_add
import com.alpara.beus.resources.ico_calendar
import com.alpara.beus.resources.ico_home
import com.alpara.beus.resources.ico_profile
import com.alpara.beus.resources.ico_rank
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun FloatingBottomNavigationBar(
    currentRoute: String,
    showAddMenu: Boolean,
    onNavigate: (String) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, Res.drawable.ico_home, "Home"),
        BottomNavItem(Screen.Calendar.route, Res.drawable.ico_calendar, "Calendar"),
        BottomNavItem("add", Res.drawable.ico_add, "Add"),
        BottomNavItem(Screen.Rank.route, Res.drawable.ico_rank, "Rank"),
        BottomNavItem(Screen.Profile.route, Res.drawable.ico_profile, "Profile")
    )

    val bgRed = MaterialTheme.colorScheme.background.red
    val isDark = bgRed < 0.5f
    val accentColor  = if (isDark) Color(0xFF7C8BFF) else Color(0xFF4F5BFF)
    val accentColor2 = if (isDark) Color(0xFFB06EFF) else Color(0xFF8B5CF6)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(55.dp))
                .hazeChild(
                    state = hazeState,
                    style = HazeMaterials.regular(MaterialTheme.colorScheme.surface)
                ),
            shape = RoundedCornerShape(55.dp),
            color = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    if (item.route == "add") {
                        val addGradient = Brush.linearGradient(
                            colors = listOf(accentColor, accentColor2)
                        )
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable { onNavigate(item.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label,
                                modifier = Modifier
                                    .size(30.dp)
                                    .graphicsLayer(alpha = 0.99f)
                                    .drawWithCache {
                                        onDrawWithContent {
                                            drawContent()
                                            drawRect(addGradient, blendMode = BlendMode.SrcAtop)
                                        }
                                    },
                                tint = Color.Unspecified
                            )
                        }
                    } else {
                        NavigationBarItem(
                            icon = item.icon,
                            label = item.label,
                            selected = currentRoute == item.route,
                            onClick = { onNavigate(item.route) }
                        )
                    }
                }
            }
        }
    }
}