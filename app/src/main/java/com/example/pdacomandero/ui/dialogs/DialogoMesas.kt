package com.example.pdacomandero.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdacomandero.R
import com.example.pdacomandero.adapters.MesasAdapter
import com.example.pdacomandero.models.Mesa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DialogoMesas : DialogFragment(), MesasAdapter.OnRecyclerMesasListener {

    private lateinit var contexto : Context
    private lateinit var vista: View
    private lateinit var database: FirebaseDatabase
    private lateinit var mesasAdapter: MesasAdapter
    private var listaMesas = ArrayList<Mesa>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.contexto = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(contexto)
        vista = LayoutInflater.from(contexto).inflate(R.layout.dialogo_mesas, null)
        builder.setView(vista)

        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")

        val recyclerView = vista.findViewById<RecyclerView>(R.id.recyclerMesas)
        recyclerView.layoutManager = LinearLayoutManager(contexto)
        mesasAdapter = MesasAdapter(listaMesas, contexto, this)
        recyclerView.adapter = mesasAdapter

        cargarMesasDatabase()

        return builder.create()
    }

    private fun cargarMesasDatabase(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId != null){
            val referenciaMesas = database.getReference("usuarios").child(userId).child("mesas")

            referenciaMesas.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nuevaListaMesas = ArrayList<Mesa>()
                    for (mesaSnapshot in snapshot.children){
                        val mesa = mesaSnapshot.getValue(Mesa::class.java)
                        if(mesa != null){
                            nuevaListaMesas.add(mesa)
                        }
                    }
                    mesasAdapter.actualizarMesas(nuevaListaMesas)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("DialogoMesas", "Error al cargar mesas: ${error.message}")
                }

            })
        }
    }

    override fun onMesaSelected(mesa: Mesa) {
        findNavController().navigate(R.id.action_nav_inicio_to_mesasFragment)
    }

}