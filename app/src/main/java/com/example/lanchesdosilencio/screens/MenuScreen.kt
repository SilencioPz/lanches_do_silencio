package com.example.lanchesdosilencio.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lanchesdosilencio.models.Menu
import com.example.lanchesdosilencio.models.Product
import com.example.lanchesdosilencio.R
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menu: Menu,
    onOrderClick: () -> Unit,
    onViewComandas: () -> Unit,
    comandaParaAdicionar: Long? = null,
    modifier: Modifier = Modifier
) {
    val produtos by remember { mutableStateOf(menu.produtos) }

    val total by derivedStateOf {
        produtos.sumOf { it.preco * it.quantidade }
    }

    val mostrarBotaoPedido by derivedStateOf {
        produtos.any { it.quantidade > 0 }
    }

    val modoAdicionarComanda = comandaParaAdicionar != null

    LaunchedEffect(produtos) {
        val selecionados = produtos.filter { it.quantidade > 0 }
        println("DEBUG MenuScreen - Produtos selecionados: ${selecionados.map 
        { "${it.quantidade}x ${it.nome}" }}")
        println("DEBUG MenuScreen - Total selecionados: ${selecionados.sumOf { it.quantidade }}")

        if (modoAdicionarComanda) {
            println("DEBUG MenuScreen - Modo: Adicionando itens à Comanda #$comandaParaAdicionar")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (modoAdicionarComanda) "➕ ADICIONAR À COMANDA " +
                                "#$comandaParaAdicionar"
                        else "CARDÁPIO - LANCHES DO SILÊNCIO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                actions = {
                    IconButton(onClick = onViewComandas) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_view),
                            contentDescription = "Ver Comandas",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {

            if (modoAdicionarComanda) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_add),
                            contentDescription = "Adicionar",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Adicionando itens à Comanda #$comandaParaAdicionar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.lanches),
                contentDescription = "Logo Lanches do Silêncio",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(8.dp)
            )
            Text(
                text = if (modoAdicionarComanda) "Adicionar Itens à Comanda" else "Cardápio",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(produtos.size) { index ->
                    val produto = produtos[index]
                    ItemProdutoComQuantidade(
                        produto = produtos[index],
                        onQuantidadeChange = { delta ->
                            val novaQuantidade = (produto.quantidade + delta).coerceAtLeast(0)
                            val produtoAtualizado = produto.copy(quantidade = novaQuantidade)
                            (menu.produtos as MutableList<Product>)[index] = produtoAtualizado
                            println("DEBUG - ${produto.nome} quantidade atualizada para $novaQuantidade")
                        }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B5E20)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (modoAdicionarComanda) "Total a Adicionar:" else "Total " +
                                    "do Pedido:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "R$ ${"%.2f".format(total)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
                if (mostrarBotaoPedido) {
                    Button(
                        onClick = onOrderClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (modoAdicionarComanda) Color(0xFF1976D2)
                            else Color(0xFFD32F2F)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id =
                                    if (modoAdicionarComanda) android.R.drawable.ic_menu_add
                                    else android.R.drawable.ic_menu_edit
                                ),
                                contentDescription = if (modoAdicionarComanda) "Adicionar Itens"
                                else "Fazer Pedido"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (modoAdicionarComanda) "Adicionar à Comanda"
                                else "Fazer Pedido",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
fun ItemProdutoComQuantidade(
    produto: Product,
    onQuantidadeChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onQuantidadeChange(-1) },
                    enabled = produto.quantidade > 0
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                        contentDescription = "Diminuir",
                        tint = if (produto.quantidade > 0) Color(0xFFD32F2F) else Color(0xFF666666)
                    )
                }
                Text(
                    text = "${produto.quantidade}",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { onQuantidadeChange(1) }
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_add),
                        contentDescription = "Aumentar",
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = produto.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCCC),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "R$ ${"%.2f".format(produto.preco)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}