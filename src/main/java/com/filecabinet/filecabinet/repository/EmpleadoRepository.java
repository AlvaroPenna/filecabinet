package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Empleado;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long>{
    
     List<Empleado> findByUsuarioId(Long userId);

    Optional<Empleado> findByIdAndUsuarioId(Long trabajadorId, Long userId);

    boolean existsByNifAndUsuarioId(String EmpleadoNif, Long userId);
}
