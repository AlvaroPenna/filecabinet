package com.filecabinet.filecabinet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GastoDto extends DocumentoComercialDto {

    private Long documento_id;
    private String numGasto;
    private String proveedor;

    private String obra;
}