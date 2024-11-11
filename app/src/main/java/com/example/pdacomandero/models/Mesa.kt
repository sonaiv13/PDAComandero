package com.example.pdacomandero.models

import java.io.Serializable

class Mesa (
    val id: Int,
    val numero: Int,
    var disponible: Boolean = true,
    var pedidos: MutableList<Pedido> = mutableListOf()
): Serializable {
    constructor() : this(0,0, true, mutableListOf())
}