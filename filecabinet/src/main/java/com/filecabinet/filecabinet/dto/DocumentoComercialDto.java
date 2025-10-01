package com.filecabinet.filecabinet.dto;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract class DocumentoComercialDto {
    private Long id;
    @NotBlank(message = "El número del documento no puede estar vacío")
    @Size(min = 1, max = 50, message = "El número del documento debe tener entre 1 y 50 caracteres")
    private String numero;
    
    @NotNull(message = "La fecha de emisión no puede ser nula")
    @PastOrPresent(message = "La fecha de emisión no puede ser en el futuro")
    private Date fechaEmision;
    
    @NotNull(message = "El precio sin IVA no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio sin IVA debe ser mayor o igual a 0")
    private BigDecimal precio_sin_iva;
    
    @NotNull(message = "El precio con IVA no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio con IVA debe ser mayor o igual a 0")
    private BigDecimal precio_con_iva;

    @NotNull(message = "El precio con IVA no puede ser nulo")
    private BigDecimal precio_iva;
    
    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;
}
