package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filecabinet.filecabinet.entidades.Cliente;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    
}
