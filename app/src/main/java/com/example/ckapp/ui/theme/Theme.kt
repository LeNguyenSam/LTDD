package com.example.ckapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    background       = Color(0xFF0A0E17),
    surface          = Color(0xFF111827),
    surfaceVariant   = Color(0xFF1C2333),
    primary          = Color(0xFF00D4FF),
    onPrimary        = Color(0xFF001A22),
    onBackground     = Color(0xFFF0F4FF),
    onSurface        = Color(0xFFF0F4FF),
    onSurfaceVariant = Color(0xFF5A6A80),
    outline          = Color(0xFF1E2D40)
)

@Composable
fun IoTTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}