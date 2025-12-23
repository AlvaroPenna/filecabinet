package com.filecabinet.filecabinet.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filecabinet.filecabinet.config.CustomUserDetails;
import com.filecabinet.filecabinet.dto.ProyectoDto;
import com.filecabinet.filecabinet.service.ProyectoService;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public ResponseEntity<List<ProyectoDto>> getAllProyectos(@AuthenticationPrincipal CustomUserDetails userDetails) {
            Long userId = userDetails.getUserId();
            List<ProyectoDto> proyectos = proyectoService.getAllProyectos(userId);
            return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/proyectoId")
    public ResponseEntity<ProyectoDto> getProyectoById(@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long proyectoId) {
                Long userId = userDetails.getUserId();
                Optional<ProyectoDto> dto = proyectoService.getProyectoById(proyectoId, userId);
                return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProyectoDto> createProyecto(@RequestBody ProyectoDto proyectoDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
                Long userId = userDetails.getUserId();
                ProyectoDto createdDto = proyectoService.createProyecto(proyectoDto, userId);
                return ResponseEntity.ok(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDto> updateProyecto(@AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long proyectoId,
        @RequestBody ProyectoDto proyectoDetails) {
            Long userId = userDetails.getUserId();
            Optional<ProyectoDto> updatedDto = proyectoService.updateProyecto(
            proyectoId, userId, proyectoDetails);
            return updatedDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{proyectoId}")
    public ResponseEntity<Void> deleteProyecto(@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long proyectoId) {
                Long userId = userDetails.getUserId();
                boolean isDeleted = proyectoService.deleteProyecto(proyectoId, userId);
                if (isDeleted) {
                    return ResponseEntity.noContent().build(); 
                } else {
                    return ResponseEntity.notFound().build(); 
                }
    }
}