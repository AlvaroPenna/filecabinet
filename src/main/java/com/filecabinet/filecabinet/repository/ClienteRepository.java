package com.filecabinet.filecabinet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Cliente;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    Optional<Cliente> findByCif(String cif);

    Optional<Cliente> findByEmail(String email);

    List<Cliente>findByUsuarios_Id(Long userId);

    Optional<Cliente> findByIdAndUsuarios_Id(Long id, Long usuarioId);
    
}
