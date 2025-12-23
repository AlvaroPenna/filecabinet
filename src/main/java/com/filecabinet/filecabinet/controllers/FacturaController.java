package com.filecabinet.filecabinet.controllers;

import com.filecabinet.filecabinet.config.CustomUserDetails;
import com.filecabinet.filecabinet.dto.FacturaDto;
import com.filecabinet.filecabinet.service.FacturaService;
import com.filecabinet.filecabinet.service.PdfGeneratorService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final PdfGeneratorService pdfGeneratorService;

    public FacturaController(FacturaService facturaService, PdfGeneratorService pdfGeneratorService) {
        this.facturaService = facturaService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping
    public List<FacturaDto> getAllFacturas() {
        return facturaService.getAllFacturas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDto> getFacturaById(@PathVariable Long id) {
        return facturaService.getFacturaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FacturaDto> createFactura(
            @RequestBody FacturaDto facturaDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        FacturaDto newFactura = facturaService.createFactura(facturaDto, userId);
        return new ResponseEntity<>(newFactura, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<FacturaDto> updateFactura(@PathVariable Long id, @RequestBody FacturaDto facturaDto) {
        return facturaService.updateFactura(id, facturaDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable Long id) {
        if (facturaService.deleteFactura(id)) {
            // Retorna 204 No Content
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarFacturaPdf(@PathVariable Long id) {

        // 1. Obtener los datos de la factura (Tu servicio existente)
        // Nota: Asegúrate de que getFacturaById devuelva el DTO, no el Optional directo
        FacturaDto facturaDto = facturaService.getFacturaById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // 2. Generar el PDF en bytes
        byte[] pdfBytes = pdfGeneratorService.exportFacturaPdf(facturaDto);

        // 3. Configurar cabeceras para la descarga
        HttpHeaders headers = new HttpHeaders();
        // Esto hace que el navegador lo descargue con ese nombre:
        headers.setContentDispositionFormData("attachment", "Factura_" + facturaDto.getNumFactura() + ".pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/exportar-excel/{id}")
    public void exportarFacturaExcel(@PathVariable Long id, 
                                     HttpServletResponse response,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        facturaService.generarExcel(id, response, userDetails);
    }

}