package com.example.ckapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ckapp.model.SensorData

@Composable
fun HistoryScreen(){

    val history = listOf(
        SensorData(58,false,21.0,"9:05"),
        SensorData(56,false,25.1,"9:10"),
        SensorData(443,false,31.5,"9:15"),
        SensorData(690,true,35.0,"9:20")
    )

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ){

        item {
            Text(
                "Lịch sử",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(16.dp))
        }

        items(history){

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ){

                Column(Modifier.padding(16.dp)) {

                    Text("Gas: ${it.gas} ppm")
                    Text("Nhiệt độ: ${it.temperature}°C")
                    Text("Lửa: ${if(it.flame)"Có" else "Không"}")
                    Text("Thời gian: ${it.time}")
                }
            }
        }
    }
}