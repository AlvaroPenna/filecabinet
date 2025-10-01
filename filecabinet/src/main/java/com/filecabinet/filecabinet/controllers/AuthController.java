package com.filecabinet.filecabinet.controllers;

import com.filecabinet.filecabinet.dto.LoginRequest;
import com.filecabinet.filecabinet.dto.LoginResponse;
import com.filecabinet.filecabinet.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Base path: /api/auth
public class AuthController {

    private final UsuarioService usuarioService;

    // Inyección de dependencia por constructor (la forma recomendada)
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para el inicio de sesión.
     * Mapea la petición POST a /api/auth/login
     *
     * @param loginRequest DTO con username y password.
     * @return ResponseEntity con el token o un error 401.
     */
    /*@PostMapping("/login") // Ruta completa: /api/auth/login
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // La lógica real de autenticación se delega al servicio
        try {
            LoginResponse response = usuarioService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            );
            // Si tiene éxito, devuelve la respuesta 200 OK con el token
            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            // Si falla la autenticación (e.g., credenciales incorrectas),
            // se devuelve un 401 Unauthorized, tal como espera el código JS.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new LoginResponse(null, "Credenciales inválidas: " + ex.getMessage())
            );
        }
    }*/
}
