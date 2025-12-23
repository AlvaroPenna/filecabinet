package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import com.filecabinet.filecabinet.enums.EstadoPago;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"detalles"})
@ToString(callSuper = true, exclude = {"detalles"})
@Table(name = "facturas")
@PrimaryKeyJoinColumn(name = "documento_id")
public class Factura extends DocumentoComercial {

    @Column(unique = true)
    private String numFactura;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;
    
    @Column(nullable = true)
    private BigDecimal descuento;

    @OneToMany(
        mappedBy = "documentoComercial", 
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<DetalleDocumento> detalles = new ArrayList<>();
}