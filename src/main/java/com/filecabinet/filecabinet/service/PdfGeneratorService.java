package com.filecabinet.filecabinet.service;

import com.filecabinet.filecabinet.dto.FacturaDto;
import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    public byte[] exportFacturaPdf(FacturaDto factura) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // FUENTES
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            // 1. CABECERA
            Paragraph title = new Paragraph("FACTURA N°: " + factura.getNumFactura(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 2. DATOS DEL CLIENTE (Asumiendo que FacturaDto tiene datos del cliente)
            // Si no los tiene, pásalos como argumento extra al método
            document.add(new Paragraph("Fecha de Emisión: " + factura.getFechaEmision(), fontBody));
            document.add(new Paragraph("Cliente: " + factura.getCliente_id(), fontBody));
            document.add(new Paragraph("CIF/NIF: " + factura.getCliente_id(), fontBody));
            document.add(new Paragraph(" ")); // Espacio en blanco

            // 3. TABLA DE PRODUCTOS (PdfPTable es mucho mejor que Table)
            PdfPTable table = new PdfPTable(4); // 4 Columnas
            table.setWidthPercentage(100);
            // Definir anchos relativos: Descripción más ancha (3), resto (1)
            table.setWidths(new float[]{3.5f, 1f, 1.5f, 1.5f}); 
            table.setSpacingBefore(10);

            // Cabeceras de Tabla
            agregarCabecera(table, "Descripción", fontHeader);
            agregarCabecera(table, "Cant.", fontHeader);
            agregarCabecera(table, "Precio U.", fontHeader);
            agregarCabecera(table, "Total", fontHeader);

            // Filas de Detalles
            NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

            for (DetalleDocumentoDto d : factura.getDetalles()) {
                table.addCell(new Phrase(d.getTrabajo(), fontBody));
                
                PdfPCell cellCant = new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), fontBody));
                cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellCant);

                PdfPCell cellPrecio = new PdfPCell(new Phrase(formatoMoneda.format(d.getPrecioUnitario()), fontBody));
                cellPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellPrecio);

                double subtotal = d.getCantidad() * d.getPrecioUnitario();
                PdfPCell cellSubtotal = new PdfPCell(new Phrase(formatoMoneda.format(subtotal), fontBody));
                cellSubtotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellSubtotal);
            }
            document.add(table);

            // 4. TOTALES (Alineados a la derecha)
            document.add(new Paragraph(" "));
            Paragraph pTotal = new Paragraph();
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            pTotal.add(new Chunk("Total Bruto: " + formatoMoneda.format(factura.getTotal_bruto()) + "\n", fontBody));
            pTotal.add(new Chunk("IVA (21%): " + formatoMoneda.format(factura.getTotal_iva()) + "\n", fontBody));
            pTotal.add(new Chunk("TOTAL NETO: " + formatoMoneda.format(factura.getTotal_neto()), fontTitle));
            document.add(pTotal);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF de la factura");
        }
        return baos.toByteArray();
    }

    // Método auxiliar para estilos de cabecera de tabla
    private void agregarCabecera(PdfPTable table, String texto, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.GRAY);
        header.setPadding(5);
        header.setPhrase(new Phrase(texto, font));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}