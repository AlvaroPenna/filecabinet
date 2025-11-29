package com.filecabinet.filecabinet.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filecabinet.filecabinet.dto.ProyectoTrabajadorDto;
import com.filecabinet.filecabinet.service.ProyectoTrabajadorService;

@RestController
@RequestMapping("/api/proyectos-trabajadores")
public class ProyectoTrabajadorController {

    private final ProyectoTrabajadorService proyectoTrabajadorService;

    // Inyección de dependencia a través del constructor
    public ProyectoTrabajadorController(ProyectoTrabajadorService proyectoTrabajadorService) {
        this.proyectoTrabajadorService = proyectoTrabajadorService;
    }

    // --- 1. OBTENER TODOS LOS REGISTROS POR USUARIO (READ ALL) ---
    @GetMapping
    public ResponseEntity<List<ProyectoTrabajadorDto>> getAllProyectoTrabajadores(
            @PathVariable Long userId) {
        
        List<ProyectoTrabajadorDto> dtos = proyectoTrabajadorService.getAllProyectoTrabajador(userId);
        return ResponseEntity.ok(dtos);
    }

    // --- 2. OBTENER UN REGISTRO ESPECÍFICO (READ ONE) ---
    @GetMapping("/{trabajadorId}")
    public ResponseEntity<ProyectoTrabajadorDto> getProyectoTrabajadorById(
            @PathVariable Long userId,
            @PathVariable Long trabajadorId) {

        Optional<ProyectoTrabajadorDto> dto = proyectoTrabajadorService.getTrabajadorById(trabajadorId, userId);
        
        // Retorna 200 OK si lo encuentra, o 404 NOT FOUND si no existe o no pertenece al usuario.
        return dto.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- 3. CREAR UN NUEVO REGISTRO (CREATE) ---
    @PostMapping
    public ResponseEntity<ProyectoTrabajadorDto> createProyectoTrabajador(
            @PathVariable Long userId,
            @RequestBody ProyectoTrabajadorDto proyectoTrabajadorDto) {

        // Nota: En un entorno real, solo se necesita el DTO sin ID para la creación.
        ProyectoTrabajadorDto createdDto = proyectoTrabajadorService.createProyectoTrabajador(proyectoTrabajadorDto, userId);
        
        // Retorna 201 Created
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    // --- 4. ACTUALIZAR UN REGISTRO EXISTENTE (UPDATE) ---
    @PutMapping("/{trabajadorId}")
    public ResponseEntity<ProyectoTrabajadorDto> updateProyectoTrabajador(
            @PathVariable Long userId,
            @PathVariable Long trabajadorId,
            @RequestBody ProyectoTrabajadorDto trabajadorDetails) {
        
        // La actualización requiere que el ID del DTO y el ID de la ruta sean consistentes
        // Aunque el service no usa trabajadorDetails.getId(), lo ideal es la consistencia.
        if (trabajadorDetails.getId() == null || !trabajadorDetails.getId().equals(trabajadorId)) {
             // Retorna 400 Bad Request si el ID del cuerpo no coincide con el ID de la URI
            return ResponseEntity.badRequest().build();
        }

        Optional<ProyectoTrabajadorDto> updatedDto = proyectoTrabajadorService.updateProyectoTrabajador(
            trabajadorId, userId, trabajadorDetails);

        // Retorna 200 OK si actualiza, o 404 NOT FOUND si no lo encuentra.
        return updatedDto.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- 5. ELIMINAR UN REGISTRO (DELETE) ---
    @DeleteMapping("/{trabajadorId}")
    public ResponseEntity<Void> deleteProyectoTrabajador(
            @PathVariable Long userId,
            @PathVariable Long trabajadorId) {

        boolean isDeleted = proyectoTrabajadorService.deleteTrabajador(trabajadorId, userId);

        if (isDeleted) {
            // Retorna 204 No Content (éxito sin cuerpo de respuesta)
            return ResponseEntity.noContent().build(); 
        } else {
            // Retorna 404 Not Found si no existe o no pertenece al usuario.
            return ResponseEntity.notFound().build(); 
        }
    }
}