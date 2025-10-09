package com.filecabinet.filecabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String apellidos;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Size(min = 9, max = 9, message = "El teléfono debe tener 9 caracteres")
    @Pattern(regexp = "\\d+", message = "El teléfono solo puede contener números")
    private String telefono;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es correcto")
    private String email;

    @NotBlank(message = "El domicilio no puede estar vacío")
    private String domicilio;

    @NotNull(message = "El código postal no puede ser nulo")
    @Size(min = 5, max = 5, message = "El código postal debe tener 5 caracteres")
    private String codigoPostal;

    @NotBlank(message = "La provincia no puede estar vacía")
    private String provincia;

    @NotBlank(message = "La población no puede estar vacía")
    private String poblacion;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contraseña;

    private boolean activo;
}