package com.example.lanchesdosilencio.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.lanchesdosilencio.models.Comanda
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
object PdfGenerator {

    fun generateComandaPdf(context: Context, comanda: Comanda, paymentMethod: String): String {
        return try {
            val timestamp = SimpleDateFormat("ddMMyyyy_HHmmss",
                Locale.getDefault()).format(Date())
            val fileName = "Comanda_${comanda.numero}_$timestamp.pdf"

            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri == null) {
                println("DEBUG - ❌ Falha ao inserir no MediaStore")
                return "❌ Erro: Falha ao criar entrada no MediaStore"
            }

            resolver.openOutputStream(uri)?.use { outputStream ->
                val writer = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(writer)
                val document = Document(pdfDocument)

                document.add(
                    Paragraph("🍔 LANCHES DO SILÊNCIO 🍔")
                        .setBold()
                        .setFontSize(20f)
                        .setTextAlignment(TextAlignment.CENTER)
                )

                document.add(Paragraph(" "))

                document.add(
                    Paragraph("COMPROVANTE DE VENDA")
                        .setBold()
                        .setFontSize(16f)
                        .setTextAlignment(TextAlignment.CENTER)
                )

                document.add(Paragraph(" "))

                val infoTable = Table(UnitValue.createPercentArray(
                    floatArrayOf(40f, 60f)))
                infoTable.setWidth(UnitValue.createPercentValue(100f))

                infoTable.addCell(Paragraph("Comanda:").setBold())
                infoTable.addCell(Paragraph("#${comanda.numero}"))

                infoTable.addCell(Paragraph("Data/Hora:").setBold())
                infoTable.addCell(Paragraph(SimpleDateFormat(
                    "dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())))

                infoTable.addCell(Paragraph("Pagamento:").setBold())
                infoTable.addCell(Paragraph(paymentMethod))

                document.add(infoTable)
                document.add(Paragraph(" "))

                document.add(Paragraph("ITENS DO PEDIDO").setBold().setTextAlignment(
                    TextAlignment.CENTER))
                document.add(Paragraph(" "))

                val table = Table(UnitValue.createPercentArray(
                    floatArrayOf(50f, 20f, 30f)))
                table.setWidth(UnitValue.createPercentValue(100f))

                table.addHeaderCell(Paragraph("Produto").setBold())
                table.addHeaderCell(Paragraph("Qtd").setBold())
                table.addHeaderCell(Paragraph("Valor").setBold())

                val itensAgrupados = comanda.itens
                    .groupingBy { it.id }
                    .eachCount()
                    .map { (id, quantidade) ->
                        val produto = comanda.itens.first { it.id == id }
                        produto to quantidade
                    }

                itensAgrupados.forEach { (produto, quantidade) ->
                    table.addCell(Paragraph(produto.nome))
                    table.addCell(Paragraph(quantidade.toString()))
                    table.addCell(Paragraph("R$ ${"%.2f".format(
                        produto.preco * quantidade)}"))
                }

                document.add(table)
                document.add(Paragraph(" "))

                document.add(
                    Paragraph("TOTAL: R$ ${"%.2f".format(comanda.total)}")
                        .setBold()
                        .setFontSize(16f)
                        .setTextAlignment(TextAlignment.RIGHT)
                )

                document.add(Paragraph(" "))
                document.add(
                    Paragraph("________________________________")
                        .setTextAlignment(TextAlignment.CENTER)
                )

                document.add(Paragraph(" "))
                document.add(
                    Paragraph("Obrigado pela preferência! Volte sempre!")
                        .setItalic()
                        .setTextAlignment(TextAlignment.CENTER)
                )

                document.close()
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            println("DEBUG - ✅ PDF criado com sucesso!")
            println("DEBUG - 📁 Local: Downloads/$fileName")
            "✅ PDF salvo em: Downloads/$fileName"

        } catch (e: Exception) {
            e.printStackTrace()
            println("DEBUG - ❌ Exceção ao criar PDF: ${e.message}")
            "❌ Erro: ${e.message}"
        }
    }
}