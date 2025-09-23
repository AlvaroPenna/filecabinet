package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Gasto;

import java.util.List;

@Repository
public interface GastosRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByUsuarioIdAndProveedor(Long usuarioId, String proveedor);
}
