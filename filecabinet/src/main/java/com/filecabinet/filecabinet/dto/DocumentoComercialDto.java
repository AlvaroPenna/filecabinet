package com.filecabinet.filecabinet.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public abstract class DocumentoComercialDto {
    private String numero;
    private Date fechaEmision;
    private BigDecimal precio_sin_iva;
    private BigDecimal precio_iva;
    private BigDecimal precio_con_iva;
    private String descripcion;
}
