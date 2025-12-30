package com.filecabinet.filecabinet.dto;

import java.math.BigDecimal;
import java.util.Date;


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
    private Date fechaEmision;
    private BigDecimal total_bruto;
    private BigDecimal total_iva;
    private BigDecimal total_neto;
    private Integer tipo_iva;

    protected Long proyecto_id;
    protected Long cliente_id;
    protected Long usuario_id;

}
