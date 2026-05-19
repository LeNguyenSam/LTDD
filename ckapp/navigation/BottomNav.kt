package com.example.ckapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

data class NavItem(
    val index: Int,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    NavItem(0, "Trang chủ", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem(1, "Tài khoản", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle),
    NavItem(2, "Lịch sử", Icons.Filled.History, Icons.Outlined.History)
)

// Colour tokens
private val TealActive = Color(0xFF00D4AA)
private val InactiveTint = Color(0xFF5A6A7E)
private val BarBackground = Color(0xFF0D1520)
private val IndicatorColor = Color(0xFF00D4AA22)

@Composable
fun BottomNav(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar(
        containerColor = BarBackground,
        contentColor = TealActive,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            val isSelected = item.index == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(item.index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealActive,
                    selectedTextColor = TealActive,
                    unselectedIconColor = InactiveTint,
                    unselectedTextColor = InactiveTint,
                    indicatorColor = IndicatorColor
                )
            )
        }
    }
}