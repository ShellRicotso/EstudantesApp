package com.example.estudantesapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.estudantesapp.model.Estudante
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    lateinit var token: String
    lateinit var listView: ListView
    lateinit var dados: List<Estudante>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        token = intent.getStringExtra("token")!!
        listView = findViewById(R.id.listView)

        carregar()

        //  Adicionar estudante
        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val i = Intent(this, AddStudentActivity::class.java)
            i.putExtra("token", token)
            startActivity(i)
        }

        // ✏Editar estudante (clique simples)
        listView.setOnItemClickListener { _, _, position, _ ->
            val estudante = dados[position]

            val i = Intent(this, AddStudentActivity::class.java)
            i.putExtra("token", token)
            i.putExtra("id", estudante.id)
            i.putExtra("nome", estudante.nome)
            i.putExtra("email", estudante.email)
            i.putExtra("contacto", estudante.contacto)

            startActivity(i)
        }

        //  Deletar estudante (clique longo)
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val estudante = dados[position]

            RetrofitClient.api.deletar("Bearer $token", estudante.id!!)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(this@HomeActivity, "Apagado!", Toast.LENGTH_SHORT).show()
                        carregar()
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })

            true
        }
    }

    override fun onResume() {
        super.onResume()
        carregar()
    }

    private fun carregar() {
        RetrofitClient.api.getEstudantes("Bearer $token")
            .enqueue(object : Callback<List<Estudante>> {
                override fun onResponse(
                    call: Call<List<Estudante>>,
                    response: Response<List<Estudante>>
                ) {
                    if (response.isSuccessful) {
                        dados = response.body()!!

                        val lista = dados.map {
                            "${it.nome} - ${it.email}"
                        }

                        listView.adapter = ArrayAdapter(
                            this@HomeActivity,
                            android.R.layout.simple_list_item_1,
                            lista
                        )
                    }
                }
                override fun onFailure(call: Call<List<Estudante>>, t: Throwable) {}
            })
    }
}
