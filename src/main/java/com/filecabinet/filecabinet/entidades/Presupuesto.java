package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

import com.filecabinet.filecabinet.enums.EstadoAceptacion;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"detalles"})
@ToString(callSuper = true, exclude = {"detalles"})
@Table(name = "presupuesto")
@PrimaryKeyJoinColumn(name = "documento_id")
public class Presupuesto extends DocumentoComercial {

    @Column(unique = true)
    private String numPresupuesto;

    @Enumerated(EnumType.STRING)
    private EstadoAceptacion estadoAceptacion;
    
    private Date fechaAceptacion;

    @OneToMany(mappedBy = "documentoComercial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDocumento> detalles;

}
