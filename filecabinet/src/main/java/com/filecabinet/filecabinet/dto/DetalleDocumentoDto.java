package com.filecabinet.filecabinet.dto;

import jakarta.validation.constraints.DecimalMin; // 🛑 Nueva Importación
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // 🛑 Nueva Importación
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDocumentoDto {
    
    private Long id; // Incluir para actualizaciones
    
    @NotBlank(message = "El trabajo no puede estar vacío")
    private String trabajo;
    
    // 🛑 CORRECCIÓN 1: Usar @NotNull y @DecimalMin para Doubles
    @NotNull(message = "La cantidad es obligatoria.")
    @DecimalMin(value = "0.01", inclusive = true, message = "La cantidad debe ser mayor a cero.")
    private Double cantidad;
    
    // 🛑 CORRECCIÓN 2: Usar @NotNull y @DecimalMin para Doubles
    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a cero.")
    private Double precio;

    // 🛑 CORRECCIÓN 3: Eliminar la referencia al padre DocumentoComercialDto
    // El detalle NUNCA debe incluir un DTO del padre. El padre (FacturaDto)
    // es quien incluye la lista de sus hijos (DetalleDocumentoDto).
    // private DocumentoComercialDto documentoComercialDto; // <-- ¡ELIMINADO!

}