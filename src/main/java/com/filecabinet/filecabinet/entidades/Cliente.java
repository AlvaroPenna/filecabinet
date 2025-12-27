package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter @Setter
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
    private Set<Factura> facturas = new HashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Presupuesto> presupuestos = new HashSet<>();

    @OneToMany(mappedBy = "cliente")
    private Set<Proyecto> proyecto = new HashSet<>();

    // --- ESTABILIDAD DEL SET ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id != null && Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}