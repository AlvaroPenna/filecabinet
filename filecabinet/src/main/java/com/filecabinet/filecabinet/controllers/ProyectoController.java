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

    // Inyección de dependencia
    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    // --- 1. OBTENER TODOS LOS PROYECTOS POR USUARIO (READ ALL) ---
    @GetMapping
    public ResponseEntity<List<ProyectoDto>> getAllProyectos(
            @PathVariable Long userId) {
        
        List<ProyectoDto> proyectos = proyectoService.getAllProyectos(userId);
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/proyectoId")
    public ResponseEntity<ProyectoDto> getProyectoById(
            @PathVariable Long userId,
            @PathVariable Long proyectoId) {

        Optional<ProyectoDto> dto = proyectoService.getProyectoById(proyectoId, userId);
        
        // Retorna 200 OK si lo encuentra, o 404 NOT FOUND si no existe o no pertenece al usuario.
        return dto.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProyectoDto> createProyecto(
            @RequestBody ProyectoDto proyectoDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long userId = userDetails.getUserId();
        ProyectoDto createdDto = proyectoService.createProyecto(proyectoDto, userId);
        return ResponseEntity.ok(createdDto);
    }

    // --- 4. ACTUALIZAR UN PROYECTO EXISTENTE (UPDATE) ---
    @PutMapping("/{proyectoId}")
    public ResponseEntity<ProyectoDto> updateProyecto(
            @PathVariable Long userId,
            @PathVariable Long proyectoId,
            @RequestBody ProyectoDto proyectoDetails) {
        
        // **Validación de ID (Recomendado):** Asegurarse de que el DTO incluye el ID si es necesario para el service
        // Aunque el service lo maneja con los path variables, es buena práctica de consistencia.
        if (proyectoDetails.getId() != null && !proyectoDetails.getId().equals(proyectoId)) {
            // Retorna 400 Bad Request si el ID del cuerpo no coincide con el ID de la URI
            return ResponseEntity.badRequest().build(); 
        }

        Optional<ProyectoDto> updatedDto = proyectoService.updateProyecto(
            proyectoId, userId, proyectoDetails);

        // Retorna 200 OK si actualiza, o 404 NOT FOUND si no lo encuentra.
        return updatedDto.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- 5. ELIMINAR UN PROYECTO (DELETE) ---
    @DeleteMapping("/{proyectoId}")
    public ResponseEntity<Void> deleteProyecto(
            @PathVariable Long userId,
            @PathVariable Long proyectoId) {

        boolean isDeleted = proyectoService.deleteProyecto(proyectoId, userId);

        if (isDeleted) {
            // Retorna 204 No Content (éxito en la eliminación)
            return ResponseEntity.noContent().build(); 
        } else {
            // Retorna 404 Not Found si el recurso no existe o no pertenece al usuario.
            return ResponseEntity.notFound().build(); 
        }
    }
}