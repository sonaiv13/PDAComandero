package com.example.pdacomandero.ui.fragments.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdacomandero.R
import com.example.pdacomandero.adapters.menu.ProductosAdapter
import com.example.pdacomandero.adapters.menu.CategoriasAdapter
import com.example.pdacomandero.databinding.FragmentMenuBinding
import com.example.pdacomandero.models.Producto
import com.example.pdacomandero.ui.dialogs.DialogoDetalleProducto
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuFragment : Fragment(), CategoriasAdapter.CategoriaClickListener,
    ProductosAdapter.ProductosClickListener {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var productosAdapter: ProductosAdapter
    private lateinit var categoriasAdapter: CategoriasAdapter
    private var listaProductos = ArrayList<Producto>()
    private var listaCategorias = ArrayList<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
        listaProductos = ArrayList()
        productosAdapter = ProductosAdapter(listaProductos, context, this)
        categoriasAdapter = CategoriasAdapter(listaCategorias, context, this)
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

        binding.recyclerProductos.adapter = productosAdapter
        binding.recyclerProductos.layoutManager = GridLayoutManager(context, 2)


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                listaProductos.clear()
                when (tab?.position){
                    0 -> {
                        binding.recyclerCategorias.visibility = View.GONE
                        binding.recyclerProductos.visibility = View.VISIBLE
                        rellenarRecyclerBebidas()
                    }
                    1 -> {
                        listaCategorias.clear()
                        binding.recyclerProductos.visibility = View.GONE
                        binding.recyclerCategorias.visibility = View.VISIBLE
                        binding.recyclerCategorias.adapter = categoriasAdapter
                        binding.recyclerCategorias.layoutManager = LinearLayoutManager(context)
                        rellenarCategorias()

                    }
                    2 -> {
                        binding.recyclerCategorias.visibility = View.GONE
                        binding.recyclerProductos.visibility = View.VISIBLE
                        rellenarRecyclerPostres()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                listaProductos.clear()
                when (tab?.position){
                    0 -> {
                        binding.recyclerCategorias.visibility = View.GONE
                        binding.recyclerProductos.visibility = View.VISIBLE
                        rellenarRecyclerBebidas()
                    }
                    1 -> {
                        listaCategorias.clear()
                        binding.recyclerProductos.visibility = View.GONE
                        binding.recyclerCategorias.visibility = View.VISIBLE
                        binding.recyclerCategorias.adapter = categoriasAdapter
                        binding.recyclerCategorias.layoutManager = LinearLayoutManager(context)
                        rellenarCategorias()

                    }
                    2 -> {
                        binding.recyclerCategorias.visibility = View.GONE
                        binding.recyclerProductos.visibility = View.VISIBLE
                        rellenarRecyclerPostres()
                    }
                }
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
                        productosAdapter.agregarProductos(producto)
                    } else {
                        Log.e("MenuFragment", "Producto nulo encontrado en la base de datos.")
                    }
                }
                productosAdapter.notifyDataSetChanged()
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
                        productosAdapter.agregarProductos(producto)
                    } else {
                        Log.e("MenuFragment", "Producto nulo encontrado en la base de datos.")
                    }
                }
                productosAdapter.notifyDataSetChanged()
                Log.d("MenuFragment", "Datos cargados: ${listaProductos.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    fun rellenarCategorias(){
        val databaseRef = database.getReference("menu").child("comida")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("MenuFragment", "Firebase respuesta: ${snapshot.value}")

                listaCategorias.clear()
                snapshot.children.forEach {
                    val categoria = it.key ?: ""
                    listaCategorias.add(categoria)
                    Log.d("MenuFragment", "Categoría encontrada: $categoria")
                }
                categoriasAdapter.actualizarCategorias(listaCategorias)
                Log.d("MenuFragment", "Adaptador notificado de cambios")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuFragment", "Error al cargar categorías: ${error.message}")
                Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
            }

        })
    }

    fun mostrarComidaCategoria(categoria: String){
        val databaseRef = database.getReference("menu").child("comida").child("$categoria")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                snapshot.children.forEach {
                    val producto = it.getValue(Producto::class.java)
                    if (producto != null) {
                        productosAdapter.agregarProductos(producto)
                    } else {
                        Log.e("MenuFragment", "Producto nulo encontrado en la base de datos.")
                    }
                }
                productosAdapter.notifyDataSetChanged()
                Log.d("MenuFragment", "Datos cargados: ${listaProductos.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCategoriaClick(categoria: String) {
        binding.recyclerCategorias.visibility = View.GONE
        binding.recyclerProductos.visibility = View.VISIBLE
        binding.recyclerProductos.adapter = productosAdapter
        mostrarComidaCategoria(categoria)
    }

    override fun onProductoClick(producto: Producto) {
        val dialogo = DialogoDetalleProducto()
        dialogo.setProducto(producto)
        dialogo.show(childFragmentManager, null)
    }

    override fun onDetach() {
        super.onDetach()
    }
}