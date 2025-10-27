package com.example.lanchesdosilencio.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object ComandaManager {
    private var proximoNumero: Long = 1
    val comandas: SnapshotStateList<Comanda> = mutableStateListOf()

    fun criarComanda(itens: List<Product>): Comanda {
        val total = itens.sumOf { it.preco }
        val comanda = Comanda(
            numero = proximoNumero++,
            itens = itens.toList(),
            total = total
        )
        comandas.add(comanda)
        println("DEBUG - Nova comanda criada: #${comanda.numero}")
        return comanda
    }

    fun adicionarItensComanda(numero: Long, novosItens: List<Product>): Comanda? {
        val comandaExistente = comandas.find { it.numero == numero }
        return comandaExistente?.let {
            val itensAtualizados = it.itens.toMutableList().apply { addAll(novosItens) }
            val novoTotal = itensAtualizados.sumOf { it.preco }

            val comandaAtualizada = it.copy(
                itens = itensAtualizados,
                total = novoTotal
            )

            comandas.remove(it)
            comandas.add(comandaAtualizada)
            println("DEBUG - Itens adicionados à comanda existente #$numero")
            println("DEBUG - Nova quantidade de itens: ${comandaAtualizada.itens.size}")
            comandaAtualizada
        }
    }

    fun finalizarComanda(numero: Long) {
        comandas.find { it.numero == numero }?.let { comanda ->
            comandas.remove(comanda)
            println("DEBUG - Comanda #$numero finalizada")
        }
    }

    fun getComandasAtivas(): List<Comanda> {
        return comandas.toList()
    }

    fun getComandaPorNumero(numero: Long): Comanda? {
        return comandas.find { it.numero == numero }
    }

    fun getTotalVendas(): Double {
        return comandas.sumOf { it.total }
    }
}