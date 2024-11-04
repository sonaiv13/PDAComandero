package com.example.pdacomandero.ui.fragments.ajustes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.FragmentAjustesBinding
import com.example.pdacomandero.databinding.FragmentInicioBinding
import com.example.pdacomandero.databinding.FragmentMainBinding
import com.example.pdacomandero.databinding.FragmentMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class AjustesFragment : Fragment() {

    private lateinit var binding: FragmentAjustesBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //BOTONES
        binding.btnSalir.setOnClickListener {
            cerrarSesion()
        }

    }

    private fun cerrarSesion(){
        FirebaseAuth.getInstance().signOut()
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("email").apply()

        val mainNavController = requireActivity().findNavController(R.id.nav_host_fragment_main)
        mainNavController.navigate(R.id.LoginFragment){
            popUpTo(R.id.LoginFragment) { inclusive = true }
        }
    }

    override fun onDetach() {
        super.onDetach()
    }
}