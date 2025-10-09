package com.filecabinet.filecabinet.controllers;

import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.filecabinet.filecabinet.dto.FacturaDto;
import com.filecabinet.filecabinet.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
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
    public ResponseEntity<FacturaDto> createFactura(@Valid @RequestBody FacturaDto facturaDto) {
        FacturaDto newFactura = facturaService.createFactura(facturaDto);
        // Retorna 201 Created
        return new ResponseEntity<>(newFactura, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaDto> updateFactura(@PathVariable Long id, @Valid @RequestBody FacturaDto facturaDto) {
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

    // ------------------------------------------------------------------------
    // 2. GESTIÓN ANIDADA DE DETALLES (LÍNEAS DE FACTURA)
    // ------------------------------------------------------------------------

    /**
     * POST /api/facturas/{facturaId}/detalles
     * Añade un nuevo detalle a una factura existente.
     */
    @PostMapping("/{facturaId}/detalles")
    public ResponseEntity<FacturaDto> addDetalle(
            @PathVariable Long facturaId,
            @Valid @RequestBody DetalleDocumentoDto detalleDto) {

        return facturaService.addDetalle(facturaId, detalleDto)
                .map(facturaDto -> new ResponseEntity<>(facturaDto, HttpStatus.CREATED))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/facturas/{facturaId}/detalles/{detalleId}
     * Modifica un detalle específico de una factura.
     * Retorna 404 si la factura no existe o si el detalle no pertenece a esa factura.
     */
    @PutMapping("/{facturaId}/detalles/{detalleId}")
    public ResponseEntity<FacturaDto> updateDetalle(
            @PathVariable Long facturaId,
            @PathVariable Long detalleId,
            @Valid @RequestBody DetalleDocumentoDto detalleDto) {
        
        try {
            return facturaService.updateDetalle(facturaId, detalleId, detalleDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            // Capturar la excepción de "Detalle no encontrado" si la implementaste
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{facturaId}/detalles/{detalleId}")
    public ResponseEntity<Void> deleteDetalle(
            @PathVariable Long facturaId,
            @PathVariable Long detalleId) {

        boolean deleted = facturaService.deleteDetalle(facturaId, detalleId);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}