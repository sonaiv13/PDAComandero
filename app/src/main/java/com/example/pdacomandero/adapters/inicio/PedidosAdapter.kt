package com.example.pdacomandero.adapters.inicio

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pdacomandero.R
import com.example.pdacomandero.models.Mesa
import com.example.pdacomandero.models.Pedido
import com.example.pdacomandero.models.Producto
import com.example.pdacomandero.ui.fragments.inicio.MesasFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PedidosAdapter(var lista: ArrayList<Producto>, 
                     val context: Context,
                     var listener: PedidoClickListener):
    RecyclerView.Adapter<PedidosAdapter.MyHolder>() {

    class MyHolder(item: View): ViewHolder(item){
        var cantidadProducto: TextView = itemView.findViewById(R.id.cantidadProducto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombreProducto)
        val btnQuitar: ImageButton = itemView.findViewById(R.id.btnQuitar)
        val btnAgregar: ImageButton = itemView.findViewById(R.id.btnAgregar)
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

        holder.btnQuitar.setOnClickListener{
            listener.onQuitarCantidad(producto)
        }
        holder.btnAgregar.setOnClickListener{
            listener.onAgregarCantidad(producto)
        }

        holder.itemView.setOnClickListener{
            listener.onPedidoClick(producto)
        }
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


    interface PedidoClickListener {
        fun onPedidoClick(producto: Producto)
        fun onAgregarCantidad(producto: Producto)
        fun onQuitarCantidad(producto: Producto)
    }


}