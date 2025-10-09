package com.filecabinet.filecabinet.dto;

import java.math.BigDecimal;
import java.util.List;

import com.filecabinet.filecabinet.enums.EstadoPago;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FacturaDto extends DocumentoComercialDto{

    @NotBlank(message = "El numero de factura no puede estar vacío")
    private String numFactura;
    private EstadoPago estadoPago;
    private BigDecimal descuento;
    private List<DetalleDocumentoDto> detalles;
    
}
