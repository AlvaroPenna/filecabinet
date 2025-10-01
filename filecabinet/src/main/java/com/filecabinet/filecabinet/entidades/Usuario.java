package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(nullable = true)
    private String apellidos;
    private String telefono;
    private String email;
    private String domicilio;
    private String codigoPostal;
    private String provincia;
    private String poblacion;
    private String passwordHash;
    @Column(nullable = true)
    private Date fechaRegistro;
    private boolean activo;

    // Relaciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Cliente> clientes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentoComercial> documentos;
}