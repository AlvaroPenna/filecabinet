package com.filecabinet.filecabinet.controllers;

import java.util.List;

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
import com.filecabinet.filecabinet.dto.EmpleadoDto;
import com.filecabinet.filecabinet.service.EmpleadoService;

/**
 * Controlador REST para gestionar las operaciones CRUD de la entidad Trabajador.
 * Todas las operaciones están securizadas y filtradas por el ID del usuario autenticado.
 */
@RestController
@RequestMapping("/api/trabajadores")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService trabajadorService) {
        this.empleadoService = trabajadorService;
    }

    /**
     * Obtiene todos los trabajadores asociados al usuario autenticado.
     */
    @GetMapping
    public List<EmpleadoDto> getAllTrabajadores(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        // Llama al servicio para obtener solo los trabajadores del usuario.
        return empleadoService.getAllTrabajores(userId);
    }

    /**
     * Obtiene un trabajador específico por ID, verificando que pertenezca al usuario autenticado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDto> getTrabajadorById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        return empleadoService.getTrabajadorById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo trabajador y lo asocia automáticamente al usuario autenticado.
     */
    @PostMapping
    public ResponseEntity<EmpleadoDto> createTrabajador(
            @RequestBody EmpleadoDto trabajadorDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        EmpleadoDto newTrabajador = empleadoService.createTrabajador(trabajadorDto, userId);
        return ResponseEntity.ok(newTrabajador);
    }

    /**
     * Actualiza un trabajador existente, verificando que pertenezca al usuario autenticado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDto> updateTrabajador(
            @PathVariable Long id,
            @RequestBody EmpleadoDto trabajadorDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        return empleadoService.updateTrabajador(id, userId, trabajadorDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un trabajador existente, verificando que pertenezca al usuario autenticado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrabajador(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        // Se asume que el método deleteTrabajador del servicio también verifica el userId.
        if (empleadoService.deleteTrabajador(id, userId)) {
            return ResponseEntity.ok().build();
        } else {
            // Retorna 404 si no se encuentra (o si no pertenece al usuario)
            return ResponseEntity.notFound().build();
        }
    }
}