package com.filecabinet.filecabinet.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.filecabinet.filecabinet.dto.UsuarioDto;
import com.filecabinet.filecabinet.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    // 1. GET ALL: Solo permitido para ADMIN
    // Requiere @EnableMethodSecurity en tu SecurityConfig
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") 
    public List<UsuarioDto> getAllUsuarios(){
        return usuarioService.getAllUsuarios();
    }

    // 2. GET BY ID: Permitido si eres ADMIN o si buscas TU PROPIO id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        
        // Verificación de seguridad:
        // Si no es ADMIN y el ID que busca no es el suyo -> 403 Forbidden
        if (!esAdmin(userDetails) && !userDetails.getUserId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return usuarioService.getUsuarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. POST: Registro (Suele ser público)
    @PostMapping
    public ResponseEntity<UsuarioDto> createUsuario(@Valid @RequestBody UsuarioDto usuarioDto){
        // @Valid activa las anotaciones @NotBlank, @Email del DTO
        UsuarioDto newUsuario = usuarioService.createUsuario(usuarioDto);
        // Devolvemos 201 Created en lugar de 200 OK
        return ResponseEntity.status(HttpStatus.CREATED).body(newUsuario);
    }
    
    // 4. PUT: Solo puedes editar TU propio perfil (o un ADMIN puede editar a cualquiera)
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> updateUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody UsuarioDto usuarioDto, // @Valid aquí también
            @AuthenticationPrincipal CustomUserDetails userDetails){
        
        // Seguridad: Solo el dueño o el admin pueden editar
        if (!esAdmin(userDetails) && !userDetails.getUserId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return usuarioService.updateUsuario(id, usuarioDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. DELETE: Solo un ADMIN debería poder borrar usuarios 
    // (o permitir borrarte a ti mismo si es un requisito)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id){
        if(usuarioService.deleteUsuario(id)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    // Método auxiliar para verificar rol (privado en el controlador)
    private boolean esAdmin(CustomUserDetails userDetails) {
        // Asumiendo que tu Enum Rol se llama ADMIN
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")); // O "ROLE_ADMIN" según configures
    }
}