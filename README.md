🍔 LANCHES DO SILÊNCIO - SISTEMA DE GESTÃO PARA LANCHONETE

APP DEFINITIVO PARA CONTROLE DE PEDIDOS, COMANDAS E PAGAMENTOS!

(Porque gerenciar lanchonete não é brincadeira - é eficiência e organização! 💰📊)

---------------------------------------------------------------------------------------------
🎯 FUNCIONALIDADES PRINCIPAIS
---------------------------------------------------------------------------------------------

📋 SISTEMA DE CARDÁPIO INTERATIVO

    Menu completo com produtos, descrições e preços

    Seleção por quantidade com interface intuitiva

    Cálculo automático de totais em tempo real

    Modo de adição a comandas existentes

---------------------------------------------------------------------------------------------
⚖️ GESTÃO DE COMANDAS AVANÇADA

    Criação e controle de múltiplas comandas simultâneas

    Adição de itens a comandas existentes

    Visualização em tempo real de pedidos pendentes

    Agrupamento inteligente de itens repetidos

---------------------------------------------------------------------------------------------
💳 SISTEMA DE PAGAMENTOS COMPLETO SIMULADO

    Múltiplas formas de pagamento:

        💵 Dinheiro (com cálculo automático de troco)

        💳 Cartão de Crédito (com parcelamento)

        💳 Cartão de Débito (com desconto automático de 5%)

        📱 PIX (com QR Code e simulação de pagamento)

    Geração automática de PDF para cada venda

    Comprovantes detalhados com todos os itens
    
---------------------------------------------------------------------------------------------
📊 RELATÓRIOS E CONTROLE

    Dashboard de comandas ativas

    Total em vendas em tempo real

    Contagem de itens vendidos

    Histórico completo de pedidos

---------------------------------------------------------------------------------------------
🛠️ TECNOLOGIAS UTILIZADAS
---------------------------------------------------------------------------------------------

Componente	     Tecnologia

Linguagem	       Kotlin 100%

UI Framework	   Jetpack Compose

Design           System	Material 3

Arquitetura	     MVVM com Estado Compose

Geração de PDF	 iText 7

Armazenamento	   MediaStore (Downloads)

Compatibilidade	 Android 7.0+ (API 24)

---------------------------------------------------------------------------------------------
🗂️ ESTRUTURA DO PROJETO

LanchesDoSilencio/

├── app/

│   ├── src/main/java/com/example/lanchesdosilencio/

│   │   ├── screens/                 # Telas principais

│   │   │   ├── MenuScreen.kt        # Tela do cardápio

│   │   │   ├── OrderScreen.kt       # Tela de preparo de pedidos

│   │   │   ├── ComandasListScreen.kt # Lista de comandas

│   │   │   └── PaymentScreen.kt     # Sistema de pagamentos

│   │   ├── models/                  # Modelos de dados

│   │   │   ├── Menu.kt              # Modelo do cardápio

│   │   │   ├── Product.kt           # Modelo de produto

│   │   │   ├── Comanda.kt           # Modelo de comanda

│   │   │   └── ComandaManager.kt    # Gerenciador de comandas

│   │   ├── utils/                   # Utilitários

│   │   │   └── PdfGenerator.kt      # Gerador de PDF

│   │   └── MainActivity.kt          # Ponto de entrada

├── build.gradle.kts                 # Configurações do projeto

└── gradle.properties                # Propriedades do build

---------------------------------------------------------------------------------------------
📊 MODELOS DE DADOS PRINCIPAIS
---------------------------------------------------------------------------------------------

🍔 Product (Produto)

data class Product(
    val id: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val imagemResId: Int,
    var quantidade: Int
)

---------------------------------------------------------------------------------------------
📋 Comanda (Pedido)

data class Comanda(
    val numero: Long,
    val itens: List<Product>,
    val total: Double,
    val dataCriacao: Date,
    var status: ComandaStatus
)

---------------------------------------------------------------------------------------------
💳 Sistema de Pagamento

enum class PaymentScreenState {
    SELECT_METHOD,
    CASH_PAYMENT,
    CREDIT_CARD_PAYMENT,
    DEBIT_CARD_PAYMENT,
    PIX_PAYMENT
}

---------------------------------------------------------------------------------------------
🎨 DESIGN E UX
🎯 Princípios de Design

    Tema escuro profissional - ideal para ambientes de lanchonete

    Cores semânticas - verde para sucesso, vermelho para alertas

    Tipografia hierárquica - fácil leitura durante o rush

    Layout responsivo - adaptável a diferentes tamanhos de tela

---------------------------------------------------------------------------------------------
✨ Componentes Personalizados

    Especificação produtos com controle de quantidade

    Interface de comandas com agrupamento inteligente

    Sistema de pagamento com fluxo intuitivo

    Geração automática de PDF para controle

---------------------------------------------------------------------------------------------
⚙️ CONFIGURAÇÃO TÉCNICA
📋 Versões Críticas

    Gradle: 8.12

    JDK: 11 (Requisito do Kotlin)

    Android Studio: Electric Eel ou superior

    Min SDK: 24 (Android 7.0)

    Target SDK: 36 (Android 14)

    Kotlin: 1.9.0+

---------------------------------------------------------------------------------------------
🔧 Dependências Principais

// Jetpack Compose
implementation(libs.androidx.activity.compose)
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.material3)

// Geração de PDF
implementation("com.itextpdf:itext7-core:7.2.5")

// AndroidX Core
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)

---------------------------------------------------------------------------------------------
🚀 COMO EXECUTAR
---------------------------------------------------------------------------------------------

Terminal (Linux/macOS):
bash

git clone https://github.com/SilencioPz/LanchesDoSilencio.git
cd LanchesDoSilencio
./gradlew assembleDebug

---------------------------------------------------------------------------------------------
PowerShell (Windows):

git clone https://github.com/SilencioPz/LanchesDoSilencio.git
cd LanchesDoSilencio
.\gradlew.bat assembleDebug

---------------------------------------------------------------------------------------------
Android Studio:

    Abra o projeto

    Build > Make Project

    Execute no emulador (Recomendado: Pixel 5 com API 34+)

---------------------------------------------------------------------------------------------
🎯 PÚBLICO-ALVO

    🍔 Donos de lanchonete que precisam de controle eficiente

    👨‍🍳 Funcionários que atendem no balcão

    💼 Gerentes que precisam de relatórios de vendas

    📱 Usuários mobile que valorizam praticidade

---------------------------------------------------------------------------------------------
📄 LICENÇA

Open-source sob GNU GPL v3.0

    Use, modifique e contribua

    Mantenha o código livre para todos

    Derivações devem usar a mesma licença

---------------------------------------------------------------------------------------------
👨‍💻 DESENVOLVIMENTO

Feito com café ☕ e paixão pelos "podrões" em Rondonópolis/MT! 😁

"Porque gerenciar uma lanchonete deveria ser simples e eficiente, não complicado!" 🍔✨

Versão 1.0 - Sistema completo de gestão para lanchonetes 📊🎯
