package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Proyecto;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long>{

    List<Proyecto> findByUsuarioId(Long userId);

    Optional<Proyecto> findByIdAndUsuarioId(Long proyectoId, Long userId);

    boolean existsByNombreAndUsuarioId(String nombreProyecto, Long userId);
    
}
