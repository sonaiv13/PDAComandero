package com.example.pdacomandero.models

import java.io.Serializable

class Pedido (
    val id: Int,
    val numMesa: Int,
    val productos: MutableList<Producto> = mutableListOf(),
    var total: Double
): Serializable {
    constructor() : this(0,0, mutableListOf(), 0.0)
}