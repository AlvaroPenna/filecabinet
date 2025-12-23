package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.filecabinet.filecabinet.enums.Rol;

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
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @ManyToMany
    @JoinTable(
        name = "rel_usuario_cliente",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Empleado> trabajadores;

}