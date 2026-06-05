package com.example.estudantesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estudantesapp.model.Estudante

class StudentAdapter(
    private var list: List<Estudante>,
    private val onClick: (Estudante) -> Unit,
    private val onMenuClick: (View, Estudante) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNome)
        val email: TextView = view.findViewById(R.id.tvEmail)
        val menu: ImageView = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estudante, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nome.text = item.nome
        holder.email.text = item.email
        
        holder.itemView.setOnClickListener { onClick(item) }
        holder.menu.setOnClickListener { onMenuClick(it, item) }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<Estudante>) {
        list = newList
        notifyDataSetChanged()
    }
}
