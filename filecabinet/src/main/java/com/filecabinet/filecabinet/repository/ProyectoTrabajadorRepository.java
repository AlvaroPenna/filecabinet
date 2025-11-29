package com.filecabinet.filecabinet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.filecabinet.filecabinet.entidades.ProyectoTrabajador;

public interface ProyectoTrabajadorRepository extends JpaRepository<ProyectoTrabajador, Long>{
    
    List<ProyectoTrabajador> findByUsuario_Id(Long userId);

    Optional<ProyectoTrabajador> findByIdAndUsuarioId(Long proyectoId, Long userId);

}
