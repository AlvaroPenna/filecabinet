package com.filecabinet.filecabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El CIF/NIF no puede estar vacío")
    @Size(min = 9, max = 9, message = "El CIF/NIF debe tener 9 caracteres")
    private String cifNif;

    @NotBlank(message = "La direccion no puede estar vacía")
    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Size(max = 100, message = "La ciudad no puede exceder los 100 caracteres")
    private String ciudad;

    @NotBlank(message = "El codigo postal no puede estar vacío")
    @Size(max = 10, message = "El código postal no puede exceder de los 5 caracteres")
    @Pattern(regexp = "\\d+", message = "El codigo postal solo puede contener números")
    private String codigoPostal;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser una dirección de correo válida")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Size(min = 9, max = 9, message = "El teléfono debe tener 9 caracteres")
    @Pattern(regexp = "\\d+", message = "El teléfono solo puede contener números")
    private String telefono;
}