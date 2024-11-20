package com.example.pdacomandero.models

class Pedido (
    val id: Int,
    val mesa: Mesa,
    val productos: MutableList<Pedido> = mutableListOf(),
    val total: Double
)