package com.filecabinet.filecabinet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.UsuarioDto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioDto> getAllUsuarios(){
        return usuarioRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioDto> getUsuarioById(Long id){
        return usuarioRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public UsuarioDto createUsuario(UsuarioDto usuarioDto){
        Usuario usuario = toEntity(usuarioDto);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return toDto(savedUsuario);
    }

    @Transactional
    public Optional<UsuarioDto> updateUsuario(Long id, UsuarioDto usuarioDto){
        return usuarioRepository.findById(id).map(usuario ->{
            usuario.setNombre(usuarioDto.getNombre());
            usuario.setApellidos(usuarioDto.getApellidos());
            usuario.setTelefono(usuarioDto.getTelefono());
            usuario.setEmail(usuarioDto.getEmail());
            usuario.setDomicilio(usuarioDto.getDomicilio());
            usuario.setCodigoPostal(usuarioDto.getCodigoPostal());
            usuario.setProvincia(usuarioDto.getProvincia());
            usuario.setPoblacion(usuario.getPoblacion());
            usuario.setPasswordHash(usuario.getPasswordHash());
            usuario.setActivo(usuarioDto.isActivo());
            return toDto(usuarioRepository.save(usuario));
        });
    }

    @Transactional
    public boolean deleteUsuario(Long id){
        if(usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Usuario toEntity(UsuarioDto dto){
        if(dto == null){
            return null;
        }
        Usuario entity = new Usuario();
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setDomicilio(dto.getDomicilio());
        entity.setCodigoPostal(dto.getCodigoPostal());
        entity.setProvincia(dto.getProvincia());
        entity.setPoblacion(dto.getPoblacion());
        entity.setPasswordHash(dto.getContraseña());
        entity.setActivo(dto.isActivo());
        return entity;
    }

    private UsuarioDto toDto(Usuario usuario){
        if(usuario == null){
            return null;
        }
        UsuarioDto dto = new UsuarioDto();
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setTelefono(usuario.getTelefono());
        dto.setEmail(usuario.getEmail());
        dto.setDomicilio(usuario.getDomicilio());
        dto.setCodigoPostal(usuario.getCodigoPostal());
        dto.setProvincia(usuario.getProvincia());
        dto.setPoblacion(usuario.getPoblacion());
        dto.setContraseña(usuario.getPasswordHash());
        dto.setActivo(usuario.isActivo());
        return dto;
    }
    

}
