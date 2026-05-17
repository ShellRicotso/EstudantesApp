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
    var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        token = intent.getStringExtra("token")!!
        id = intent.getIntExtra("id", -1)

        val nome = findViewById<EditText>(R.id.nome)
        val email = findViewById<EditText>(R.id.email)
        val contacto = findViewById<EditText>(R.id.contacto)
        val btn = findViewById<Button>(R.id.btnSalvar)

        //  Preencher campos se for edição
        if (id != -1) {
            nome.setText(intent.getStringExtra("nome"))
            email.setText(intent.getStringExtra("email"))
            contacto.setText(intent.getStringExtra("contacto"))
        }

        btn.setOnClickListener {
            val estudante = Estudante(
                id = if (id == -1) null else id, // só coloca id se for edição
                nome = nome.text.toString(),
                data_nascimento = "2000-01-01",
                genero = "M",
                numero_estudante = System.currentTimeMillis().toString(),
                ano_ingresso = 2024,
                email = email.text.toString(),
                contacto = contacto.text.toString(),
                observacoes = ""
            )

            if (id == -1) {
                //  Criar novo estudante
                RetrofitClient.api.criarEstudante("Bearer $token", estudante)
                    .enqueue(callback("Criado!"))
            } else {
                // ️ Atualizar estudante existente
                RetrofitClient.api.atualizar("Bearer $token", id!!, estudante)
                    .enqueue(callback("Atualizado!"))
            }
        }
    }

    private fun callback(msg: String): Callback<Void> {
        return object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(this@AddStudentActivity, msg, Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddStudentActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
