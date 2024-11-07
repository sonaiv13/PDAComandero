package com.example.pdacomandero.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.FragmentMainBinding
import com.example.pdacomandero.databinding.FragmentRegisterBinding
import com.example.pdacomandero.models.Mesa
import com.example.pdacomandero.models.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Funcionalidad de botones
        binding.btnRegistrar.setOnClickListener {
            val correo = binding.editCorreoRegistro.text.toString()
            val nombre = binding.editNombreRegistro.text.toString()
            val telefono = binding.editTelefonoRegistro.text.toString()

            if(binding.editCorreoRegistro.text.isEmpty() || binding.editPassRegistro.text.isEmpty() || binding.editPassConfirmacion.text.isEmpty()){
                Snackbar.make(view, "Por favor rellene todas las casillas.", Snackbar.LENGTH_SHORT).show()
            } else if (binding.editPassRegistro.text.toString() != binding.editPassConfirmacion.text.toString()) {
                Snackbar.make(view, "Las contraseñas no coinciden.", Snackbar.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(correo, binding.editPassRegistro.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val userId = auth.currentUser!!.uid
                            val referenciaUsuarios = database.getReference("usuarios")
                            val nuevoUsuario = Usuario(nombre, correo, telefono.toInt())

                            referenciaUsuarios.child(userId).setValue(nuevoUsuario)
                                .addOnSuccessListener {
                                    //Crea 30 mesas para el nuevo usuario
                                    val referenciaMesas = referenciaUsuarios.child(userId).child("mesas")
                                    for(i in 1..30){
                                        val mesa = Mesa(id = i, numero = i, disponible = true, pedidos = mutableListOf())
                                        referenciaMesas.child(i.toString()).setValue(mesa)
                                    }

                                    //Navega al MainFragment al completar el registro
                                    val bundle = Bundle().apply { putString("correo", correo) }
                                    findNavController().navigate(R.id.action_RegisterFragment_to_MainFragment, bundle)
                                    Snackbar.make(view, "Se ha registrado correctamente.", Snackbar.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {exception ->
                                    Snackbar.make(view, "Error al registrarse: ${exception.message}", Snackbar.LENGTH_SHORT).show()
                                }
                        }
                    }
            }
        }

        binding.btnAtras.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }
    }

    override fun onDetach() {
        super.onDetach()
    }
}