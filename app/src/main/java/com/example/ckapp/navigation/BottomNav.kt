package com.example.ckapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNav(
    selected:Int,
    onClick:(Int)->Unit
){

    NavigationBar {

        NavigationBarItem(
            selected = selected==0,
            onClick = { onClick(0) },
            icon = { Icon(Icons.Default.Home,null) },
            label = { Text("Trang chủ") }
        )

        NavigationBarItem(
            selected = selected==1,
            onClick = { onClick(1) },
            icon = { Icon(Icons.Default.History,null) },
            label = { Text("Lịch sử") }
        )
    }
}