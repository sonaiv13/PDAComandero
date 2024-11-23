package com.example.pdacomandero.ui.fragments.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdacomandero.R
import com.example.pdacomandero.adapters.menu.BebidasAdapter
import com.example.pdacomandero.adapters.menu.PostresAdapter
import com.example.pdacomandero.databinding.FragmentMenuBinding
import com.example.pdacomandero.models.Producto
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var bebidasAdapter: BebidasAdapter
    private lateinit var postresAdapter: PostresAdapter
    private var listaProductos = ArrayList<Producto>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
        listaProductos = ArrayList()
        bebidasAdapter = BebidasAdapter(listaProductos, context)
        postresAdapter = PostresAdapter(listaProductos, context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayoutMenu
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.bebidas))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.comida))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.postres))

        binding.recyclerProductos.adapter = bebidasAdapter
        binding.recyclerProductos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position){
                    0 -> {
                        binding.recyclerProductos.adapter = bebidasAdapter
                        binding.recyclerProductos.visibility = View.VISIBLE
                        rellenarRecyclerBebidas()
                    }
                    1 -> {
                        listaProductos.clear()
                        bebidasAdapter.notifyDataSetChanged()
                    }
                    2 -> {
                        binding.recyclerProductos.adapter = postresAdapter
                        binding.recyclerProductos.visibility = View.VISIBLE
                        rellenarRecyclerPostres()
                    }
                    else -> {
                        binding.recyclerProductos.visibility = View.GONE
                        listaProductos.clear()
                        bebidasAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        rellenarRecyclerBebidas()

    }

    fun rellenarRecyclerBebidas(){
        val databaseRef = database.getReference("menu").child("bebidas")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                snapshot.children.forEach{
                    val producto = it.getValue(Producto::class.java)
                    if (producto != null) {
                        bebidasAdapter.agregarProductos(producto)
                    } else {
                        Log.e("MenuFragment", "Producto nulo encontrado en la base de datos.")
                    }
                }
                bebidasAdapter.notifyDataSetChanged()
                Log.d("MenuFragment", "Datos cargados: ${listaProductos.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    fun rellenarRecyclerPostres(){
        val databaseRef = database.getReference("menu").child("postres")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                snapshot.children.forEach{
                    val producto = it.getValue(Producto::class.java)
                    if (producto != null) {
                        postresAdapter.agregarProductos(producto)
                    } else {
                        Log.e("MenuFragment", "Producto nulo encontrado en la base de datos.")
                    }
                }
                postresAdapter.notifyDataSetChanged()
                Log.d("MenuFragment", "Datos cargados: ${listaProductos.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
    }
}