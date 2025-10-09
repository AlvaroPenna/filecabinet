package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Gasto extends DocumentoComercial {

    @Column(unique = true)
    private String numGasto;

    private String proveedor;
    
    @Column(nullable = true)
    private String obra;
}