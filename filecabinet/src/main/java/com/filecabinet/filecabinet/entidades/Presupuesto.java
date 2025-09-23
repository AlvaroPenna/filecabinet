package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.filecabinet.filecabinet.enums.EstadoAceptacion;

@Entity
@Getter
@Setter
public class Presupuesto extends DocumentoComercial {

    @Enumerated(EnumType.STRING)
    private EstadoAceptacion estadoAceptacion;
    
    private Date fechaAceptacion;

    // Getters y Setters...
}
