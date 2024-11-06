package com.example.pdacomandero.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.google.firebase.auth.FirebaseAuth

class DialogoSalir : DialogFragment() {

    private lateinit var contexto : Context
    private lateinit var vista: View
    private lateinit var botonPositivo: Button
    private lateinit var botonNegativo: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.contexto = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(contexto)
        vista = LayoutInflater.from(contexto).inflate(R.layout.dialogo_salir, null)
        builder.setView(vista)

        val dialog = builder.create()

        //Configuración botones
        botonPositivo = vista.findViewById(R.id.botonPositivo)
        botonNegativo = vista.findViewById(R.id.botonNegativo)

        botonPositivo.setOnClickListener {
            cerrarSesion()
            dismiss()
        }
        botonNegativo.setOnClickListener {
            dismiss()
        }

        return dialog
    }
    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("email").apply()
        findNavController().navigate(R.id.action_global_to_LoginFragment)
    }
}