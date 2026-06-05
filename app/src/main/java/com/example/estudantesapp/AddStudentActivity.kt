package com.example.estudantesapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.estudantesapp.model.Estudante
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddStudentActivity : AppCompatActivity() {

    private lateinit var token: String
    private var estudanteEdit: Estudante? = null

    private lateinit var etNome: EditText
    private lateinit var etDataNasc: EditText
    private lateinit var actvGenero: AutoCompleteTextView
    private lateinit var etNumeroEstudante: EditText
    private lateinit var etAnoIngresso: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContacto: EditText
    private lateinit var etObservacoes: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        val toolbar = findViewById<Toolbar>(R.id.toolbarAdd)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        token = intent.getStringExtra("token") ?: ""
        estudanteEdit = intent.getSerializableExtra("estudante") as? Estudante

        bindViews()
        setupGeneroDropdown()
        setupDatePicker()

        if (estudanteEdit != null) {
            preencherCampos(estudanteEdit!!)
            btnSalvar.text = "Atualizar Estudante"
            supportActionBar?.title = "Editar Estudante"
        }

        btnSalvar.setOnClickListener { salvar() }
        btnCancelar.setOnClickListener { finish() }
    }

    private fun bindViews() {
        etNome = findViewById(R.id.etNome)
        etDataNasc = findViewById(R.id.etDataNasc)
        actvGenero = findViewById(R.id.actvGenero)
        etNumeroEstudante = findViewById(R.id.etNumeroEstudante)
        etAnoIngresso = findViewById(R.id.etAnoIngresso)
        etEmail = findViewById(R.id.etEmail)
        etContacto = findViewById(R.id.etContacto)
        etObservacoes = findViewById(R.id.etObservacoes)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)
    }

    private fun setupGeneroDropdown() {
        val generos = arrayOf("Masculino", "Feminino", "Outro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, generos)
        actvGenero.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        etDataNasc.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etDataNasc.setText(date)
            }, year, month, day)
            datePicker.show()
        }
    }

    private fun preencherCampos(e: Estudante) {
        etNome.setText(e.nome)
        etDataNasc.setText(e.data_nascimento)
        actvGenero.setText(e.genero, false)
        etNumeroEstudante.setText(e.numero_estudante)
        etAnoIngresso.setText(e.ano_ingresso.toString())
        etEmail.setText(e.email)
        etContacto.setText(e.contacto)
        etObservacoes.setText(e.observacoes)
    }

    private fun salvar() {
        val nome = etNome.text.toString().trim()
        val dataNasc = etDataNasc.text.toString().trim()
        val genero = actvGenero.text.toString().trim()
        val numero = etNumeroEstudante.text.toString().trim()
        val anoIngresso = etAnoIngresso.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val contacto = etContacto.text.toString().trim()
        val obs = etObservacoes.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || numero.isEmpty()) {
            Toast.makeText(this, "Nome, Email e Número são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val estudante = Estudante(
            id = estudanteEdit?.id,
            nome = nome,
            data_nascimento = dataNasc,
            genero = if (genero == "Masculino") "M" else if (genero == "Feminino") "F" else "O",
            numero_estudante = numero,
            ano_ingresso = anoIngresso.toIntOrNull() ?: 2024,
            email = email,
            contacto = contacto,
            observacoes = obs
        )

        val call = if (estudante.id == null) {
            RetrofitClient.api.criarEstudante("Bearer $token", estudante)
        } else {
            RetrofitClient.api.atualizar("Bearer $token", estudante.id, estudante)
        }

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddStudentActivity, "Sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddStudentActivity, "Erro ao salvar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddStudentActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
