package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Presupuesto;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long>{

    boolean existsByNumPresupuestoAndUsuarioId(String numPresupuesto, Long userId);
    
}
