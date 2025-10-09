package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.DetalleDocumento;

@Repository
public interface DetalleDocumentoRepository extends JpaRepository<DetalleDocumento, Long> {
    
}
