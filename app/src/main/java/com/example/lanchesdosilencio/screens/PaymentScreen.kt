package com.example.lanchesdosilencio.screens

import android.os.Build
import androidx.compose.material.icons.filled.ShoppingCart
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lanchesdosilencio.R
import com.example.lanchesdosilencio.models.Comanda
import com.example.lanchesdosilencio.models.ComandaManager
import com.example.lanchesdosilencio.utils.PdfGenerator

enum class PaymentScreenState {
    SELECT_METHOD,
    CASH_PAYMENT,
    CREDIT_CARD_PAYMENT,
    DEBIT_CARD_PAYMENT,
    PIX_PAYMENT
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    comandaNumero: Long,
    onBackClick: () -> Unit,
    onPaymentCompleted: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(PaymentScreenState.SELECT_METHOD) }
    var selectedPaymentMethod by remember { mutableStateOf("Dinheiro") }

    when (currentScreen) {
        PaymentScreenState.SELECT_METHOD -> {
            PaymentSelectionScreen(
                comandaNumero = comandaNumero,
                onBackClick = onBackClick,
                onPaymentMethodSelected = { method ->
                    selectedPaymentMethod = method
                    when (method) {
                        "Dinheiro" -> currentScreen = PaymentScreenState.CASH_PAYMENT
                        "Cartão Crédito" -> currentScreen = PaymentScreenState.CREDIT_CARD_PAYMENT
                        "Cartão Débito" -> currentScreen = PaymentScreenState.DEBIT_CARD_PAYMENT
                        "PIX" -> currentScreen = PaymentScreenState.PIX_PAYMENT
                    }
                }
            )
        }
        PaymentScreenState.CASH_PAYMENT -> {
            CashPaymentScreen(
                comandaNumero = comandaNumero,
                onBackClick = { currentScreen = PaymentScreenState.SELECT_METHOD },
                onPaymentCompleted = onPaymentCompleted
            )
        }
        PaymentScreenState.CREDIT_CARD_PAYMENT -> {
            CreditCardPaymentScreen(
                comandaNumero = comandaNumero,
                onBackClick = { currentScreen = PaymentScreenState.SELECT_METHOD },
                onPaymentCompleted = onPaymentCompleted
            )
        }
        PaymentScreenState.DEBIT_CARD_PAYMENT -> {
            DebitCardPaymentScreen(
                comandaNumero = comandaNumero,
                onBackClick = { currentScreen = PaymentScreenState.SELECT_METHOD },
                onPaymentCompleted = onPaymentCompleted
            )
        }
        PaymentScreenState.PIX_PAYMENT -> {
            PixPaymentScreen(
                comandaNumero = comandaNumero,
                onBackClick = { currentScreen = PaymentScreenState.SELECT_METHOD },
                onPaymentCompleted = onPaymentCompleted
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSelectionScreen(
    comandaNumero: Long,
    onBackClick: () -> Unit,
    onPaymentMethodSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var comanda by remember { mutableStateOf<Comanda?>(null) }

    LaunchedEffect(comandaNumero) {
        comanda = ComandaManager.getComandaPorNumero(comandaNumero)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "💳 PAGAMENTO - COMANDA #$comandaNumero",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
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
            comanda?.let { cmd ->
                // Resumo da comanda
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📋 RESUMO DA COMANDA",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val itensAgrupados = cmd.itens
                            .groupingBy { it.id }
                            .eachCount()
                            .map { (id, quantidade) ->
                                val produto = cmd.itens.first { it.id == id }
                                produto to quantidade
                            }

                        itensAgrupados.forEach { (produto, quantidade) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$quantidade× ${produto.nome}",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "R$ ${"%.2f".format(produto.preco * quantidade)}",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL:",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "R$ ${"%.2f".format(cmd.total)}",
                                color = Color(0xFF4CAF50),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "💳 SELECIONE O MÉTODO DE PAGAMENTO",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            onClick = { onPaymentMethodSelected("Dinheiro") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2D2D2D)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Dinheiro",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dinheiro",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Card(
                            onClick = { onPaymentMethodSelected("Cartão Crédito") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2D2D2D)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cartão Crédito",
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Cartão Crédito",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Card(
                            onClick = { onPaymentMethodSelected("Cartão Débito") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2D2D2D)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cartão Débito",
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Cartão Débito",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Card(
                            onClick = { onPaymentMethodSelected("PIX") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2D2D2D)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "PIX",
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "PIX",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Comanda não encontrada",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashPaymentScreen(
    comandaNumero: Long,
    onBackClick: () -> Unit,
    onPaymentCompleted: () -> Unit
) {
    val context = LocalContext.current
    var comanda by remember { mutableStateOf<Comanda?>(null) }
    var receivedAmount by remember { mutableStateOf("") }
    var pdfStatus by remember { mutableStateOf("") }

    LaunchedEffect(comandaNumero) {
        comanda = ComandaManager.getComandaPorNumero(comandaNumero)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "💵 PAGAMENTO EM DINHEIRO",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
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
                .padding(16.dp)
        ) {
            comanda?.let { cmd ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "💰 VALOR A PAGAR",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ ${"%.2f".format(cmd.total)}",
                            color = Color(0xFF4CAF50),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = receivedAmount,
                    onValueChange = { receivedAmount = it },
                    label = { Text("Valor recebido", color = Color.Gray) },
                    placeholder = { Text("Ex: 50.00", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D2D2D),
                        unfocusedContainerColor = Color(0xFF2D2D2D),
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                receivedAmount.toDoubleOrNull()?.let { received ->
                    val change = received - cmd.total
                    if (change > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1B5E20)
                            )
                        ) {
                            Text(
                                text = "💰 TROCO: R$ ${"%.2f".format(change)}",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else if (change < 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFD32F2F)
                            )
                        ) {
                            Text(
                                text = "❌ VALOR INSUFICIENTE - FALTAM R$ ${"%.2f".format(-change)}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val received = receivedAmount.toDoubleOrNull() ?: 0.0
                        if (received >= cmd.total) {
                            val resultado = PdfGenerator.generateComandaPdf(
                                context, cmd, "Dinheiro")
                            pdfStatus = resultado

                            ComandaManager.finalizarComanda(cmd.numero)

                            Toast.makeText(context, "Pagamento em dinheiro processado com sucesso!",
                                Toast.LENGTH_LONG).show()

                            if (!resultado.contains("Erro")) {
                                Toast.makeText(context, "PDF gerado automaticamente!",
                                    Toast.LENGTH_LONG).show()
                            }

                            onPaymentCompleted()
                        } else {
                            Toast.makeText(context, "Valor recebido é insuficiente!",
                                Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    enabled = receivedAmount.toDoubleOrNull() ?: 0.0 >= cmd.total
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Confirmar Pagamento"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "💵 CONFIRMAR PAGAMENTO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardPaymentScreen(
    comandaNumero: Long,
    onBackClick: () -> Unit,
    onPaymentCompleted: () -> Unit
) {
    val context = LocalContext.current
    var comanda by remember { mutableStateOf<Comanda?>(null) }
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var installments by remember { mutableStateOf(1) }

    LaunchedEffect(comandaNumero) {
        comanda = ComandaManager.getComandaPorNumero(comandaNumero)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "💳 PAGAMENTO - CARTÃO CRÉDITO",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
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
                .padding(16.dp)
        ) {
            comanda?.let { cmd ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "💰 VALOR A PAGAR",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ ${"%.2f".format(cmd.total)}",
                            color = Color(0xFF4CAF50),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Número do Cartão", color = Color.Gray) },
                    placeholder = { Text("1234 5678 9012 3456", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D2D2D),
                        unfocusedContainerColor = Color(0xFF2D2D2D),
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Nome no Cartão", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D2D2D),
                        unfocusedContainerColor = Color(0xFF2D2D2D),
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Validade", color = Color.Gray) },
                        placeholder = { Text("MM/AA", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF2D2D2D),
                            unfocusedContainerColor = Color(0xFF2D2D2D),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV", color = Color.Gray) },
                        placeholder = { Text("123", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF2D2D2D),
                            unfocusedContainerColor = Color(0xFF2D2D2D),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📈 PARCELAMENTO",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${installments}x de R$ ${"%.2f".format(cmd.total / installments)}",
                            color = Color(0xFF4CAF50),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = installments.toFloat(),
                            onValueChange = { installments = it.toInt() },
                            valueRange = 1f..12f,
                            steps = 11,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "$installments parcelas",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val resultado = PdfGenerator.generateComandaPdf(
                            context, cmd, "Cartão Crédito - ${installments}x")

                        ComandaManager.finalizarComanda(cmd.numero)

                        Toast.makeText(context,
                            "Pagamento no cartão de crédito aprovado! ${installments}x de R$ ${"%.2f".format(cmd.total / installments)}",
                            Toast.LENGTH_LONG).show()

                        if (!resultado.contains("Erro")) {
                            Toast.makeText(context, "PDF gerado automaticamente!",
                                Toast.LENGTH_LONG).show()
                        }

                        onPaymentCompleted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = cardNumber.length >= 16 && cardName.isNotEmpty() &&
                            expiryDate.length >= 5 && cvv.length >= 3
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Pagar com Cartão"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "💳 CONFIRMAR PAGAMENTO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebitCardPaymentScreen(
    comandaNumero: Long,
    onBackClick: () -> Unit,
    onPaymentCompleted: () -> Unit
) {
    val context = LocalContext.current
    var comanda by remember { mutableStateOf<Comanda?>(null) }
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    LaunchedEffect(comandaNumero) {
        comanda = ComandaManager.getComandaPorNumero(comandaNumero)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "💳 PAGAMENTO - CARTÃO DÉBITO",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
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
                .padding(16.dp)
        ) {
            comanda?.let { cmd ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "💰 VALOR A PAGAR",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ ${"%.2f".format(cmd.total)}",
                            color = Color(0xFF4CAF50),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Número do Cartão", color = Color.Gray) },
                    placeholder = { Text("1234 5678 9012 3456", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D2D2D),
                        unfocusedContainerColor = Color(0xFF2D2D2D),
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Nome no Cartão", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D2D2D),
                        unfocusedContainerColor = Color(0xFF2D2D2D),
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Validade", color = Color.Gray) },
                        placeholder = { Text("MM/AA", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF2D2D2D),
                            unfocusedContainerColor = Color(0xFF2D2D2D),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV", color = Color.Gray) },
                        placeholder = { Text("123", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF2D2D2D),
                            unfocusedContainerColor = Color(0xFF2D2D2D),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "💳 PAGAMENTO À VISTA",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Desconto de 5% aplicado!",
                            color = Color(0xFF4CAF50),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total com desconto:",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "R$ ${"%.2f".format(cmd.total * 0.95)}",
                                color = Color(0xFF4CAF50),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val resultado = PdfGenerator.generateComandaPdf(
                            context, cmd, "Cartão Débito")

                        ComandaManager.finalizarComanda(cmd.numero)

                        Toast.makeText(context,
                            "Pagamento no cartão de débito aprovado! Desconto de 5% aplicado.",
                            Toast.LENGTH_LONG).show()

                        if (!resultado.contains("Erro")) {
                            Toast.makeText(context, "PDF gerado automaticamente!",
                                Toast.LENGTH_LONG).show()
                        }

                        onPaymentCompleted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = cardNumber.length >= 16 && cardName.isNotEmpty() &&
                            expiryDate.length >= 5 && cvv.length >= 3
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Pagar com Cartão"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "💳 CONFIRMAR PAGAMENTO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixPaymentScreen(
    comandaNumero: Long,
    onBackClick: () -> Unit,
    onPaymentCompleted: () -> Unit
) {
    val context = LocalContext.current
    var comanda by remember { mutableStateOf<Comanda?>(null) }
    var paymentConfirmed by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(300) // 5 minutos em segundos
    }

    LaunchedEffect(comandaNumero) {
        comanda = ComandaManager.getComandaPorNumero(comandaNumero)
    }

    LaunchedEffect(countdown) {
        if (countdown > 0 && !paymentConfirmed) {
            kotlinx.coroutines.delay(1000L)
            countdown--
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "📱 PAGAMENTO PIX",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
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
                .padding(16.dp)
        ) {
            comanda?.let { cmd ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "💰 VALOR A PAGAR",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ ${"%.2f".format(cmd.total)}",
                            color = Color(0xFF4CAF50),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!paymentConfirmed) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E1E)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📱 ESCANEIE O QR CODE",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .size(250.dp)
                                    .background(Color.White, MaterialTheme.shapes.medium),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.silenciopz_qrcode),
                                    contentDescription = "QR Code PIX",
                                    modifier = Modifier
                                        .size(220.dp)
                                        .padding(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Chave PIX: silencio@pz.com.br",
                                color = Color(0xFFFF9800),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val minutes = countdown / 60
                            val seconds = countdown % 60
                            Text(
                                text = "⏰ TEMPO RESTANTE: ${minutes}:${"%02d".format(seconds)}",
                                color = if (countdown > 60) Color.White else Color(0xFFF44336),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { paymentConfirmed = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Simular Pagamento"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "🔓 SIMULAR PAGAMENTO CONCLUÍDO",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E1E)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Pagamento Confirmado",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(80.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "✅ PAGAMENTO CONFIRMADO!",
                                color = Color(0xFF4CAF50),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "PIX de R$ ${"%.2f".format(cmd.total)}",
                                color = Color.White,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val resultado = PdfGenerator.generateComandaPdf(
                                        context, cmd, "PIX")

                                    ComandaManager.finalizarComanda(cmd.numero)

                                    Toast.makeText(context,
                                        "Pagamento PIX confirmado com sucesso!",
                                        Toast.LENGTH_LONG).show()

                                    if (!resultado.contains("Erro")) {
                                        Toast.makeText(context, "PDF gerado automaticamente!",
                                            Toast.LENGTH_LONG).show()
                                    }

                                    onPaymentCompleted()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                Text(
                                    "🎉 FINALIZAR PEDIDO",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📋 INSTRUÇÕES PIX",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Abra seu app bancário",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "• Toque em 'Pagar com PIX'",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "• Escaneie o QR Code acima",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "• Confirme o pagamento",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}