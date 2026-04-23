package com.example.ckapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ckapp.navigation.BottomNav
import com.example.ckapp.ui.HomeScreen
import com.example.ckapp.ui.HistoryScreen
import com.example.ckapp.ui.theme.IoTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { IoTTheme { IoTApp() } }
    }
}

@Composable
fun IoTApp() {
    var selected by remember { mutableStateOf(0) }
    Scaffold(
        containerColor = Color(0xFF0A0E17),
        bottomBar      = { BottomNav(selected) { selected = it } }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding), color = Color(0xFF0A0E17)) {
            when (selected) {
                0 -> HomeScreen()
                1 -> HistoryScreen()
            }
        }
    }
}