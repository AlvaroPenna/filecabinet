package com.filecabinet.filecabinet.util;

import com.filecabinet.filecabinet.entidades.DetalleDocumento;
import com.filecabinet.filecabinet.entidades.Factura;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class FacturaExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private Factura factura;

    // Color corporativo
    private static final XSSFColor COLOR_AZUL_RAP = new XSSFColor(new java.awt.Color(26, 68, 126),
            new DefaultIndexedColorMap());

    // Estilos
    private XSSFCellStyle styleHeaderTabla;
    private XSSFCellStyle styleDatos;
    private XSSFCellStyle styleCurrency;
    private XSSFCellStyle styleBold;
    private XSSFCellStyle styleTotalLabel;
    private XSSFCellStyle styleTotalGrande;
    private XSSFCellStyle styleBoldBig;
    private XSSFCellStyle styleDatosCenter;
    private XSSFCellStyle styleCurrencyCenter;

    private XSSFFont fontBold;
    private XSSFFont fontBig;
    private XSSFFont fontNormal;

    public FacturaExcelExporter(Factura factura) {
        this.factura = factura;
        workbook = new XSSFWorkbook();
    }

    private void initStyles() {
        fontBold = workbook.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeightInPoints((short) 11);

        fontBig = workbook.createFont();
        fontBig.setBold(true);
        fontBig.setFontHeightInPoints((short) 13);

        fontNormal = workbook.createFont();
        fontNormal.setFontHeightInPoints((short) 11);

        styleBoldBig = workbook.createCellStyle();
        styleBoldBig.setFont(fontBig);
        styleBoldBig.setVerticalAlignment(VerticalAlignment.TOP);
        styleBoldBig.setAlignment(HorizontalAlignment.LEFT);
        styleBoldBig.setWrapText(true);

        // Estilo Negrita (Con ajuste de texto, para cabeceras generales)
        styleBold = workbook.createCellStyle();
        styleBold.setFont(fontBold);
        styleBold.setVerticalAlignment(VerticalAlignment.TOP);
        styleBold.setAlignment(HorizontalAlignment.LEFT);
        styleBold.setWrapText(true);

        // NUEVO: Estilo Negrita SIN ajuste de texto (Para los totales: Base Imponible,
        // etc.)
        // Esto evita que se corte o cree espacios en blanco feos.
        styleTotalLabel = workbook.createCellStyle();
        styleTotalLabel.setFont(fontBold);
        styleTotalLabel.setVerticalAlignment(VerticalAlignment.TOP);
        styleTotalLabel.setAlignment(HorizontalAlignment.LEFT);
        styleTotalLabel.setWrapText(false); // IMPORTANTE: No permite salto de línea

        // Estilo Texto Normal
        styleDatos = workbook.createCellStyle();
        styleDatos.setFont(fontNormal);
        styleDatos.setVerticalAlignment(VerticalAlignment.TOP);
        styleDatos.setAlignment(HorizontalAlignment.LEFT);
        styleDatos.setWrapText(true);

        // Estilo Moneda
        /*styleCurrency = workbook.createCellStyle();
        styleCurrency.setFont(fontNormal);
        styleCurrency.setVerticalAlignment(VerticalAlignment.TOP);
        DataFormat format = workbook.createDataFormat();
        styleCurrency.setDataFormat(format.getFormat("#,##0.00 \"€\""));*/

        // Estilo Moneda Centrado (Para PRECIO y TOTAL)
        styleCurrencyCenter = workbook.createCellStyle();
        styleCurrencyCenter.setFont(fontNormal);
        styleCurrencyCenter.setVerticalAlignment(VerticalAlignment.TOP);
        styleCurrencyCenter.setAlignment(HorizontalAlignment.CENTER); // <--- AQUÍ ESTÁ LA CLAVE
        DataFormat format = workbook.createDataFormat(); // Asegúrate de tener 'format' disponible o créalo
        styleCurrencyCenter.setDataFormat(format.getFormat("#,##0.00 \"€\""));

        // Estilo Header Tabla
        styleHeaderTabla = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeightInPoints((short) 12);
        fontHeader.setColor(IndexedColors.WHITE.getIndex());
        styleHeaderTabla.setFont(fontHeader);
        styleHeaderTabla.setFillForegroundColor(COLOR_AZUL_RAP);
        styleHeaderTabla.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeaderTabla.setAlignment(HorizontalAlignment.CENTER);
        styleHeaderTabla.setVerticalAlignment(VerticalAlignment.CENTER);

        // Estilo Total Grande
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setBold(true);
        fontTotal.setFontHeightInPoints((short) 14);
        fontTotal.setColor(COLOR_AZUL_RAP);

        styleTotalGrande = workbook.createCellStyle();
        styleTotalGrande.setFont(fontTotal);
        styleTotalGrande.setDataFormat(format.getFormat("#,##0.00 \"€\""));
        styleTotalGrande.setAlignment(HorizontalAlignment.RIGHT);

        styleDatosCenter = workbook.createCellStyle();
        styleDatosCenter.setFont(fontNormal);
        styleDatosCenter.setVerticalAlignment(VerticalAlignment.TOP);
        styleDatosCenter.setAlignment(HorizontalAlignment.CENTER); // <--- AQUÍ ESTÁ LA CLAVE
        styleDatosCenter.setWrapText(true);

    }

    private void insertLogo() {
        try {
            // 1. Cargar la imagen (Tu código de carga parece correcto, lo mantenemos)
            InputStream is = getClass().getClassLoader().getResourceAsStream("static/imagenes/logo.png");
            if (is == null)
                is = getClass().getClassLoader().getResourceAsStream("static/imagenes/logo.png");
            if (is == null)
                is = getClass().getClassLoader().getResourceAsStream("logo.png");

            if (is != null) {
                byte[] bytes = IOUtils.toByteArray(is);
                // Es importante usar el tipo correcto. Si tu logo es JPG, cambia a
                // PICTURE_TYPE_JPEG
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                is.close();

                Drawing<?> drawing = sheet.createDrawingPatriarch();

                // Usamos XSSFClientAnchor para asegurarnos de que funciona bien en .xlsx
                XSSFClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();

                // --- POSICIONAMIENTO EXACTO (ESTIRAR PARA ENCAJAR) ---

                // ESQUINA SUPERIOR IZQUIERDA: Inicio de Columna 0 (A), Fila 0
                anchor.setCol1(0); // Columna 0 (A)
                anchor.setRow1(0); // Fila 0
                anchor.setDx1(0); // Pegado al borde exacto
                anchor.setDy1(0);

                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize();
                pict.resize(0.39);

            }
        } catch (Exception e) {
            System.err.println("Excepción al cargar logo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Factura");
        initStyles();

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);

        sheet.setAutobreaks(true); // Permite saltos de página automáticos
        printSetup.setFitWidth((short) 1); // Ajustar ancho a 1 página
        printSetup.setFitHeight((short) 0);

        sheet.setMargin(Sheet.RightMargin, 0.4);
        sheet.setMargin(Sheet.LeftMargin, 0.4);

        // --- COLUMNAS REAJUSTADAS ---
        // Ampliamos la columna 2 para que "BASE IMPONIBLE" quepa sobrada
        sheet.setColumnWidth(0, 12000); // Descripción
        sheet.setColumnWidth(1, 3000); // Cantidad
        sheet.setColumnWidth(2, 4500); // Precio / Etiquetas (Ampliado)
        sheet.setColumnWidth(3, 4500); // Total

        sheet.addMergedRegion(new CellRangeAddress(0, 8, 0, 0));

        insertLogo();

        // --- DATOS FACTURA (Derecha) ---

        // Fila 0: Factura Nº
        Row row0 = getOrCreateRow(2);
        createCell(row0, 2, "FACTURA Nº:", styleBoldBig);
        createCell(row0, 3, (factura.getNumFactura() != null ? factura.getNumFactura() : "---"), styleBoldBig);

        // Fila 1: Fecha
        Row row1 = getOrCreateRow(3);
        createCell(row1, 2, "FECHA:", styleBoldBig);

        String fechaTexto = "---";
        if (factura.getFechaEmision() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fechaTexto = sdf.format(factura.getFechaEmision());
        }
        createCell(row1, 3, fechaTexto, styleBoldBig);

        // --- BLOQUE EMPRESA (Izq) y CLIENTE (Der) ---
        int rowDataIdx = 10;

        // Fila 11: Empresa | Cliente Título
        Row row11 = getOrCreateRow(rowDataIdx);
        createCell(row11, 0, "REFORMAS ANTONIO PEÑA S.L.U", styleBold);
        createCell(row11, 2, "CLIENTE:", styleBold);

        // Datos cliente
        String nombreCliente = "Cliente Genérico";
        String cifCliente = "";
        String direccionCliente = "";

        if (factura.getCliente() != null) {
            if (factura.getCliente().getNombre() != null) {
                nombreCliente = factura.getCliente().getNombre() + " " +
                        (factura.getCliente().getApellidos() != null ? factura.getCliente().getApellidos() : "");
            }
            if (factura.getCliente().getCif() != null)
                cifCliente = factura.getCliente().getCif();
            if (factura.getCliente().getDireccion() != null)
                direccionCliente = factura.getCliente().getDireccion();
        }

        // Fila 12: CIF Empresa | NOMBRE CLIENTE
        Row row12 = getOrCreateRow(rowDataIdx + 1);
        createCell(row12, 0, "CIF: B-56614191", styleDatos);
        createCell(row12, 2, nombreCliente, styleDatos);
        sheet.addMergedRegion(new CellRangeAddress(rowDataIdx + 1, rowDataIdx + 1, 2, 3));

        // Fila 13: Dir Empresa | DIRECCIÓN CLIENTE
        Row row13 = getOrCreateRow(rowDataIdx + 2);
        createCell(row13, 0, "C/ Camino de los talleres, 14-1ºB.", styleDatos);
        createCell(row13, 2, direccionCliente, styleDatos);
        sheet.addMergedRegion(new CellRangeAddress(rowDataIdx + 2, rowDataIdx + 2, 2, 3));

        // Fila 14: Ciudad Empresa | CIF CLIENTE (FUSIONADO AHORA)
        Row row14 = getOrCreateRow(rowDataIdx + 3);
        createCell(row14, 0, "28031-MADRID (MADRID)", styleDatos);

        // CAMBIO: Ahora el CIF del cliente sale todo en una celda fusionada
        createCell(row14, 2, "CIF: " + cifCliente, styleDatos);
        sheet.addMergedRegion(new CellRangeAddress(rowDataIdx + 3, rowDataIdx + 3, 2, 3));
    }

    private Row getOrCreateRow(int index) {
        Row row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return row;
    }

    private void writeTableHeaders() {
        int headerRowIdx = 16;
        Row row = getOrCreateRow(headerRowIdx);
        row.setHeightInPoints(25);

        createCell(row, 0, "CONCEPTO / DESCRIPCIÓN", styleHeaderTabla);
        createCell(row, 1, "CANT.", styleHeaderTabla);
        createCell(row, 2, "PRECIO U.", styleHeaderTabla);
        createCell(row, 3, "TOTAL", styleHeaderTabla);
    }

    private void writeDataLines() {
        int rowCount = 17;

        if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
            for (DetalleDocumento detalle : factura.getDetalles()) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;

                // --- COLUMNA 0: TRABAJO + DESCRIPCIÓN ---
                Cell cellDesc = row.createCell(columnCount++);

                String trabajo = (detalle.getTrabajo() != null ? detalle.getTrabajo() : "");
                String descripcion = (detalle.getDescripcion() != null ? detalle.getDescripcion() : "");

                String textoCompleto = trabajo;
                if (!descripcion.isEmpty()) {
                    textoCompleto += "\n" + descripcion;
                }

                XSSFRichTextString richString = new XSSFRichTextString(textoCompleto);
                if (trabajo.length() > 0) {
                    richString.applyFont(0, trabajo.length(), fontBold);
                }
                if (descripcion.length() > 0) {
                    int startIndex = trabajo.length();
                    if (textoCompleto.contains("\n"))
                        startIndex++;
                    if (startIndex < textoCompleto.length()) {
                        richString.applyFont(startIndex, textoCompleto.length(), fontNormal);
                    }
                }
                cellDesc.setCellValue(richString);
                cellDesc.setCellStyle(styleDatos);

                double cantidad = (detalle.getCantidad() != null) ? detalle.getCantidad() : 0.0;
                double precio = (detalle.getPrecioUnitario() != null) ? detalle.getPrecioUnitario().doubleValue() : 0.0;
                double subtotal = (detalle.getSubTotal() != null) ? detalle.getSubTotal().doubleValue() : 0.0;

                createCell(row, columnCount++, cantidad, styleDatosCenter);
                createCell(row, columnCount++, precio, styleCurrencyCenter);
                createCell(row, columnCount++, subtotal, styleCurrencyCenter);
            }

            double descuento = (factura.getDescuento() != null) ? factura.getDescuento().doubleValue() : 0.0;
            if (descuento > 0) {
                Row row = sheet.createRow(rowCount++); // Usamos el mismo contador para seguir abajo
                int columnCount = 0;

                // Columna 0: Título "Descuento"
                Cell cellDesc = row.createCell(columnCount++);
                XSSFRichTextString richString = new XSSFRichTextString("Descuento");
                richString.applyFont(fontBold); // Lo ponemos en negrita para diferenciarlo
                cellDesc.setCellValue(richString);
                cellDesc.setCellStyle(styleDatos);

                // Columna 1: Cantidad (Vacía visualmente)
                createCell(row, columnCount++, "", styleDatos);

                // Columna 2: Precio Unitario (Vacía visualmente)
                createCell(row, columnCount++, "", styleCurrency);

                // Columna 3: El Total restando (Importante el signo menos "-")
                createCell(row, columnCount++, -descuento, styleCurrencyCenter);
            }

        } else {
            Row row = sheet.createRow(rowCount++);
            createCell(row, 0, "Sin conceptos", styleDatos);
        }

        // --- PIE DE PÁGINA ---
        int filaMinimaPie = 39;

        if (rowCount < filaMinimaPie) {
            // ESCENARIO A: Pocos productos (ej: terminamos en fila 20).
            // Saltamos directamente a la fila 42 para que el pie quede abajo estéticamente.
            rowCount = filaMinimaPie;
        } else {
            // ESCENARIO B: Muchos productos (ej: terminamos en fila 50).
            // NO volvemos a la 42 (porque sobrescribiríamos datos).
            // Simplemente dejamos un par de líneas de separación y escribimos a continuación.
            // Excel creará la página 2 automáticamente gracias al printSetup.setFitHeight(0).
            rowCount += 2; 
        }

        // --- A PARTIR DE AQUÍ IMPRIMIMOS EL PIE NORMALMENTE ---

        // Forma de Pago
        Row rowPago = sheet.createRow(rowCount);
        createCell(rowPago, 0, "Modo de pago: TRANSFERENCIA BANCARIA", styleBold);
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 1));

        // IBAN
        Row rowIban = sheet.createRow(rowCount + 1);
        createCell(rowIban, 0, "Nº de cuenta: ES86 XXXX XXXX XXXX XXXX", styleDatos);
        sheet.addMergedRegion(new CellRangeAddress(rowCount + 1, rowCount + 1, 0, 1));

        // Concepto
        Row rowConcepto = sheet.createRow(rowCount + 2);
        createCell(rowConcepto,0,"Rogamos indiquen en el concepto: Factura: " + factura.getNumFactura(), styleBold);
        sheet.addMergedRegion(new CellRangeAddress(rowCount + 2, rowCount + 2, 0, 1));

        // --- TOTALES ---

        // Base Imponible
        createCell(rowPago, 2, "BASE IMPONIBLE:", styleTotalLabel);
        createCell(rowPago, 3, getSafeDouble(factura.getTotal_bruto()), styleCurrencyCenter);

        // IVA
        Row rowIva = getOrCreateRow(rowCount + 1);
        createCell(rowIva, 2, "I.V.A. " + factura.getTipo_iva() + "%:", styleTotalLabel);
        createCell(rowIva, 3, getSafeDouble(factura.getTotal_iva()), styleCurrencyCenter);

        // Total Final
        Row rowTotal = getOrCreateRow(rowCount + 2);
        Cell cellTituloTotal = rowTotal.createCell(2);
        cellTituloTotal.setCellValue("TOTAL NETO:");

        XSSFCellStyle styleTituloTotal = workbook.createCellStyle();
        XSSFFont fontTitulo = workbook.createFont();
        fontTitulo.setBold(true);
        fontTitulo.setFontHeightInPoints((short) 14);
        fontTitulo.setColor(COLOR_AZUL_RAP);
        styleTituloTotal.setFont(fontTitulo);
        styleTituloTotal.setAlignment(HorizontalAlignment.RIGHT);
        cellTituloTotal.setCellStyle(styleTituloTotal);

        createCell(rowTotal, 3, getSafeDouble(factura.getTotal_neto()), styleTotalGrande);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    private double getSafeDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.00;
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeTableHeaders();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}