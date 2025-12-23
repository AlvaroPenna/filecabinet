package com.filecabinet.filecabinet.dto;

import java.math.BigDecimal;
import java.util.List;

import com.filecabinet.filecabinet.enums.EstadoPago;

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

    private Long documento_id;
    private String numFactura;
    private EstadoPago estadoPago;
    private BigDecimal descuento;
    private List<DetalleDocumentoDto> detalles;
    
}
