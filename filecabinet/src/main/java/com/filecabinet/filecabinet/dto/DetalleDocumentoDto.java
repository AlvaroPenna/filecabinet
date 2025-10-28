package com.filecabinet.filecabinet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDocumentoDto {
    
    private Long id;
    
    @NotBlank(message = "El trabajo no puede estar vacío")
    private String trabajo;

    private String descripcion;
    
    @NotNull(message = "La cantidad es obligatoria.")
    @DecimalMin(value = "0.01", inclusive = true, message = "La cantidad debe ser mayor a cero.")
    private Double cantidad;
    
    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a cero.")
    private Double precio;

}