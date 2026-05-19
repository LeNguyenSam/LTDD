package com.example.ckapp.network

import com.example.ckapp.model.SensorData
import com.example.ckapp.ui.UserAccount
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val user: UserAccount? = null,
    val error: String? = null
)

interface ApiService {

    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("/api/sensors/latest")
    suspend fun getLatestSensor(): SensorData

    @GET("/api/sensors/history")
    suspend fun getHistory(): List<SensorData>
}