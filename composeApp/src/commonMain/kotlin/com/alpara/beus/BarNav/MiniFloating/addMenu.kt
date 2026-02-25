package com.alpara.beus.BarNav.MiniFloating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alpara.beus.BarNav.Floating.MenuButton
import com.alpara.beus.resources.Res
import com.alpara.beus.resources.ico_bet
import com.alpara.beus.resources.ico_event

@Composable
fun AddFloatingMenu(
    onEventClick: () -> Unit,
    onBetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(bottom = 80.dp)
            .clip(RoundedCornerShape(20.dp))
            //.padding(8.dp),
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuButton(
            icon = Res.drawable.ico_event,
            label = "Event",
            onClick = onEventClick
        )
        MenuButton(
            icon = Res.drawable.ico_bet,
            label = "Bet",
            onClick = onBetClick
        )
    }
}
