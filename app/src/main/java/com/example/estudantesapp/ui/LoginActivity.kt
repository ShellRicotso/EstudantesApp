package com.example.estudantesapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.estudantesapp.HomeActivity
import com.example.estudantesapp.R
import com.example.estudantesapp.RetrofitClient
import com.example.estudantesapp.model.LoginRequest
import com.example.estudantesapp.model.LoginResponse
import com.example.estudantesapp.model.RegisterRequest
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etNome = findViewById<EditText>(R.id.etNome)
        val tilNome = findViewById<TextInputLayout>(R.id.tilNome)
        val etEmail = findViewById<EditText>(R.id.email)
        val etSenha = findViewById<EditText>(R.id.senha)
        val btnAction = findViewById<Button>(R.id.btnLogin)
        val tvToggle = findViewById<TextView>(R.id.btnToggleRegister)
        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitle)

        tvToggle.setOnClickListener {
            isLoginMode = !isLoginMode
            if (isLoginMode) {
                tilNome.visibility = View.GONE
                tvSubtitle.text = "Faça login para continuar"
                btnAction.text = "Entrar"
                tvToggle.text = "Não tem uma conta? Cadastre-se"
            } else {
                tilNome.visibility = View.VISIBLE
                tvSubtitle.text = "Crie sua conta agora"
                btnAction.text = "Cadastrar"
                tvToggle.text = "Já tem uma conta? Faça login"
            }
        }

        btnAction.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()
            val nome = etNome.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty() || (!isLoginMode && nome.isEmpty())) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) {
                realizarLogin(email, senha)
            } else {
                realizarRegistro(nome, email, senha)
            }
        }
    }

    private fun realizarLogin(email: String, senha: String) {
        val request = LoginRequest(email, senha)
        RetrofitClient.api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Erro: Credenciais inválidas", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun realizarRegistro(nome: String, email: String, senha: String) {
        val request = RegisterRequest(nome, email, senha)
        RetrofitClient.api.register(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Registro realizado! Faça login.", Toast.LENGTH_LONG).show()
                    // Muda para modo login automaticamente
                    findViewById<TextView>(R.id.btnToggleRegister).performClick()
                } else {
                    Toast.makeText(this@LoginActivity, "Erro ao registrar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
