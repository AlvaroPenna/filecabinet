package com.filecabinet.filecabinet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDocumentoDto {
    
    private Long id;
    private String trabajo;
    private String descripcion;
    private Double cantidad;
    private Double precioUnitario;
    private Double subTotal;

}