package com.filecabinet.filecabinet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {

    private Long id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private String domicilio;
    private String codigoPostal;
    private String provincia;
    private String poblacion;
    private String contraseña;
    private boolean activo;
}