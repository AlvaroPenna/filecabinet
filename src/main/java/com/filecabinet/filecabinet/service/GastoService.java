package com.filecabinet.filecabinet.service;

import com.filecabinet.filecabinet.dto.GastoDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.Gasto;
import com.filecabinet.filecabinet.entidades.Proyecto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.ClienteRepository;
import com.filecabinet.filecabinet.repository.GastoRepository;
import com.filecabinet.filecabinet.repository.ProyectoRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GastoService {

    private final GastoRepository gastosRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;

    public GastoService(GastoRepository gastosRepository,ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository, ProyectoRepository proyectoRepository) {
        this.gastosRepository = gastosRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public List<GastoDto> getAllGastos() {
        return gastosRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<GastoDto> getGastoById(Long id) {
        return gastosRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public GastoDto createGasto(GastoDto gastoDto, Long userId) {
        String numeroFactura = gastoDto.getNumGasto();

    if (gastosRepository.existsByNumGastoAndUsuarioId(numeroFactura, userId)) {
        throw new IllegalStateException("El gasto con número " + numeroFactura + " ya ha sido registrada.");
    }
    Gasto gasto = toEntity(gastoDto);
    if (userId != null) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        gasto.setUsuario(usuario);
    }

    Gasto savedGasto = gastosRepository.save(gasto);
    return toDto(savedGasto);
    }

    @Transactional
    public Optional<GastoDto> updateGasto(Long id, GastoDto gastoDetails) {
        return gastosRepository.findById(id).map(gasto -> {
            gasto.setNumGasto(gastoDetails.getNumGasto());
            gasto.setFechaEmision(gastoDetails.getFechaEmision());
            gasto.setTotal_bruto(gastoDetails.getTotal_bruto());
            gasto.setTotal_iva(gastoDetails.getTotal_iva());
            gasto.setTotal_neto(gastoDetails.getTotal_neto());
            gasto.setProveedor(gastoDetails.getProveedor());
            return toDto(gastosRepository.save(gasto));
        });
    }

    @Transactional
    public boolean deleteGasto(Long id) {
        if (gastosRepository.existsById(id)) {
            gastosRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Gasto toEntity(GastoDto dto) {
        Gasto entity = new Gasto();
        entity.setNumGasto(dto.getNumGasto());;
        entity.setFechaEmision(dto.getFechaEmision());
        entity.setTotal_bruto(dto.getTotal_bruto());
        entity.setTotal_iva(dto.getTotal_iva());
        entity.setTotal_neto(dto.getTotal_neto());
        entity.setProveedor(dto.getProveedor());
        entity.setTipo_iva(dto.getTipo_iva());
        if(dto.getCliente_id() != null){
            Cliente cliente = clienteRepository.findById(dto.getCliente_id())
                                .orElse(null);
            entity.setCliente(cliente); 
        }
        if (dto.getProyecto_id() != null) {
            Proyecto proyecto = proyectoRepository.findById(dto.getProyecto_id())
                                            .orElse(null);
            entity.setProyecto(proyecto);
        }
        return entity;
    }

    private GastoDto toDto(Gasto entity) {
        GastoDto dto = new GastoDto();
        dto.setNumGasto(entity.getNumGasto());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setTotal_bruto(entity.getTotal_bruto());
        dto.setTotal_iva(entity.getTotal_iva());
        dto.setTotal_neto(entity.getTotal_neto());
        dto.setProveedor(entity.getProveedor());
        dto.setTipo_iva(entity.getTipo_iva());
        if (entity.getCliente() != null) {
            dto.setCliente_id(entity.getCliente().getId());
        }
        
        if (entity.getProyecto() != null) {
            dto.setProyecto_id(entity.getProyecto().getId());
        }
        return dto;
    }
}