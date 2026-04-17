package com.example.ckapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*

@Composable
fun HomeScreen(){

    var gas by remember { mutableStateOf(166) }
    var flame by remember { mutableStateOf(false) }

    val status = when{
        gas < 300 -> "SAFE"
        gas < 600 -> "WARNING"
        else -> "DANGER"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){

        Text(
            "IoT Monitoring",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(20.dp))

        SystemStatusCard(status)

        Spacer(Modifier.height(16.dp))

        GasCard(gas)

        Spacer(Modifier.height(16.dp))

        FlameCard(flame)
    }
}