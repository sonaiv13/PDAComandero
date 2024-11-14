package com.example.pdacomandero.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pdacomandero.R
import com.example.pdacomandero.models.Mesa

class MesasAdapter(var lista: ArrayList<Mesa>, val context: Context, var listener: OnRecyclerMesasListener):
    RecyclerView.Adapter<MesasAdapter.MyHolder>() {

    class MyHolder(item: View): ViewHolder(item){
        val numero: TextView = itemView.findViewById(R.id.numMesa)
        val indicador: View = itemView.findViewById(R.id.indicador)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val vista: View = LayoutInflater.from(context).inflate(R.layout.recycler_mesas, parent, false)
        return MyHolder(vista)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val mesa = lista[position]
        holder.numero.text = mesa.numero.toString()

        if (mesa.disponible){
            holder.indicador.setBackgroundResource(R.drawable.circle_green)
        } else {
            holder.indicador.setBackgroundResource(R.drawable.circle_red)
        }

        holder.itemView.setOnClickListener {
            listener.onMesaSelected(mesa)
        }
    }

    fun actualizarMesas(nuevaLista: ArrayList<Mesa>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    interface OnRecyclerMesasListener{
        fun onMesaSelected(mesa: Mesa)
    }
}