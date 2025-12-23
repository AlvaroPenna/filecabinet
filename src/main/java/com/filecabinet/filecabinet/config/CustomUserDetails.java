package com.filecabinet.filecabinet.config;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.filecabinet.filecabinet.entidades.Usuario;

public class CustomUserDetails extends User {

    private final Long userId; 

    public CustomUserDetails(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
        super(
            usuario.getEmail(), 
            usuario.getPasswordHash(), 
            usuario.isActivo(),
            true, 
            true, 
            true, 
            authorities
        );
        
        this.userId = usuario.getId(); 
    }
    
    public Long getUserId() {
        return userId;
    }
}