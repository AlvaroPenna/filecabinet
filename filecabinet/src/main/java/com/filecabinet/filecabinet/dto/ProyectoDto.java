package com.filecabinet.filecabinet.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDto {
    private String id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private Date fechaInicio;
    private Date fechaFin;
    protected Long cliente_id;
}
