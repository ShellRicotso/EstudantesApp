package com.example.estudantesapp

import com.example.estudantesapp.model.Estudante
import com.example.estudantesapp.model.LoginRequest
import com.example.estudantesapp.model.LoginResponse
import com.example.estudantesapp.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<Void>

    @GET("/estudantes")
    fun getEstudantes(
        @Header("Authorization") token: String
    ): Call<List<Estudante>>

    @POST("/estudantes")
    fun criarEstudante(
        @Header("Authorization") token: String,
        @Body estudante: Estudante
    ): Call<Void>

    @PUT("/estudantes/{id}")
    fun atualizar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body estudante: Estudante
    ): Call<Void>

    @DELETE("/estudantes/{id}")
    fun deletar(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}
