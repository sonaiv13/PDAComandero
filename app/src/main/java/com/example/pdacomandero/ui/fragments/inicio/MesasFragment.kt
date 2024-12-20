package com.example.pdacomandero.ui.fragments.inicio

import android.content.Context
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdacomandero.R
import com.example.pdacomandero.adapters.inicio.PedidosAdapter
import com.example.pdacomandero.adapters.menu.CategoriasAdapter
import com.example.pdacomandero.adapters.menu.ProductosAdapter
import com.example.pdacomandero.databinding.FragmentMesasBinding
import com.example.pdacomandero.models.Mesa
import com.example.pdacomandero.models.Pedido
import com.example.pdacomandero.models.Producto
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MesasFragment : Fragment(), CategoriasAdapter.CategoriaClickListener,
    ProductosAdapter.ProductosClickListener, PedidosAdapter.PedidoClickListener {

    private lateinit var binding: FragmentMesasBinding
    private lateinit var database: FirebaseDatabase
    var mesaSeleccionada: Int? = null
    private lateinit var productosAdapter: ProductosAdapter
    private lateinit var categoriasAdapter: CategoriasAdapter
    private lateinit var pedidosAdapter: PedidosAdapter
    private var listaProductos = ArrayList<Producto>()
    private var listaCategorias = ArrayList<String>()
    private var listaPedidos = ArrayList<Producto>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = FirebaseDatabase.getInstance("https://pdacomandero-default-rtdb.europe-west1.firebasedatabase.app/")
        mesaSeleccionada = arguments?.getInt("mesaSeleccionada")
        listaProductos = ArrayList()
        listaCategorias = ArrayList()
        listaPedidos = ArrayList()
        productosAdapter = ProductosAdapter(listaProductos, context, this, R.layout.recycler_productos_mesas)
        categoriasAdapter = CategoriasAdapter(listaCategorias, context, this, R.layout.recycler_categoria_mesas)
        pedidosAdapter = PedidosAdapter(listaPedidos, context, this)
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

        binding.textMesaSeleccionada.text = "Mesa ${mesaSeleccionada ?: "No seleccionada"}"

        setUpRecyclerView(binding.recyclerPedido, pedidosAdapter)
        setUpRecyclerView(binding.recyclerProductos, productosAdapter)

        mesaSeleccionada?.let {
            cambiarDisponibilidad(it)
            cargarDatosMesa(it)
        }

        binding.tabLayoutMesa.apply {
            addTab(newTab().setIcon(R.drawable.bebidas))
            addTab(newTab().setIcon(R.drawable.comida))
            addTab(newTab().setIcon(R.drawable.postres))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    listaProductos.clear()
                    when (tab?.position) {
                        0 -> showRecycler(binding.recyclerProductos) { rellenarRecyclerBebidas() }
                        1 -> {
                            listaCategorias.clear()
                            showRecycler(binding.recyclerCategorias) {
                                setUpRecyclerView(binding.recyclerCategorias, categoriasAdapter)
                                rellenarCategorias()
                            }
                        }
                        2 -> showRecycler(binding.recyclerProductos) { rellenarRecyclerPostres() }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    listaProductos.clear()
                    when (tab?.position) {
                        0 -> showRecycler(binding.recyclerProductos) { rellenarRecyclerBebidas() }
                        1 -> {
                            listaCategorias.clear()
                            showRecycler(binding.recyclerCategorias) {
                                setUpRecyclerView(binding.recyclerCategorias, categoriasAdapter)
                                rellenarCategorias()
                            }
                        }
                        2 -> showRecycler(binding.recyclerProductos) { rellenarRecyclerPostres() }
                    }
                }

            })
        }

        rellenarRecyclerBebidas()

    }

    private fun getUserId() = FirebaseAuth.getInstance()?.uid

    private fun setUpRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun cambiarDisponibilidad(numMesa: Int){
        getUserId()?.let{ userId ->
            val mesaRef = database.getReference("usuarios/$userId/mesas/$numMesa")

            mesaRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nuevaDisponibilidad = snapshot.child("pedidos").children.none { it.child("productos").exists() }
                    mesaRef.child("disponible").setValue(nuevaDisponibilidad)
                        .addOnSuccessListener {
                            Log.d("MesasFragment", "Disponibilidad actualizada a $nuevaDisponibilidad.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MesasFragment", "Error al actualizar la disponibilidad: ${e.message}")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("MesasFragment", "Error al cargar datos de la mesa: ${error.message}")
                }

            })
        }
    }

    private fun cargarDatosMesa(numeroMesa: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if(userId != null){
            getUserId()?.let { userId ->
                database.getReference("usuarios/$userId/mesas/$numeroMesa")
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mesa = snapshot.getValue(Mesa::class.java)
                        mesa?.let { mostrarDatosMesa(it) } ?: run {
                            binding.textMesaSeleccionada.text = "Mesa $numeroMesa no encontrada"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("MesasFragment", "Error al cargar datos de la mesa: ${error.message}")
                    }

                })
            }
        }
    }

    private fun mostrarDatosMesa(mesa: Mesa) {
        binding.textMesaSeleccionada.text = "Mesa ${mesa.numero}"
        rellenarRecyclerPedido()
    }

    private fun showRecycler(recyclerToShow: View, action: () -> Unit) {
        binding.recyclerProductos.visibility = View.GONE
        binding.recyclerCategorias.visibility = View.GONE
        recyclerToShow.visibility = View.VISIBLE
        action()
    }

    private fun rellenarRecyclerPedido(){
        getUserId().let { userId ->
            val mesaRef = database.getReference("usuarios/$userId/mesas/${mesaSeleccionada}")

            mesaRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mesa = snapshot.getValue(Mesa::class.java)
                    if(mesa != null){
                        val listaPedidos = mesa.pedidos
                        if (listaPedidos.isNotEmpty()){
                            val ultimoPedido = listaPedidos.last()

                            val productosRef = mesaRef.child("pedidos").child(listaPedidos.indexOf(ultimoPedido).toString()).child("productos")
                            productosRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val productos = snapshot.children.mapNotNull { it.getValue(Producto::class.java) }

                                    pedidosAdapter.actualizarPedido(ArrayList(productos))
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("Firebase", "Error al leer los productos", error.toException())
                                }

                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error al leer la mesa", error.toException())
                }

            })
        }
    }

    private fun rellenarRecyclerBebidas() = cargarProductos("bebidas", productosAdapter)

    private fun rellenarRecyclerPostres() = cargarProductos("postres", productosAdapter)

    private fun rellenarCategorias(){
        val databaseRef = database.getReference("menu").child("comida")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaCategorias.clear()
                snapshot.children.forEach {
                    val categoria = it.key ?: ""
                    listaCategorias.add(categoria)
                    Log.d("MenuFragment", "Categoría encontrada: $categoria")
                }
                categoriasAdapter.actualizarCategorias(listaCategorias)
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
            }

        })
    }

    private fun cargarProductos(categoria: String, adapter: ProductosAdapter) {
        database.getReference("menu/$categoria").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productos = snapshot.children.mapNotNull { it.getValue(Producto::class.java) }
                adapter.actualizarProductos(productos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar $categoria", error.toException())
            }

        })
    }

    private fun mostrarComidaCategoria(categoria: String){
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
        if(mesaSeleccionada != null){
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            if(userId != null){
                val mesaRef = database.getReference("usuarios").child(userId).child("mesas").child(mesaSeleccionada.toString())

                mesaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mesa = snapshot.getValue(Mesa::class.java)
                        if (mesa != null) {
                            val listaPedidos = mesa.pedidos

                            val pedidoExistente = listaPedidos.firstOrNull { it.numMesa == mesa.numero }

                            if (pedidoExistente != null){
                                pedidoExistente.productos.add(producto)
                                pedidoExistente.total += producto.precio
                            } else {
                                val nuevoPedido = Pedido(
                                    id = listaPedidos.size + 1,
                                    numMesa = mesa.numero,
                                    productos = mutableListOf(producto),
                                    total = producto.precio
                                )
                                listaPedidos.add(nuevoPedido)
                            }

                            mesa.pedidos = listaPedidos

                            //Guardar cambios en Firebase
                            mesaRef.setValue(mesa).addOnSuccessListener {
                                Snackbar.make(binding.root, "Producto añadido correctamente", Snackbar.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Snackbar.make(binding.root, "Error al guardar el producto", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Snackbar.make(binding.root,"Algo ha fallado con la conexion a internet", Snackbar.LENGTH_SHORT).show()
                    }

                })
            }
        } else {
            Snackbar.make(binding.root,"Ninguna mesa seleccionada", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onPedidoClick(producto: Producto) {
        if (mesaSeleccionada != null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val mesaRef = database.getReference("usuarios").child(userId).child("mesas").child(mesaSeleccionada.toString())

                mesaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mesa = snapshot.getValue(Mesa::class.java)
                        if (mesa != null) {
                            val listaPedidos = mesa.pedidos
                            val pedidoActual = listaPedidos.find { it.numMesa == mesa.numero }

                            if (pedidoActual != null) {
                                // Verificar si producto no es nulo antes de operar
                                val productoIndex = pedidoActual.productos.indexOfFirst { it.id == producto.id }
                                if (productoIndex >= 0) {
                                    pedidoActual.productos.removeAt(productoIndex)
                                    pedidoActual.total -= producto.precio

                                    // Si no hay más productos en el pedido, elimina el pedido completo
                                    if (pedidoActual.productos.isEmpty()) {
                                        listaPedidos.remove(pedidoActual)
                                    }

                                    // Actualiza Firebase
                                    mesa.pedidos = listaPedidos
                                    mesaRef.setValue(mesa).addOnSuccessListener {
                                        // Actualiza la lista local
                                        listaPedidos.flatMap { it.productos }.also {
                                            pedidosAdapter.actualizarPedido(ArrayList(it))
                                        }
                                        Snackbar.make(binding.root, "Producto eliminado", Snackbar.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Snackbar.make(binding.root, "Error al eliminar el producto", Snackbar.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Snackbar.make(binding.root, "Producto no encontrado", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Snackbar.make(binding.root, "Error al conectar con Firebase", Snackbar.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Snackbar.make(binding.root, "Ninguna mesa seleccionada", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onAgregarCantidad(producto: Producto) {
        if (mesaSeleccionada != null){
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if(userId != null){
                val mesaRef = database.getReference("usuarios").child(userId).child("mesas").child(mesaSeleccionada.toString())

                mesaRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mesa = snapshot.getValue(Mesa::class.java)
                        if(mesa != null){
                            val pedidoActual = mesa.pedidos.lastOrNull { it.numMesa == mesa.numero }
                            pedidoActual?.let {
                                val productoIndex = it.productos.indexOfFirst { p -> p.id == producto.id }
                                if(productoIndex >= 0){
                                    it.productos[productoIndex].cantidad += 1
                                    it.total += producto.precio
                                }
                            }
                            mesaRef.setValue(mesa).addOnSuccessListener {
                                rellenarRecyclerPedido()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Snackbar.make(binding.root, "Error al conectar con Firebase", Snackbar.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    override fun onQuitarCantidad(producto: Producto) {
        if (mesaSeleccionada != null){
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if(userId != null){
                val mesaRef = database.getReference("usuarios").child(userId).child("mesas").child(mesaSeleccionada.toString())

                mesaRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mesa = snapshot.getValue(Mesa::class.java)
                        if(mesa != null){
                            val pedidoActual = mesa.pedidos.lastOrNull { it.numMesa == mesa.numero }
                            pedidoActual?.let {
                                val productoIndex = it.productos.indexOfFirst { p -> p.id == producto.id }
                                if(productoIndex >= 0){
                                    if(it.productos[productoIndex].cantidad > 1){
                                        it.productos[productoIndex].cantidad -= 1
                                        it.total -= producto.precio
                                    } else {
                                        it.productos.removeAt(productoIndex)
                                        it.total -= producto.precio
                                    }
                                }
                            }
                            mesaRef.setValue(mesa).addOnSuccessListener {
                                rellenarRecyclerPedido()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Snackbar.make(binding.root, "Error al conectar con Firebase", Snackbar.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }


    override fun onDetach() {
        super.onDetach()
    }

}