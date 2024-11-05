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
import com.example.pdacomandero.ui.activities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private var correoRecibido: String? = null
    private var nombreRecibido: String? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
        correoRecibido = arguments?.getString("correo")
        nombreRecibido = arguments?.getString("nombre")
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

        //Comprobar si el usuario ya había iniciado sesión
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", null)
        if(savedEmail != null){
            findNavController().navigate(R.id.action_LoginFragment_to_MainFragment)
            return
        }

        //Guardar y recuperar datos del usuario
        val correoRegistro = binding.editCorreo.text.toString()

        val bundle = Bundle()
        bundle.putString("correo", correoRecibido.toString())
        bundle.putString("nombre", correoRegistro)

        //Funcionalidad BOTONES
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
                val referencia = database.getReference("usuarios")

                //Mostrar indicador progreso
                binding.progressBar.visibility = View.VISIBLE

                referencia.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var usuarioEncontrado = false
                        snapshot.children.forEach {usuarioSnapshot ->
                            val usuario = usuarioSnapshot.getValue(Usuario::class.java)
                            if(usuario?.correo.toString() == binding.editCorreo.text.toString()){
                                usuarioEncontrado = true
                                auth.signInWithEmailAndPassword(binding.editCorreo.text.toString(), binding.editPass.text.toString())
                                    .addOnCompleteListener {
                                        //Ocultar el indicador
                                        binding.progressBar.visibility = View.GONE

                                        if(it.isSuccessful){
                                            //Guardar el correo en preferencias para futuras sesiones
                                            sharedPreferences.edit().putString("email", binding.editCorreo.text.toString()).apply()

                                            val bundle = Bundle().apply { putString("nombre", usuario!!.nombre) }
                                            findNavController().navigate(R.id.action_LoginFragment_to_MainFragment, bundle)
                                        } else {
                                            Snackbar.make(view, "Datos incorrectos, prueba otra vez.", Snackbar.LENGTH_SHORT).show()
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
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }

    }

    override fun onDetach() {
        super.onDetach()
    }
}