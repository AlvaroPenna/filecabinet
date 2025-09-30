package com.filecabinet.filecabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.filecabinet.filecabinet.entidades.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
}
