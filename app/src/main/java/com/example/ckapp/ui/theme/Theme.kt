package com.example.ckapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4CAF50),
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22)
)

@Composable
fun IoTTheme(content:@Composable ()->Unit){
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}