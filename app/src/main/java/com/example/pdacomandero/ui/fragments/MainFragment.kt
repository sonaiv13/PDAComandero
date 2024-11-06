package com.example.pdacomandero.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pdacomandero.R
import com.example.pdacomandero.databinding.FragmentMainBinding
import com.example.pdacomandero.ui.dialogs.DialogoSalir
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController

        //Bottom Navigation Bar
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.setupWithNavController(navController)

        binding.bottomNav.setOnItemReselectedListener { item ->
            when(item.itemId){
                R.id.nav_inicio -> {
                    navController.popBackStack(R.id.nav_inicio, true)
                    navController.navigate(R.id.nav_inicio)
                }
                R.id.nav_menu -> {
                    navController.popBackStack(R.id.nav_menu, true)
                    navController.navigate(R.id.nav_menu)
                }
                R.id.nav_facturas -> {
                    navController.popBackStack(R.id.nav_facturas, true)
                    navController.navigate(R.id.nav_facturas)
                }
            }
        }

        //Drawer Navigation Bar
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = binding.drawerNav

        binding.btnOpciones.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //Listener Drawer Items
        navigationView.setNavigationItemSelectedListener {item ->
            when(item.itemId){
                R.id.cerrarSesion -> {
                    val dialogoSalir = DialogoSalir()
                    dialogoSalir.show(childFragmentManager, "DialogoSalir")
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }

        }

    }

    override fun onDetach() {
        super.onDetach()
    }
}