package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.Id;

import java.util.Set;

import com.filecabinet.filecabinet.enums.Categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true)
    private String nif;

    private String telefono;
    private String email;
    private Categoria categoria;
    private String direccion;
    private String ciudad;
    private String codigoPostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "empleado")
    private Set<ProyectoEmpleado> proyectos;
}
