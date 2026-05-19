package com.example.ckapp.model

data class SensorData(
    val gas:         Int,
    val flame:       Boolean,
    val temperature: Double,
    val humidity:    Double,
    val pressure:    Double,   // hPa
    val light:       Int,      // lux
    val uvIndex:     Double,
    val time:        String
)