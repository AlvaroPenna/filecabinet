package com.filecabinet.filecabinet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import com.filecabinet.filecabinet.entidades.Usuario;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {

    private Long id;
    private String nombre;
    private String apellidos;
    private String Cif;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private Set<Usuario> usuarios;
    
}