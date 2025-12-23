package com.filecabinet.filecabinet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.EmpleadoDto;
import com.filecabinet.filecabinet.entidades.Empleado;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.EmpleadoRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpleadoService(EmpleadoRepository trabajadorRepository, UsuarioRepository usuarioRepository){
        this.empleadoRepository = trabajadorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDto> getAllTrabajores(Long userId){
        return empleadoRepository.findByUsuarioId(userId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EmpleadoDto> getTrabajadorById(Long trabajadorId, Long userId){
        return empleadoRepository.findByIdAndUsuarioId(trabajadorId, userId).map(this::toDto);
    }

    @Transactional
    public EmpleadoDto createTrabajador(EmpleadoDto trabajadorDto, Long userId){
        String empleadoNif = trabajadorDto.getNif();
        if(empleadoRepository.existsByNifAndUsuarioId(empleadoNif, userId)){
            throw new IllegalStateException("El empleado con nif " + empleadoNif +" ya ha sido registrado");
        }
        Empleado trabajador = toEntity(trabajadorDto);
        if(userId != null){
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            trabajador.setUsuario(usuario);
        }
        Empleado savedTrabajador = empleadoRepository.save(trabajador);
        return toDto(savedTrabajador);
    }

    @Transactional
    public Optional<EmpleadoDto> updateTrabajador(Long trabajadorId, Long userId, EmpleadoDto trabajadorDetails){
        return empleadoRepository.findByIdAndUsuarioId(trabajadorId, userId).map(trabajador -> {
            trabajador.setId(trabajadorDetails.getId());
            trabajador.setNombre(trabajadorDetails.getNombre());
            trabajador.setApellidos(trabajadorDetails.getApellidos());
            trabajador.setNif(trabajadorDetails.getNif());
            trabajador.setTelefono(trabajadorDetails.getTelefono());
            trabajador.setEmail(trabajadorDetails.getEmail());
            trabajador.setDireccion(trabajadorDetails.getDireccion());
            trabajador.setCiudad(trabajadorDetails.getCiudad());
            trabajador.setCodigoPostal(trabajadorDetails.getCodigoPostal());
            return toDto(empleadoRepository.save(trabajador));
        });
    }

    @Transactional
    public boolean deleteTrabajador(Long trabajadorId, Long userId){
        Optional<Empleado> trabajador = empleadoRepository.findByIdAndUsuarioId(trabajadorId, userId);
        if (trabajador.isPresent()) {
            empleadoRepository.delete(trabajador.get());
            return true;
        }
        return false;
    }

    private Empleado toEntity(EmpleadoDto dto){
        if(dto == null){
            return null;
        }
        Empleado entity = new Empleado();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setNif(dto.getNif());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setDireccion(dto.getDireccion());
        entity.setCiudad(dto.getCiudad());
        entity.setCodigoPostal(dto.getCodigoPostal());
        return entity;
    }

    private EmpleadoDto toDto(Empleado entity){
        if(entity == null){
            return null;
        }
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellidos(entity.getApellidos());
        dto.setNif(entity.getNif());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        dto.setDireccion(entity.getDireccion());
        dto.setCiudad(entity.getCiudad());
        dto.setCodigoPostal(entity.getCodigoPostal());
        return dto;
    }
}
