package com.example.pdacomandero.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.FragmentLoginBinding
import com.example.pdacomandero.models.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
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

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMostrarPass.setOnCheckedChangeListener {_, isChecked ->
            if(isChecked){
                binding.editPass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.editPass.setSelection(binding.editPass.text.length)
        }

        binding.btnEntrar.setOnClickListener {
            if(binding.editCorreo.text.isEmpty() || binding.editPass.text.isEmpty()){
                Snackbar.make(view, "Por favor, rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            } else {
                var usuarioEncontrado = false
                val referencia = database.getReference("usuarios")

                //Mostrar indicador progreso
                binding.progressBar.visibility = View.VISIBLE

                referencia.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {usuario ->
                            val usuario = usuario.getValue(Usuario::class.java)
                            if(usuario!!.correo.toString() == binding.editCorreo.text.toString()){
                                usuarioEncontrado = true
                                auth.signInWithEmailAndPassword(binding.editCorreo.text.toString(), binding.editPass.text.toString())
                                    .addOnCompleteListener {
                                        //Ocultar el indicador
                                        binding.progressBar.visibility = View.GONE

                                        if(it.isSuccessful){
                                            val bundle = Bundle()
                                            bundle.putString("nombre", usuario.nombre)
                                        }
                                    }
                            }
                        }
                        if(!usuarioEncontrado){
                            //Ocultar el indicador
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(view, "No estas registrado", Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }

        binding.btnRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }

    override fun onDetach() {
        super.onDetach()
    }
}