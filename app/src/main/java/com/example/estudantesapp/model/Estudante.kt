package com.example.estudantesapp.model

data class Estudante(
    val id: Int? = null,
    val nome: String,
    val data_nascimento: String,
    val genero: String,
    val numero_estudante: String,
    val ano_ingresso: Int,
    val email: String,
    val contacto: String,
    val observacoes: String
)