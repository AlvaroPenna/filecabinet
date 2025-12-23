package com.filecabinet.filecabinet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.ProyectoEmpleadoDto;
import com.filecabinet.filecabinet.entidades.ProyectoEmpleado;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.EmpleadoRepository;
import com.filecabinet.filecabinet.repository.ProyectoRepository;
import com.filecabinet.filecabinet.repository.ProyectoTrabajadorRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class ProyectoEmpleadoService {

    private final ProyectoTrabajadorRepository proyectoTrabajadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EmpleadoRepository empleadoRepository;

    public ProyectoEmpleadoService(ProyectoTrabajadorRepository proyectoTrabajadorRepository,UsuarioRepository usuarioRepository,
        ProyectoRepository proyectoRepository,EmpleadoRepository empleadoRepository){
        this.proyectoTrabajadorRepository= proyectoTrabajadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProyectoEmpleadoDto> getAllProyectoTrabajador(Long userId){
        return proyectoTrabajadorRepository.findByUsuarioId(userId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProyectoEmpleadoDto> getTrabajadorById(Long proyectoTrabajadorId, Long userId){
        return proyectoTrabajadorRepository.findByIdAndUsuarioId(proyectoTrabajadorId, userId).map(this::toDto);
    }

    @Transactional
    public ProyectoEmpleadoDto createProyectoTrabajador(ProyectoEmpleadoDto proyectoTrabajadorDto, Long userId){
        ProyectoEmpleado proyectoTrabajador = toEntity(proyectoTrabajadorDto);
        if(userId != null){
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            proyectoTrabajador.setUsuario(usuario);
        }
        ProyectoEmpleado savedTrabajador = proyectoTrabajadorRepository.save(proyectoTrabajador);
        return toDto(savedTrabajador);
    }

    @Transactional
    public Optional<ProyectoEmpleadoDto> updateProyectoTrabajador(Long proyectoTrabajadorId, Long userId, ProyectoEmpleadoDto trabajadorDetails){
        return proyectoTrabajadorRepository.findByIdAndUsuarioId(proyectoTrabajadorId, userId).map(trabajador -> {
            trabajador.setId(trabajadorDetails.getId());
            trabajador.setDia(trabajadorDetails.getDia());
            trabajador.setHoras(trabajadorDetails.getHoras());
            return toDto(proyectoTrabajadorRepository.save(trabajador));
        });
    }

    @Transactional
    public boolean deleteTrabajador(Long trabajadorId, Long userId){
        Optional<ProyectoEmpleado> ProyectoTrabajador = proyectoTrabajadorRepository.findByIdAndUsuarioId(trabajadorId, userId);
        if (ProyectoTrabajador.isPresent()) {
            proyectoTrabajadorRepository.delete(ProyectoTrabajador.get());
            return true;
        }
        return false;
    }

    private ProyectoEmpleado toEntity(ProyectoEmpleadoDto dto){
        if(dto != null){
            ProyectoEmpleado entity = new ProyectoEmpleado();
            if(dto.getEmpleado_id() != null){
                entity.setEmpleado(empleadoRepository.findById(dto.getEmpleado_id()).orElse(null));
            }
            if(dto.getProyecto_id() != null){
                entity.setProyecto(proyectoRepository.findById(dto.getProyecto_id()).orElse(null));
            }       
            entity.setDia(dto.getDia());
            entity.setHoras(dto.getHoras());
            entity.setTareaRealizada(dto.getTareaRealizada());
            return entity;
            }
        return null;
    }

    private ProyectoEmpleadoDto toDto(ProyectoEmpleado entity){
        if(entity == null){
            return null;
        }
        ProyectoEmpleadoDto dto = new ProyectoEmpleadoDto();
        dto.setId(entity.getId());
        dto.setDia(entity.getDia());
        dto.setHoras(entity.getHoras());
        dto.setTareaRealizada(entity.getTareaRealizada());
        return dto;
    }
    
}
