package com.example.lanchesdosilencio.models

import androidx.compose.runtime.mutableStateListOf
import com.example.lanchesdosilencio.R

class Menu {
    private val _produtos = mutableStateListOf<Product>()
    val produtos: List<Product> get() = _produtos

    init {
        _produtos.addAll(
            listOf(
                Product(1, "X-Salada", "Hambúrguer, queijo, alface, " +
                        "tomate", 12.00, 0,
                    "Lanche" ,R.drawable.x_salada),
                Product(2, "X-Bacon", "Hambúrguer, queijo, " +
                        "bacon",
                    15.00, 0, "Lanche", R.drawable.x_bacon),
                Product(3, "Cachorro-Quente", "Salsicha, purê, batata " +
                        "palha, " +
                        "molho",
                    10.00, 0, "Lanche", R.drawable.hot_dog),
                Product(4, "Refri Lata", "Coca, Guaraná ou " +
                        "Fanta",
                    5.00, 0, "Bebida", R.drawable.refri),
                Product(5, "Suco Natural", "Laranja ou Limão",
                    6.00, 0, "Bebida", R.drawable.suco),
                Product(6, "X-Tudo", "Hambúrguer, queijo, alface, tomate, " +
                        "bacon, ovo, " +
                        "salsicha", 20.00, 0,
                    "Lanche", R.drawable.x_tudo)
            )
        )
    }

    fun getProdutosSelecionados(): List<Product> {
        return produtos.filter { it.selecionado }
    }

    fun getTotalPedido(): Double {
        return getProdutosSelecionados().sumOf { it.preco }
    }
}