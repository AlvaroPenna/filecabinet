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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    protected Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true)
    protected Cliente cliente;

    private String numero;
    private Date fechaEmision;
    private BigDecimal precio_sin_iva;
    private BigDecimal precio_iva;
    private BigDecimal precio_con_iva;
    @Column( nullable = true)
    private String descripcion;

}
