package com.filecabinet.filecabinet.controllers;

import com.filecabinet.filecabinet.config.CustomUserDetails;
import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.filecabinet.filecabinet.dto.PresupuestoDto;
import com.filecabinet.filecabinet.service.PresupuestoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping
    public List<PresupuestoDto> getAllPresupuestos() {
        return presupuestoService.getAllPresupuestos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDto> getPresupuestoById(@PathVariable Long id) {
        return presupuestoService.getPresupuestoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PresupuestoDto> createPresupuesto(
        @RequestBody PresupuestoDto presupuestoDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
            long userId = userDetails.getUserId();
            PresupuestoDto newPresupuesto = presupuestoService.createPresupuesto(presupuestoDto, userId);
            return new ResponseEntity<>(newPresupuesto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresupuestoDto> updatePresupuesto(@PathVariable Long id, @Valid @RequestBody PresupuestoDto presupuestoDto) {
        return presupuestoService.updatePresupuesto(id, presupuestoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePresupuesto(@PathVariable Long id) {
        if (presupuestoService.deletePresupuesto(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{presupuestoId}/detalles")
    public ResponseEntity<PresupuestoDto> addDetalle(
            @PathVariable Long presupuestoId,
            @Valid @RequestBody DetalleDocumentoDto detalleDto) {

        return presupuestoService.addDetalle(presupuestoId, detalleDto)
                .map(presupuestoDto -> new ResponseEntity<>(presupuestoDto, HttpStatus.CREATED))
                .orElse(ResponseEntity.notFound().build());
    }

}
