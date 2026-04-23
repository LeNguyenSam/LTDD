package com.example.ckapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNav(selected: Int, onClick: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF111827),
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick  = { onClick(0) },
            icon     = { Icon(Icons.Default.Home, contentDescription = null) },
            label    = { Text("Trang chủ") },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = Color(0xFF00D4FF),
                selectedTextColor   = Color(0xFF00D4FF),
                indicatorColor      = Color(0xFF00D4FF).copy(alpha = 0.12f),
                unselectedIconColor = Color(0xFF5A6A80),
                unselectedTextColor = Color(0xFF5A6A80)
            )
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick  = { onClick(1) },
            icon     = { Icon(Icons.Default.History, contentDescription = null) },
            label    = { Text("Lịch sử") },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = Color(0xFF00D4FF),
                selectedTextColor   = Color(0xFF00D4FF),
                indicatorColor      = Color(0xFF00D4FF).copy(alpha = 0.12f),
                unselectedIconColor = Color(0xFF5A6A80),
                unselectedTextColor = Color(0xFF5A6A80)
            )
        )
    }
}