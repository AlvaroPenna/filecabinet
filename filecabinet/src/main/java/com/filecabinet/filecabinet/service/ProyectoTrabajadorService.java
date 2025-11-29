package com.filecabinet.filecabinet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.ProyectoTrabajadorDto;
import com.filecabinet.filecabinet.entidades.ProyectoTrabajador;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.ProyectoTrabajadorRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class ProyectoTrabajadorService {

    private final ProyectoTrabajadorRepository proyectoTrabajadorRepository;
    private final UsuarioRepository usuarioRepository;

    public ProyectoTrabajadorService(ProyectoTrabajadorRepository proyectoTrabajadorRepository,UsuarioRepository usuarioRepository){
        this.proyectoTrabajadorRepository= proyectoTrabajadorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ProyectoTrabajadorDto> getAllProyectoTrabajador(Long userId){
        return proyectoTrabajadorRepository.findByUsuario_Id(userId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProyectoTrabajadorDto> getTrabajadorById(Long proyectoTrabajadorId, Long userId){
        return proyectoTrabajadorRepository.findByIdAndUsuarioId(proyectoTrabajadorId, userId).map(this::toDto);
    }

    @Transactional
    public ProyectoTrabajadorDto createProyectoTrabajador(ProyectoTrabajadorDto proyectoTrabajadorDto, Long userId){
        ProyectoTrabajador proyectoTrabajador = toEntity(proyectoTrabajadorDto);
        if(userId != null){
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            proyectoTrabajador.setUsuario(usuario);
        }
        ProyectoTrabajador savedTrabajador = proyectoTrabajadorRepository.save(proyectoTrabajador);
        return toDto(savedTrabajador);
    }

    @Transactional
    public Optional<ProyectoTrabajadorDto> updateProyectoTrabajador(Long proyectoTrabajadorId, Long userId, ProyectoTrabajadorDto trabajadorDetails){
        return proyectoTrabajadorRepository.findByIdAndUsuarioId(proyectoTrabajadorId, userId).map(trabajador -> {
            trabajador.setId(trabajadorDetails.getId());
            trabajador.setDia(trabajadorDetails.getDia());
            trabajador.setHoras(trabajadorDetails.getHoras());
            return toDto(proyectoTrabajadorRepository.save(trabajador));
        });
    }

    @Transactional
    public boolean deleteTrabajador(Long trabajadorId, Long userId){
        Optional<ProyectoTrabajador> ProyectoTrabajador = proyectoTrabajadorRepository.findByIdAndUsuarioId(trabajadorId, userId);
        if (ProyectoTrabajador.isPresent()) {
            proyectoTrabajadorRepository.delete(ProyectoTrabajador.get());
            return true;
        }
        return false;
    }

    private ProyectoTrabajador toEntity(ProyectoTrabajadorDto dto){
        if(dto == null){
            return null;
        }
        ProyectoTrabajador entity = new ProyectoTrabajador();
        entity.setId(dto.getId());
        entity.setDia(dto.getDia());
        entity.setHoras(dto.getHoras());
        return entity;
    }

    private ProyectoTrabajadorDto toDto(ProyectoTrabajador entity){
        if(entity == null){
            return null;
        }
        ProyectoTrabajadorDto dto = new ProyectoTrabajadorDto();
        dto.setId(entity.getId());
        dto.setDia(entity.getDia());
        dto.setHoras(entity.getHoras());
        return dto;
    }
    
}
