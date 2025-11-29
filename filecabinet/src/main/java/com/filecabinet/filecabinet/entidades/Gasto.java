package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gastos")
@Setter
@Getter
public class Gasto extends DocumentoComercial {

    @Column(unique = true)
    private String numGasto;

    private String proveedor;
    
}