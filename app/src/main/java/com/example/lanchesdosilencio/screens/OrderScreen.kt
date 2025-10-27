package com.example.lanchesdosilencio.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lanchesdosilencio.models.Comanda
import com.example.lanchesdosilencio.models.ComandaManager
import com.example.lanchesdosilencio.models.Menu
import com.example.lanchesdosilencio.models.Product
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    menu: Menu,
    onBackClick: () -> Unit,
    onViewComandas: () -> Unit,
    onPedidoFinalizado: () -> Unit,
    onPaymentClick: (Long) -> Unit,
    comandaEspecifica: Long? = null,
    modifier: Modifier = Modifier
) {
    var comanda by remember { mutableStateOf<Comanda?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    val itensSelecionados by remember(menu.produtos) {
        derivedStateOf {
            menu.produtos.filter { it.quantidade > 0 }
        }
    }

    LaunchedEffect(comandaEspecifica) {
        comandaEspecifica?.let { numero ->
            comanda = ComandaManager.getComandaPorNumero(numero)
            println("DEBUG - Carregada comanda existente: #$numero")
        }
    }

    LaunchedEffect(showSuccess) {
        if (showSuccess && comandaEspecifica != null) {
            kotlinx.coroutines.delay(100)
            comanda = ComandaManager.getComandaPorNumero(comandaEspecifica)
            println("DEBUG - Comanda recarregada após adicionar itens: #$comandaEspecifica")
        }
    }

    LaunchedEffect(itensSelecionados) {
        if (itensSelecionados.isNotEmpty() && comanda == null) {
            println("DEBUG - Itens prontos para comanda: ${itensSelecionados.map { 
                "${it.quantidade}x ${it.nome}" }}")
        } else if (itensSelecionados.isEmpty()) {
            if (comandaEspecifica == null) {
                comanda = null
            }
        }
    }

    fun processarItens() {
        println("DEBUG - processarItens chamado com comandaEspecifica = $comandaEspecifica")
        if (itensSelecionados.isNotEmpty()) {
            val itensParaProcessar = itensSelecionados.flatMap { produto ->
                List(produto.quantidade) { produto.copy(quantidade = 1) }
            }

            if (comandaEspecifica != null) {
                comanda = ComandaManager.adicionarItensComanda(
                    comandaEspecifica, itensParaProcessar)
                println("DEBUG - Itens adicionados à comanda existente #$comandaEspecifica")
                println("DEBUG - Nova quantidade de itens na comanda: ${comanda?.itens?.size}")
            } else {
                comanda = ComandaManager.criarComanda(itensParaProcessar)
                println("DEBUG - Nova comanda criada: #${comanda?.numero}")
            }
            showSuccess = true

            menu.produtos.forEach { produto ->
                val index = menu.produtos.indexOf(produto)
                (menu.produtos as MutableList<Product>)[index] = produto.copy(quantidade = 0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "COMANDA - PREPARO",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_revert),
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (itensSelecionados.isNotEmpty() && !showSuccess) {
                        IconButton(onClick = { processarItens() }) {
                            Icon(
                                painter = painterResource(id =
                                    if (comandaEspecifica != null) android.R.drawable.ic_menu_add
                                    else android.R.drawable.ic_menu_save
                                ),
                                contentDescription = if (comandaEspecifica != null) "Adicionar " +
                                        "Itens" else "Criar Comanda",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = onViewComandas) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_view),
                            contentDescription = "Ver Todas as Comandas",
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
            if (showSuccess) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = if (comandaEspecifica != null)
                            "✅ ITENS ADICIONADOS À COMANDA #$comandaEspecifica!"
                        else
                            "✅ COMANDA CRIADA! PREPARE OS PEDIDOS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (comandaEspecifica != null && itensSelecionados.isNotEmpty() && !showSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0xFF1976D2))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Itens Pendentes para Adicionar à Comanda #$comandaEspecifica",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        itensSelecionados.forEach { produto ->
                            Text(
                                text = "${produto.quantidade}x ${produto.nome} - R$ ${"%.2f".format(produto.preco * produto.quantidade)}",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total a Adicionar: R$ ${"%.2f".format(itensSelecionados.sumOf { it.preco * it.quantidade })}",
                            color = Color(0xFF4CAF50),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Novo Total Após Adição: R$ ${"%.2f".format((comanda?.total ?: 0.0) + itensSelecionados.sumOf { it.preco * it.quantidade })}",
                            color = Color(0xFF4CAF50),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Clique no ícone ➕ no topo para confirmar e adicionar",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            when {
                comanda == null && itensSelecionados.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum item para preparar\n\nVolte ao cardápio para " +
                                    "adicionar pedidos",
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                comanda == null && itensSelecionados.isNotEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (comandaEspecifica != null)
                                    "➕ Adicionar Itens à Comanda #$comandaEspecifica"
                                else
                                    "📋 Itens Prontos para Nova Comanda",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            itensSelecionados.forEach { produto ->
                                Text(
                                    text = "${produto.quantidade}x ${produto.nome} - " +
                                            "R$ ${"%.2f".format(
                                                produto.preco * produto.quantidade)}",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TOTAL: R$ ${"%.2f".format(
                                    itensSelecionados.sumOf { it.preco * it.quantidade })}",
                                color = Color(0xFF4CAF50),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                else -> {
                    val comandaData = comanda!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1B5E20))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "COMANDA Nº",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${comandaData.numero}",
                                color = Color.White,
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (showSuccess) "ITENS ATUALIZADOS - PREPARAR PARA O CLIENTE"
                                else "PREPARAR PARA O CLIENTE",
                                color = Color(0xFF4CAF50),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        val itensAgrupados = comandaData.itens
                            .groupingBy { it.id }
                            .eachCount()
                            .map { (id, quantidade) ->
                                val produto = comandaData.itens.first { it.id == id }
                                produto to quantidade
                            }
                        items(itensAgrupados) { (produto, quantidade) ->
                            ItemComandaVisual(
                                produto = produto,
                                quantidade = quantidade
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
                                containerColor = Color(0xFF1976D2)
                            )
                        ) {
                            Text(
                                text = "👨‍🍳 PREPARE TODOS OS ITENS ACIMA E ENTREGUE AO CLIENTE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
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
                                    text = "TOTAL A PAGAR:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "R$ ${"%.2f".format(comandaData.total)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (comandaEspecifica != null) {
                                Button(
                                    onClick = onBackClick,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1976D2)
                                    )
                                ) {
                                    Text("➕ Adicionar Mais")
                                }
                            }

                            Button(
                                onClick = {
                                    comanda?.let {
                                        onPaymentClick(it.numero)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text("💳 Finalizar e Pagar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemComandaVisual(
    produto: Product,
    quantidade: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${quantidade}x",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (produto.imagemResId != 0) {
                Image(
                    painter = painterResource(id = produto.imagemResId),
                    contentDescription = produto.nome,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = produto.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCCC),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (quantidade > 1) {
                    Text(
                        text = "${quantidade} × R$ ${"%.2f".format(produto.preco)} = " +
                                "R$ ${"%.2f".format(produto.preco * quantidade)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "R$ ${"%.2f".format(produto.preco)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}