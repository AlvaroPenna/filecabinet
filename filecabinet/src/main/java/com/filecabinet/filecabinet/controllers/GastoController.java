package com.filecabinet.filecabinet.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.filecabinet.filecabinet.config.CustomUserDetails;
import com.filecabinet.filecabinet.dto.GastoDto;
import com.filecabinet.filecabinet.service.GastoService;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final GastoService gastosService;

    public GastoController(GastoService gastosService) {
        this.gastosService = gastosService;
    }

    @GetMapping
    public List<GastoDto> getAllGastos() {
        return gastosService.getAllGastos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoDto> getGastoById(@PathVariable Long id) {
        return gastosService.getGastoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GastoDto> createGasto(
        @RequestBody GastoDto gastoDto, 
        @AuthenticationPrincipal CustomUserDetails userDetails) {
            Long userId = userDetails.getUserId();
            GastoDto newGasto = gastosService.createGasto(gastoDto, userId);
            return ResponseEntity.ok(newGasto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoDto> updateGasto(@PathVariable Long id, @RequestBody GastoDto gastoDetails) {
        return gastosService.updateGasto(id, gastoDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGasto(@PathVariable Long id) {
        if (gastosService.deleteGasto(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}