package com.example.ckapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SystemStatusCard(status:String){

    val color = when(status){
        "SAFE" -> Color(0xFF2ECC71)
        "WARNING" -> Color(0xFFF39C12)
        else -> Color(0xFFE74C3C)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = when(status){
                "SAFE"->"Hệ thống An toàn"
                "WARNING"->"Cảnh báo"
                else->"Nguy hiểm"
            },
            modifier = Modifier.padding(20.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun GasCard(gas:Int){

    val percent = gas/1000f

    Card(
        modifier = Modifier.fillMaxWidth()
    ){

        Column(Modifier.padding(20.dp)) {

            Text("Cảm biến Khí Gas")

            Text(
                "$gas ppm",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = percent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FlameCard(safe:Boolean){

    Card(
        modifier = Modifier.fillMaxWidth()
    ){

        Column(Modifier.padding(20.dp)) {

            Text("Cảm biến Lửa")

            Spacer(Modifier.height(8.dp))

            Text(
                if(!safe) "AN TOÀN" else "PHÁT HIỆN LỬA",
                style = MaterialTheme.typography.headlineMedium,
                color = if(!safe) Color.Green else Color.Red
            )
        }
    }
}