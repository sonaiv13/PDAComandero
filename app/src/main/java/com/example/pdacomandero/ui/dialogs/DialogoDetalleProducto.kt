package com.example.pdacomandero.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.DialogoDetalleProductoBinding
import com.example.pdacomandero.models.Producto
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DialogoDetalleProducto : DialogFragment() {

    private lateinit var binding: DialogoDetalleProductoBinding
    private lateinit var contexto: Context
    private lateinit var database: FirebaseDatabase
    private lateinit var nombre: TextView
    private lateinit var precio: TextView

    private var producto: Producto? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.contexto = context
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogoDetalleProductoBinding.inflate(LayoutInflater.from(contexto))
        val vista: View = binding.root

        nombre = vista.findViewById(R.id.nombreProducto)
        precio = vista.findViewById(R.id.precioProducto)

        producto?.let {
            nombre.text = it.nombre
            precio.text = it.precio.toString() + "€"
        } ?: run {
            Snackbar.make(binding.root, "Producto no encontrado", Snackbar.LENGTH_SHORT).show()
        }

        return AlertDialog.Builder(requireContext())
            .setView(vista)
            .create()
    }

    fun setProducto(producto: Producto) {
        this.producto = producto
    }

}