package com.example.pdacomandero.models

data class Mesa (
    val id: Int,
    val numero: Int,
    var disponible: Boolean = true,
    var pedidos: MutableList<Pedido> = mutableListOf()
) {
    constructor() : this(0,0, true, mutableListOf())
}