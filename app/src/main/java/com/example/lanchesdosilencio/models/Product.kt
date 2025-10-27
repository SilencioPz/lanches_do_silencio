package com.example.lanchesdosilencio.models

data class Product(
    val id: Int = 0,
    val nome: String,
    val descricao: String,
    val preco: Double,
    var quantidade: Int,
    val categoria: String = "Lanche",
    val imagemResId: Int = 0,
    var selecionado: Boolean = false
)