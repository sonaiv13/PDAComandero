package com.example.pdacomandero.adapters.inicio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pdacomandero.R
import com.example.pdacomandero.models.Mesa
import com.example.pdacomandero.models.Pedido
import com.example.pdacomandero.models.Producto

class PedidosAdapter(var lista: ArrayList<Producto>, val context: Context):
    RecyclerView.Adapter<PedidosAdapter.MyHolder>() {

    class MyHolder(item: View): ViewHolder(item){
        val cantidadProducto: TextView = itemView.findViewById(R.id.cantidadProducto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombreProducto)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val vista: View = LayoutInflater.from(context).inflate(R.layout.recycler_pedido, parent, false)
        return MyHolder(vista)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val producto = lista[position]
        holder.cantidadProducto.text = producto.cantidad.toString()
        holder.nombreProducto.text = producto.nombre

    }

    fun actualizarPedido(nuevaLista: ArrayList<Producto>) {
        if(lista != nuevaLista){
            lista.clear()
            lista.addAll(nuevaLista)
        }
        notifyDataSetChanged()
    }

    fun agregarProducto(producto: Producto){
        lista.add(producto)
        notifyItemInserted(lista.size - 1)
    }


}