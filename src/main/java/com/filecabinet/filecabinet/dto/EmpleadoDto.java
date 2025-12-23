package com.filecabinet.filecabinet.dto;

import com.filecabinet.filecabinet.enums.Categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDto {

    private Long id;
    private String nombre;
    private String apellidos;
    private String nif;
    private String telefono;
    private String email;
    private Categoria categoria;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    
}
