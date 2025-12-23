package com.filecabinet.filecabinet.dto;

import java.util.Date;
import java.util.List;

import com.filecabinet.filecabinet.enums.EstadoAceptacion;

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
public class PresupuestoDto extends DocumentoComercialDto{

    private Long documento_id;
    private String numPresupuesto;
    private EstadoAceptacion estadoAceptacion;
    private Date fechaAceptacion;
    private List<DetalleDocumentoDto> detalles;
    
}
