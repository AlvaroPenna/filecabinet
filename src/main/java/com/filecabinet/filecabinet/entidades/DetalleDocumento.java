package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"documentoComercial"})
@Table(name = "detalle_documento")
public class DetalleDocumento{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String trabajo;
    private String descripcion;
    private Double cantidad;
    private Double precioUnitario;
    private Double subTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_comercial_id", nullable = false)
    private DocumentoComercial documentoComercial;

}
