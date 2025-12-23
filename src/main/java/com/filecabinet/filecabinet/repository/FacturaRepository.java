package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long>{

    boolean existsByNumFacturaAndUsuarioId(String numGasto, Long userId);

}
