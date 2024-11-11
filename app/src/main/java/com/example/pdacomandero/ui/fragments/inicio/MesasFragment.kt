package com.example.pdacomandero.ui.fragments.inicio

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.FragmentInicioBinding
import com.example.pdacomandero.databinding.FragmentMainBinding
import com.example.pdacomandero.databinding.FragmentMesasBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MesasFragment : Fragment() {

    private lateinit var binding: FragmentMesasBinding
    private var mesaSeleccionada: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
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



    }

    override fun onDetach() {
        super.onDetach()
    }
}