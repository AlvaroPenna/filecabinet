package com.filecabinet.filecabinet.entidades;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(exclude = {"facturas", "presupuestos"})
@ToString(exclude = {"facturas", "presupuestos"})
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellidos;
    private String cif;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String email;
    private String telefono;

    @ManyToMany(mappedBy = "clientes")
    private Set<Usuario> usuarios = new HashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Factura> facturas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Presupuesto> presupuestos;

    @OneToMany(mappedBy = "cliente")
    private Set<Proyecto> proyecto;

}