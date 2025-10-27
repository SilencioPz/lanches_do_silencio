package com.example.lanchesdosilencio.models

data class Comanda(
    val numero: Long,
    val itens: List<Product>,
    val total: Double,
    val ativa: Boolean = true
)