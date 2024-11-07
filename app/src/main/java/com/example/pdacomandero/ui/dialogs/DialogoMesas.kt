package com.example.pdacomandero.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdacomandero.R
import com.example.pdacomandero.adapters.MesasAdapter
import com.example.pdacomandero.databinding.DialogoMesasBinding
import com.example.pdacomandero.models.Mesa
import com.example.pdacomandero.models.Pedido
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DialogoMesas : DialogFragment() {

    private lateinit var contexto : Context
    private lateinit var vista: View
    private lateinit var binding: DialogoMesasBinding
    private lateinit var mesasAdapter: MesasAdapter
    private lateinit var listaMesas: ArrayList<Mesa>
    private lateinit var numMesa: TextView
    private lateinit var recyclerMesas: RecyclerView
    private lateinit var database: FirebaseDatabase

    private var mesa: Mesa? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.contexto = context
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
        listaMesas = ArrayList()
        mesasAdapter = MesasAdapter(listaMesas, context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogoMesasBinding.inflate(LayoutInflater.from(contexto))
        vista = binding.root

        recyclerMesas = binding.recyclerMesas

        recyclerMesas.layoutManager = LinearLayoutManager(contexto)
        recyclerMesas.adapter = mesasAdapter

        rellenarRecyclerMesa()

        return AlertDialog.Builder(requireContext()).setView(vista).create()

    }

    private fun rellenarRecyclerMesa() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId == null) {
            Snackbar.make(binding.root, "Error: Usuario no autenticado", Snackbar.LENGTH_SHORT).show()
            return
        }

        val databaseRef = database.getReference("usuarios").child(userId).child("mesas")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMesas.clear()
                Log.d("Debug", "Snapshot exists: ${snapshot.exists()}")

                if(snapshot.exists()){
                    snapshot.children.forEach{ mesaSnapshot ->
                        Log.d("Debug", "Mesa snapshot: $mesaSnapshot")

                        val idMesa = mesaSnapshot.child("id").getValue(Int::class.java)
                        val numMesa = mesaSnapshot.child("numero").getValue(Int::class.java)
                        if(idMesa != null && numMesa != null){
                            listaMesas.add(Mesa(idMesa, numMesa))
                        } else {
                            Log.d("Debug", "Error: mesa data is null or missing")
                        }
                    }
                    mesasAdapter.notifyDataSetChanged()
                } else {
                    Snackbar.make(binding.root, "No hay mesas disponibles", Snackbar.LENGTH_SHORT).show()
                    Log.d("Debug", "Snapshot no contiene mesas.")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root, "Algo ha fallado con la conexión a internet", Snackbar.LENGTH_SHORT).show()
            }

        })

    }
}