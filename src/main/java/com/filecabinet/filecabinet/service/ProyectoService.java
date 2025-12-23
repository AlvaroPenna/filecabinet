package com.filecabinet.filecabinet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.ProyectoDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.Proyecto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.ClienteRepository;
import com.filecabinet.filecabinet.repository.ProyectoRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public ProyectoService(ProyectoRepository proyectoRepository,UsuarioRepository usuarioRepository,ClienteRepository clienteRepository){
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ProyectoDto> getAllProyectos(Long userId){
        return proyectoRepository.findByUsuarioId(userId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProyectoDto> getProyectoById(Long proyectoId, Long userId){
        return proyectoRepository.findByIdAndUsuarioId(proyectoId, userId).map(this::toDto);
    }

    @Transactional
    public ProyectoDto createProyecto(ProyectoDto proyectoDto, Long userId){
        String nombreProyecto = proyectoDto.getNombre();
        if(proyectoRepository.existsByNombreAndUsuarioId(nombreProyecto, userId)){
            throw new IllegalStateException("El proyecto con nombre " + nombreProyecto + " ya ha sido registrado.");
        }
        Proyecto proyecto = toEntity(proyectoDto, userId);
        if(userId != null){
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            proyecto.setUsuario(usuario);
        }
        Proyecto savedProyecto = proyectoRepository.save(proyecto);
        return toDto(savedProyecto);
    }

    @Transactional
    public Optional<ProyectoDto> updateProyecto(Long proyectoId, Long userId, ProyectoDto proyectoDetails){
        return proyectoRepository.findByIdAndUsuarioId(proyectoId, userId).map(proyecto ->{
            proyecto.setNombre(proyectoDetails.getNombre());
            proyecto.setDireccion(proyectoDetails.getDireccion());
            proyecto.setCiudad(proyectoDetails.getCiudad());
            proyecto.setCodigoPostal(proyectoDetails.getCodigoPostal());
            proyecto.setFechaInicio(proyectoDetails.getFechaInicio());
            proyecto.setFechaFin(proyectoDetails.getFechaFin());
            return toDto(proyectoRepository.save(proyecto));
        });

    }

    @Transactional
    public boolean deleteProyecto(Long proyectoId, Long userId){
        Optional<Proyecto> proyecto = proyectoRepository.findByIdAndUsuarioId(proyectoId, userId);
        if (proyecto.isPresent()) {
            proyectoRepository.delete(proyecto.get());
            return true;
        }
        return false;
    }

    private Proyecto toEntity(ProyectoDto dto, Long userId){
        if(dto == null){
            return null;
        }
        Proyecto entity = new Proyecto();
        entity.setNombre(dto.getNombre());
        entity.setDireccion(dto.getDireccion());
        entity.setCiudad(dto.getCiudad());
        entity.setCodigoPostal(dto.getCodigoPostal());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        if(dto.getCliente_id() != null){
            Cliente cliente = clienteRepository.findByIdAndUsuarios_Id(dto.getCliente_id(), userId)
                                .orElse(null);
            entity.setCliente(cliente);
        }
        return entity;
    }

    private ProyectoDto toDto(Proyecto entity){
        if(entity == null){
            return null;
        }
        ProyectoDto dto = new ProyectoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDireccion(entity.getDireccion());
        dto.setCiudad(entity.getCiudad());
        dto.setCodigoPostal(entity.getCodigoPostal());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        return dto;
    }
    
}
