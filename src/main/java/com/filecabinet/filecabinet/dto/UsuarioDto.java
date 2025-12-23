package com.filecabinet.filecabinet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.filecabinet.filecabinet.enums.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellidos;

    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    private String domicilio;
    private String codigoPostal;
    private String provincia;
    private String poblacion;

    // SEGURIDAD: WRITE_ONLY asegura que este campo se pueda recibir en un POST/PUT
    // pero NUNCA se enviará de vuelta en un GET (JSON response).
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contraseña;

    private boolean activo;
    
    private Rol rol;
}