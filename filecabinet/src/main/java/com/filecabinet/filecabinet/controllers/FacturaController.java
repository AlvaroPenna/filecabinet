package com.filecabinet.filecabinet.controllers;

import com.filecabinet.filecabinet.dto.FacturaDto;
import com.filecabinet.filecabinet.service.FacturaService;
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
    public ResponseEntity<FacturaDto> createFactura(@RequestBody FacturaDto facturaDto) {
        FacturaDto newFactura = facturaService.createFactura(facturaDto);
        // Retorna 201 Created
        return new ResponseEntity<>(newFactura, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaDto> updateFactura(@PathVariable Long id,@RequestBody FacturaDto facturaDto) {
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

}