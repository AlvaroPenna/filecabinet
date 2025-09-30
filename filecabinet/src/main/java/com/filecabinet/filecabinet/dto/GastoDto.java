package com.filecabinet.filecabinet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GastoDto extends DocumentoComercialDto {
    @NotBlank(message = "El proveedor no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre del proveedor debe tener entre 2 y 100 caracteres")
    private String proveedor;

    private String obra;
}