package com.filecabinet.filecabinet.entidades;

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
    @Column(unique = true)
    private String cifNif;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String email;
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Factura> facturas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Presupuesto> presupuestos;

    @OneToMany(mappedBy = "cliente")
    private Set<Proyecto> proyecto;

}