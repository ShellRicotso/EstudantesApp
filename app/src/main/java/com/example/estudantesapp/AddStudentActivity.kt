package com.example.estudantesapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.estudantesapp.model.Estudante
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStudentActivity : AppCompatActivity() {

    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        token = intent.getStringExtra("token")!!

        val nome = findViewById<EditText>(R.id.nome)
        val email = findViewById<EditText>(R.id.email)
        val contacto = findViewById<EditText>(R.id.contacto)

        findViewById<Button>(R.id.btnSalvar).setOnClickListener {

            val estudante = Estudante(
                nome = nome.text.toString(),
                data_nascimento = "2000-01-01",
                genero = "M",
                numero_estudante = System.currentTimeMillis().toString(),
                ano_ingresso = 2024,
                email = email.text.toString(),
                contacto = contacto.text.toString(),
                observacoes = ""
            )

            RetrofitClient.api.criarEstudante("Bearer $token", estudante)
                .enqueue(object : Callback<Void> {

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(this@AddStudentActivity, "Criado!", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
        }
    }
}