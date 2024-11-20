package com.example.pdacomandero.ui.fragments.inicio

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.FragmentInicioBinding
import com.example.pdacomandero.databinding.FragmentMainBinding
import com.example.pdacomandero.ui.dialogs.DialogoMesas
import com.google.android.material.bottomnavigation.BottomNavigationView

class InicioFragment : Fragment() {

    private lateinit var binding: FragmentInicioBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Funcionalidad BOTONES
        binding.btnMesas.setOnClickListener {
            val dialogoMesas = DialogoMesas()
            dialogoMesas.show(childFragmentManager, "DialogoMesas")
        }

        binding.btnCuentas.setOnClickListener {  }

    }

    override fun onDetach() {
        super.onDetach()
    }
}