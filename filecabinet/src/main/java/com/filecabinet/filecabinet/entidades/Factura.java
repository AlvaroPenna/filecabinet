package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import com.filecabinet.filecabinet.enums.EstadoPago;

@Entity
@Getter
@Setter
public class Factura extends DocumentoComercial {

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;
    
    private Date fechaVencimiento;
    @Column(nullable = true)
    private BigDecimal descuento;
}
