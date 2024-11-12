package com.example.pdacomandero.ui.fragments.inicio

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.adapters.MesasAdapter
import com.example.pdacomandero.databinding.FragmentInicioBinding
import com.example.pdacomandero.databinding.FragmentMainBinding
import com.example.pdacomandero.databinding.FragmentMesasBinding
import com.example.pdacomandero.models.Mesa
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MesasFragment : Fragment() {

    private lateinit var binding: FragmentMesasBinding
    private lateinit var database: FirebaseDatabase
    private var mesaSeleccionada: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
        mesaSeleccionada = arguments?.getInt("mesaSeleccionada")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMesasBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textMesaSeleccionada.text = "Mesa ${mesaSeleccionada ?: "No seleccionada"}"

        if (mesaSeleccionada != null){
            cargarDatosMesa(mesaSeleccionada!!)
        }

    }

    private fun cargarDatosMesa(numeroMesa: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if(userId != null){
            val referenciaMesa = database.getReference("usuarios").child(userId).child("mesas").child(numeroMesa.toString())

            referenciaMesa.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mesa = snapshot.getValue(Mesa::class.java)
                    if(mesa != null){
                        mostrarDatosMesa(mesa)
                    } else {
                        binding.textMesaSeleccionada.text = "Mesa $numeroMesa no encontrada"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("MesasFragment", "Error al cargar datos de la mesa: ${error.message}")
                }

            })
        }
    }

    private fun mostrarDatosMesa(mesa: Mesa) {
        binding.textMesaSeleccionada.text = "Mesa ${mesa.numero}"
        binding.textNumMesa.text = "Número:  ${mesa.numero}"
    }

    override fun onDetach() {
        super.onDetach()
    }

}