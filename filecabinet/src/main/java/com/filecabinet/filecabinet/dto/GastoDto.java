package com.filecabinet.filecabinet.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GastoDto extends DocumentoComercialDto {
    private Long id;
    private String proveedor;
    private String obra;
}