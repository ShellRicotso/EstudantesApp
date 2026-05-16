package com.example.estudantesapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.estudantesapp.MainActivity
import com.example.estudantesapp.R
import com.example.estudantesapp.RetrofitClient
import com.example.estudantesapp.model.LoginRequest
import com.example.estudantesapp.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val senha = findViewById<EditText>(R.id.senha)
        val btn = findViewById<Button>(R.id.btnLogin)

        btn.setOnClickListener {

            val request = LoginRequest(
                email.text.toString(),
                senha.text.toString()
            )

            RetrofitClient.api.login(request)
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {

                            val token = response.body()?.token

                            Toast.makeText(
                                this@LoginActivity,
                                "Login OK",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("token", token)
                            startActivity(intent)

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Erro login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Erro: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}