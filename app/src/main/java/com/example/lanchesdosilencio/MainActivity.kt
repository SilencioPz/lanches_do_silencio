package com.example.lanchesdosilencio

//Gerar tela de Pagamentos!
//Criar tela de impressão via PDF, para controle da loja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.lanchesdosilencio.models.Menu
import com.example.lanchesdosilencio.screens.MenuScreen
import com.example.lanchesdosilencio.ui.theme.LanchesDoSilencioTheme
import com.example.lanchesdosilencio.screens.OrderScreen
import com.example.lanchesdosilencio.screens.ComandasListScreen
import com.example.lanchesdosilencio.screens.PaymentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LanchesDoSilencioTheme {
                var telaAtual by remember { mutableStateOf("menu") }
                var menu by remember { mutableStateOf(Menu()) }
                var pedidoFinalizado by remember { mutableStateOf(false) }
                var comandaParaAdicionar by remember { mutableStateOf<Long?>(null) }
                var comandaParaPagar by remember { mutableStateOf<Long?>(null) }

                when (telaAtual) {
                    "menu" -> {
                        if (pedidoFinalizado) {
                            LaunchedEffect(Unit) {
                                menu = Menu()
                                pedidoFinalizado = false
                            }
                        }
                        MenuScreen(
                            menu = menu,
                            onOrderClick = {
                                telaAtual = "order"
                            },
                            onViewComandas = { telaAtual = "comandas" },
                            comandaParaAdicionar = comandaParaAdicionar
                        )
                    }
                    "order" -> {
                        OrderScreen(
                            menu = menu,
                            onBackClick = {
                                telaAtual = "menu"
                            },
                            onViewComandas = {
                                telaAtual = "comandas"
                            },
                            onPedidoFinalizado = {
                                pedidoFinalizado = true
                                comandaParaAdicionar = null
                                telaAtual = "comandas"
                            },
                            onPaymentClick = { comandaNumero ->
                                comandaParaPagar = comandaNumero
                                telaAtual = "payment"
                            },
                            comandaEspecifica = comandaParaAdicionar
                        )
                    }
                    "comandas" -> {
                        ComandasListScreen(
                            onBackClick = {
                                telaAtual = "menu"
                            },
                            onViewOrder = {
                                comandaParaAdicionar = null
                                telaAtual = "order"
                            },
                            onAddMoreItems = { telaAtual = "menu" },
                            onAdicionarItensComanda = { numeroComanda ->
                                comandaParaAdicionar = numeroComanda
                                telaAtual = "menu"
                            },
                            onPaymentClick = { comandaNumero ->
                                comandaParaPagar = comandaNumero
                                telaAtual = "payment"
                            }
                        )
                    }
                    "payment" -> {
                        comandaParaPagar?.let { numero ->
                            PaymentScreen(
                                comandaNumero = numero,
                                onBackClick = {
                                    telaAtual = "comandas"
                                },
                                onPaymentCompleted = {
                                    comandaParaPagar = null
                                    telaAtual = "comandas"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}