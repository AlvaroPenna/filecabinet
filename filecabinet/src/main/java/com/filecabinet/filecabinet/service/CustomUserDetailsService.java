package com.filecabinet.filecabinet.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.filecabinet.filecabinet.config.CustomUserDetails;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UsuarioRepository userRepository;

    public CustomUserDetailsService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Usuario user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + email));
        
        // ¡SOLUCIÓN! Devuelve tu clase personalizada, pasándole la entidad 'user'
        return new CustomUserDetails(
            user,
            // Asumo que el campo de la entidad es getPasswordHash()
            // Y que el rol es simple, como en tu código original
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) 
        );
    }
}
