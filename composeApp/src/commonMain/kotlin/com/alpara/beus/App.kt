package com.alpara.beus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import beus.composeapp.generated.resources.Res
import beus.composeapp.generated.resources.ico_add
import beus.composeapp.generated.resources.ico_calendar
import beus.composeapp.generated.resources.ico_event
import beus.composeapp.generated.resources.ico_home
import beus.composeapp.generated.resources.ico_profile
import beus.composeapp.generated.resources.ico_rank
import com.alpara.beus.screens.HomeScreen
import com.alpara.beus.ui.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class BottomNavItem(
    val label: String,
    val icon: DrawableResource,
    val screen: Screen
)

@Composable
fun MainScreen(){
    var sltItem by remember { mutableStateOf(0) }
    val navItems = listOf(
        BottomNavItem("Home",  Res.drawable.ico_home, Screen.Home),
        BottomNavItem("Calendar", Res.drawable.ico_calendar, Screen.Calendar),
        BottomNavItem("Add", Res.drawable.ico_add, Screen.Add),
        BottomNavItem("Rank", Res.drawable.ico_rank, Screen.Rank),
        BottomNavItem("Profile", Res.drawable.ico_profile, Screen.Profile)
        /*BottomNavItem("Bet", Res.drawable.ico_rank, Screen.Bet),
        BottomNavItem("Event", Res.drawable.ico_event, Screen.Event)*/
    )

    Scaffold(
        bottomBar = {
            Box (
                modifier = Modifier
                    .padding(bottom = 32.dp , start = 16.dp, end = 16.dp, top = 16.dp )
                    .shadow(8.dp, RoundedCornerShape(55.dp))
            ) {
                NavigationBar (
                    windowInsets = WindowInsets(0),
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(painter = painterResource(item.icon), contentDescription = item.label) },
                            label = { Text(item.label)},
                            selected = sltItem == index,
                            onClick = {sltItem = index},
                            )
                        }
                    }
                }
            }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ){
            when (sltItem)
            {
                0 -> HomeScreen()
                /*1 -> CalendarScreen()
                2 -> AddScreen()
                3 -> RankingScreen()
                4 -> ProfileScreen()
                5 -> BetScreen()
                6 -> EventScreen()*/
            }
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}