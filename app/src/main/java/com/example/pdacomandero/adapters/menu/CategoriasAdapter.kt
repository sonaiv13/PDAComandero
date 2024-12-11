package com.example.pdacomandero.adapters.menu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pdacomandero.R

class CategoriasAdapter(var lista: ArrayList<String>, val context: Context, private val listener: CategoriaClickListener,
                        private var itemLayout: Int):
    RecyclerView.Adapter<CategoriasAdapter.MyHolder>() {

    class MyHolder(item: View): ViewHolder(item){
        val nombreCategoria: TextView = itemView.findViewById(R.id.nombreCategoria)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val vista: View = LayoutInflater.from(context).inflate(itemLayout, parent, false)
        return MyHolder(vista)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val categoria = lista[position]
        holder.nombreCategoria.text = categoria

        holder.itemView.setOnClickListener {
            Log.d("CategoriasAdapter", "Categoría clickeada: $categoria")
            listener.onCategoriaClick(categoria)
        }
    }

    fun actualizarCategorias(nuevaLista: List<String>){
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    interface CategoriaClickListener {
        fun onCategoriaClick(categoria: String)
    }

}