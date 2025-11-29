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
        return proyectoRepository.findByUsuario_Id(userId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProyectoDto> getProyectoById(Long proyectoId, Long userId){
        return proyectoRepository.findByIdAndUsuarioId(proyectoId, userId).map(this::toDto);
    }

    @Transactional
    public ProyectoDto createProyecto(ProyectoDto proyectoDto, Long userId){
        Proyecto proyecto = toEntity(proyectoDto);
        if(userId != null){
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            proyecto.setUsuario(usuario);
        }
        Long clienteId = proyectoDto.getCliente_id();
            Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
            proyecto.setCliente(cliente);
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

    private Proyecto toEntity(ProyectoDto dto){
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
        return entity;
    }

    private ProyectoDto toDto(Proyecto entity){
        if(entity == null){
            return null;
        }
        ProyectoDto dto = new ProyectoDto();
        dto.setNombre(entity.getNombre());
        dto.setDireccion(entity.getDireccion());
        dto.setCiudad(entity.getCiudad());
        dto.setCodigoPostal(entity.getCodigoPostal());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        return dto;
    }
    
}
