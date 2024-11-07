package com.example.pdacomandero.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pdacomandero.R
import com.example.pdacomandero.models.Mesa

class MesasAdapter(var lista: ArrayList<Mesa>, var contexto: Context) : RecyclerView.Adapter<MesasAdapter.MyHolder>(){

    class MyHolder(item: View) : RecyclerView.ViewHolder(item){

        var numMesa: TextView

        init {
            numMesa = item.findViewById(R.id.numMesa)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val vista : View = LayoutInflater.from(contexto).inflate(R.layout.recycler_mesas, parent, false)
        return MyHolder(vista)
    }

    override fun getItemCount(): Int {
        return lista.size;
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val mesa = lista[position]
        Log.d("Debug", "Mostrando mesa: ${mesa.numero}")
        holder.numMesa.text = mesa.numero.toString()

    }

    fun addMesa(mesa: Mesa){
        this.lista.add(mesa)
        notifyItemInserted(lista.size-1)
    }

}