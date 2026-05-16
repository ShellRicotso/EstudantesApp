package com.example.estudantesapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.estudantesapp.model.Estudante
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    lateinit var token: String
    lateinit var listView: ListView
    lateinit var lista: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recupera o token passado pelo LoginActivity
        token = intent.getStringExtra("token")!!

        listView = findViewById(R.id.listView)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        // Carregar lista de estudantes
        carregarEstudantes()

        // Botão para adicionar estudante
        btnAdd.setOnClickListener {
            val i = Intent(this, AddStudentActivity::class.java)
            i.putExtra("token", token)
            startActivity(i)
        }
    }

    private fun carregarEstudantes() {
        RetrofitClient.api.getEstudantes("Bearer $token")
            .enqueue(object : Callback<List<Estudante>> {

                override fun onResponse(
                    call: Call<List<Estudante>>,
                    response: Response<List<Estudante>>
                ) {
                    if (response.isSuccessful) {
                        val dados = response.body()!!
                        lista = ArrayList()

                        for (e in dados) {
                            lista.add("${e.nome} - ${e.email}")
                        }

                        val adapter = ArrayAdapter(
                            this@HomeActivity,
                            android.R.layout.simple_list_item_1,
                            lista
                        )

                        listView.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<Estudante>>, t: Throwable) {
                    // Aqui você pode mostrar um Toast de erro
                }
            })
    }
}
