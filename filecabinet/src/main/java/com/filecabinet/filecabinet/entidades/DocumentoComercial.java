package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name = "documentos_comerciales")
public abstract class DocumentoComercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fechaEmision;
    private BigDecimal total_sin_iva;
    private BigDecimal total_iva;
    private BigDecimal total_con_iva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_id")
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    protected Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true)
    protected Cliente cliente;

}
