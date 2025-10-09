package com.filecabinet.filecabinet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    private String numGasto;
    @NotBlank(message = "El proveedor no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre del proveedor debe tener entre 2 y 100 caracteres")
    private String proveedor;

    private String obra;
}