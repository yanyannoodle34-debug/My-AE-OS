package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.NavTab
import com.example.ui.theme.*

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = ImmersiveSurface,
        contentColor = ImmersiveTextPrimary
    ) {
        NavTab.values().forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = getTabIcon(tab),
                        contentDescription = tab.title,
                        tint = if (selected) AetherCyan else ImmersiveTextMuted
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) AetherCyan else ImmersiveTextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = AetherIndigo.copy(alpha = 0.2f)
                )
            )
        }
    }
}

private fun getTabIcon(tab: NavTab): ImageVector {
    return when (tab) {
        NavTab.ARCHITECTURE -> Icons.Default.Hub
        NavTab.KERNEL -> Icons.Default.Memory
        NavTab.WORKERS -> Icons.Default.Engineering
        NavTab.AI_AGENTS -> Icons.Default.SmartToy
        NavTab.CLI -> Icons.Default.Terminal
        NavTab.LOGS_CONFIG -> Icons.Default.Settings
    }
}
