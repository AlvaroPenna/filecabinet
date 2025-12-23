package com.filecabinet.filecabinet.entidades;

import java.sql.Date;
import java.util.Set;

import jakarta.persistence.Id;

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
@Table(name = "proyectos")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String codigoPostal;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column(nullable = true)
    private Date fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    @OneToMany(mappedBy = "proyecto")
    private Set<DocumentoComercial> documentos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) 
    private Usuario usuario;

    @OneToMany(mappedBy = "proyecto", fetch = FetchType.LAZY)
    private Set<ProyectoEmpleado> proyectoTrabajadores;
    
}
