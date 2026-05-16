package com.example.estudantesapp

import com.example.estudantesapp.model.LoginRequest
import com.example.estudantesapp.model.LoginResponse
import retrofit2.Call
import retrofit2.http.*
interface ApiService {
    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}