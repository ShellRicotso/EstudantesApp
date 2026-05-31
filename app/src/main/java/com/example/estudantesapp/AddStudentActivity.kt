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

    private lateinit var nomeEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var contactoEt: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        // Receber extras
        token = intent.getStringExtra("token") ?: ""
        id = intent.getIntExtra("id", -1)

        // Views
        nomeEt = findViewById(R.id.nome)
        emailEt = findViewById(R.id.email)
        contactoEt = findViewById(R.id.contacto)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Preencher campos se for edição
        if (id != -1) {
            nomeEt.setText(intent.getStringExtra("nome") ?: "")
            emailEt.setText(intent.getStringExtra("email") ?: "")
            contactoEt.setText(intent.getStringExtra("contacto") ?: "")
        }

        // Salvar
        btnSalvar.setOnClickListener {
            val nome = nomeEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val contacto = contactoEt.text.toString().trim()

            // Validação simples
            if (nome.isEmpty()) {
                nomeEt.error = "Nome obrigatório"
                nomeEt.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailEt.error = "Email obrigatório"
                emailEt.requestFocus()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.error = "Email inválido"
                emailEt.requestFocus()
                return@setOnClickListener
            }
            if (contacto.isEmpty()) {
                contactoEt.error = "Contacto obrigatório"
                contactoEt.requestFocus()
                return@setOnClickListener
            }

            val estudante = Estudante(
                id = if (id == -1) null else id,
                nome = nome,
                data_nascimento = "2000-01-01",
                genero = "M",
                numero_estudante = System.currentTimeMillis().toString(),
                ano_ingresso = 2024,
                email = email,
                contacto = contacto,
                observacoes = ""
            )

            // Chamada Retrofit
            if (id == -1) {
                RetrofitClient.api.criarEstudante("Bearer $token", estudante)
                    .enqueue(callback("Criado!"))
            } else {
                RetrofitClient.api.atualizar("Bearer $token", id!!, estudante)
                    .enqueue(callback("Atualizado!"))
            }
        }

        // Cancelar: fecha a Activity sem salvar
        btnCancelar.setOnClickListener {
            // Se quiser limpar campos em vez de fechar, substitua por:
            // nomeEt.text.clear(); emailEt.text.clear(); contactoEt.text.clear()
            finish()
        }
    }

    private fun callback(msg: String): Callback<Void> {
        return object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddStudentActivity, msg, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@AddStudentActivity, "Erro servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddStudentActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
