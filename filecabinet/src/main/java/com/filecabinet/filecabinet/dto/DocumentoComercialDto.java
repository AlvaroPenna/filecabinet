package com.filecabinet.filecabinet.dto;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class DocumentoComercialDto {
    
    private Long id;
    
    @NotNull(message = "La fecha de emisión no puede ser nula")
    @PastOrPresent(message = "La fecha de emisión no puede ser en el futuro")
    private Date fechaEmision;
    
    @NotNull(message = "El precio sin IVA no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio sin IVA debe ser mayor o igual a 0")
    private BigDecimal total_sin_iva;
    
    @NotNull(message = "El precio con IVA no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio con IVA debe ser mayor o igual a 0")
    private BigDecimal total_con_iva;

    @NotNull(message = "El precio con IVA no puede ser nulo")
    private BigDecimal total_iva;
    
    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    protected Long cliente_id;
    protected Long usuario_id;

}
