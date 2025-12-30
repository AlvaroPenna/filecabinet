package com.filecabinet.filecabinet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.UsuarioDto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.enums.Rol;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioDto> getUsuarioById(Long id) {
        return usuarioRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public UsuarioDto createUsuario(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new IllegalArgumentException(
                    "El correo electrónico " + usuarioDto.getEmail() + " ya está registrado.");
        }

        if (usuarioDto.getContraseña() == null || usuarioDto.getContraseña().isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        Usuario usuario = toEntity(usuarioDto);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return toDto(savedUsuario);
    }

    @Transactional
    public Optional<UsuarioDto> updateUsuario(Long id, UsuarioDto usuarioDto) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (!usuario.getEmail().equals(usuarioDto.getEmail())
                    && usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
                throw new IllegalArgumentException("El email " + usuarioDto.getEmail() + " ya está en uso.");
            }
            usuario.setNombre(usuarioDto.getNombre());
            usuario.setApellidos(usuarioDto.getApellidos());
            usuario.setTelefono(usuarioDto.getTelefono());
            usuario.setEmail(usuarioDto.getEmail());
            usuario.setDomicilio(usuarioDto.getDomicilio());
            usuario.setCodigoPostal(usuarioDto.getCodigoPostal());
            usuario.setProvincia(usuarioDto.getProvincia());
            usuario.setPoblacion(usuarioDto.getPoblacion());
            return toDto(usuarioRepository.save(usuario));
        });
    }

    @Transactional
    public boolean deleteUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Usuario toEntity(UsuarioDto dto) {
        if (dto == null)
            return null;

        Usuario entity = new Usuario();
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setTelefono(dto.getTelefono());
        entity.setCif(dto.getCif());
        entity.setEmail(dto.getEmail());
        entity.setDomicilio(dto.getDomicilio());
        entity.setCodigoPostal(dto.getCodigoPostal());
        entity.setProvincia(dto.getProvincia());
        entity.setPoblacion(dto.getPoblacion());
        entity.setActivo(dto.isActivo());

        if (dto.getContraseña() != null) {
            entity.setPasswordHash(passwordEncoder.encode(dto.getContraseña()));
        }

        if (dto.getRol() == null) {
            entity.setRol(Rol.Usuario);
        } else {
            entity.setRol(dto.getRol());
        }

        return entity;
    }

    private UsuarioDto toDto(Usuario usuario) {
        if (usuario == null)
            return null;

        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setTelefono(usuario.getTelefono());
        dto.setCif(usuario.getCif());
        dto.setEmail(usuario.getEmail());
        dto.setDomicilio(usuario.getDomicilio());
        dto.setCodigoPostal(usuario.getCodigoPostal());
        dto.setProvincia(usuario.getProvincia());
        dto.setPoblacion(usuario.getPoblacion());
        dto.setActivo(usuario.isActivo());
        dto.setRol(usuario.getRol());
        return dto;
    }

    @Transactional(readOnly = true)
    public Usuario usuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}