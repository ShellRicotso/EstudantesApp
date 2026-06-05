package com.example.estudantesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.estudantesapp.model.Estudante
import com.example.estudantesapp.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        token = intent.getStringExtra("token") ?: ""
        if (token.isEmpty()) {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = StudentAdapter(emptyList(), { estudante ->
            abrirEdicao(estudante)
        }, { view, estudante ->
            mostrarMenuOpcoes(view, estudante)
        })
        
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAdd).setOnClickListener {
            val i = Intent(this, AddStudentActivity::class.java)
            i.putExtra("token", token)
            startActivity(i)
        }

        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            confirmarLogout()
        }

        carregar()
    }

    private fun mostrarMenuOpcoes(view: View, estudante: Estudante) {
        val popup = PopupMenu(this, view)
        popup.menu.add("Editar")
        popup.menu.add("Excluir")
        
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Editar" -> abrirEdicao(estudante)
                "Excluir" -> confirmarExclusao(estudante)
            }
            true
        }
        popup.show()
    }

    private fun abrirEdicao(estudante: Estudante) {
        val i = Intent(this, AddStudentActivity::class.java)
        i.putExtra("token", token)
        i.putExtra("estudante", estudante)
        startActivity(i)
    }

    private fun confirmarExclusao(estudante: Estudante) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Estudante")
            .setMessage("Tem certeza que deseja remover ${estudante.nome}?")
            .setPositiveButton("Sim") { _, _ -> deletar(estudante) }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun confirmarLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja encerrar a sessão?")
            .setPositiveButton("Sair") { _, _ ->
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        carregar()
    }

    private fun carregar() {
        RetrofitClient.api.getEstudantes("Bearer $token")
            .enqueue(object : Callback<List<Estudante>> {
                override fun onResponse(call: Call<List<Estudante>>, response: Response<List<Estudante>>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            adapter.updateList(it)
                        }
                    } else if (response.code() == 401) {
                        finish()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    }
                }
                override fun onFailure(call: Call<List<Estudante>>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deletar(estudante: Estudante) {
        RetrofitClient.api.deletar("Bearer $token", estudante.id!!)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@HomeActivity, "Removido com sucesso", Toast.LENGTH_SHORT).show()
                        carregar()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "Erro ao remover", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
