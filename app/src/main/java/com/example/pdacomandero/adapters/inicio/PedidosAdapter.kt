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
            if (producto.cantidad > 1) {
                producto.cantidad--
                holder.cantidadProducto.text = producto.cantidad.toString()
                actualizarProductoEnFirebase(producto)
            }
        }
        holder.btnAgregar.setOnClickListener{
            producto.cantidad++
            holder.cantidadProducto.text = producto.cantidad.toString()
            actualizarProductoEnFirebase(producto)
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
    }

    private fun actualizarProductoEnFirebase(producto: Producto) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val mesaSeleccionada = (context as? MesasFragment)?.mesaSeleccionada
            if (mesaSeleccionada != null) {
                val mesaRef = FirebaseDatabase.getInstance()
                    .getReference("usuarios/$userId/mesas/$mesaSeleccionada")

                mesaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mesa = snapshot.getValue(Mesa::class.java)
                        if (mesa != null) {
                            val pedidoActual = mesa.pedidos.find { it.numMesa == mesaSeleccionada }
                            if (pedidoActual != null) {
                                val productoIndex = pedidoActual.productos.indexOfFirst { it.id == producto.id }
                                if (productoIndex >= 0) {
                                    // Actualiza la cantidad del producto
                                    pedidoActual.productos[productoIndex].cantidad = producto.cantidad
                                    mesaRef.setValue(mesa).addOnSuccessListener {
                                        Log.d("PedidosAdapter", "Producto actualizado en Firebase.")
                                    }.addOnFailureListener {
                                        Log.e("PedidosAdapter", "Error al actualizar Firebase: ${it.message}")
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("PedidosAdapter", "Error al leer Firebase: ${error.message}")
                    }
                })
            }
        }
    }
    

}