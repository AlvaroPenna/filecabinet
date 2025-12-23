package com.filecabinet.filecabinet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.filecabinet.filecabinet.entidades.ProyectoEmpleado;

public interface ProyectoTrabajadorRepository extends JpaRepository<ProyectoEmpleado, Long>{
    
    List<ProyectoEmpleado> findByUsuarioId(Long userId);

    Optional<ProyectoEmpleado> findByIdAndUsuarioId(Long proyectoId, Long userId);

}
