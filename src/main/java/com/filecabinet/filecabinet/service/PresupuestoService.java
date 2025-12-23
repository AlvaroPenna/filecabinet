package com.filecabinet.filecabinet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.filecabinet.filecabinet.dto.PresupuestoDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.DetalleDocumento;
import com.filecabinet.filecabinet.entidades.Presupuesto;
import com.filecabinet.filecabinet.entidades.Proyecto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.ClienteRepository;
import com.filecabinet.filecabinet.repository.PresupuestoRepository;
import com.filecabinet.filecabinet.repository.ProyectoRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProyectoRepository proyectoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository, UsuarioRepository usuarioRepository, 
        ClienteRepository clienteRepository, ProyectoRepository proyectoRepository){
        this.presupuestoRepository = presupuestoRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public List<PresupuestoDto> getAllPresupuestos(){
        return presupuestoRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PresupuestoDto> getPresupuestoById(Long id){
        return presupuestoRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public PresupuestoDto createPresupuesto(PresupuestoDto presupuestoDto, Long userId){
        String numPresupuesto = presupuestoDto.getNumPresupuesto();
        if(presupuestoRepository.existsByNumPresupuestoAndUsuarioId(numPresupuesto, userId)){
            throw new IllegalStateException("El presupuesto con número " + numPresupuesto + " ya ha sido registrada.");
        }
        Presupuesto presupuesto = toEntity(presupuestoDto);

        if (userId != null) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        presupuesto.setUsuario(usuario);
        }

        List<DetalleDocumento> detallesEntidades = mapDetallesToEntity(presupuestoDto.getDetalles(), presupuesto);
        presupuesto.setDetalles(detallesEntidades);

        Presupuesto savedPresupuesto = presupuestoRepository.save(presupuesto);
        return toDto(savedPresupuesto);
    }

    @Transactional
    public Optional<PresupuestoDto> updatePresupuesto(Long id, PresupuestoDto presupuestoDetails){
        return presupuestoRepository.findById(id).map(presupuesto -> {
            presupuesto.setNumPresupuesto(presupuestoDetails.getNumPresupuesto());
            presupuesto.setEstadoAceptacion(presupuestoDetails.getEstadoAceptacion());
            presupuesto.setFechaAceptacion(presupuestoDetails.getFechaAceptacion());
            presupuesto.setFechaEmision(presupuestoDetails.getFechaEmision());
            presupuesto.setTotal_bruto(presupuestoDetails.getTotal_bruto());
            presupuesto.setTotal_iva(presupuestoDetails.getTotal_iva());
            presupuesto.setTotal_neto(presupuestoDetails.getTotal_neto());

            List<DetalleDocumentoDto> detallesDto = presupuestoDetails.getDetalles();
            if (detallesDto != null && !detallesDto.isEmpty()) {
            Map<Long, DetalleDocumento> detallesExistentesMap = presupuesto.getDetalles().stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(DetalleDocumento::getId, Function.identity()));

            for (DetalleDocumentoDto detalleDto : detallesDto) {
                Long detalleId = detalleDto.getId();
                if (detalleId != null) {
                    DetalleDocumento detalle = detallesExistentesMap.get(detalleId);

                    if (detalle != null) {
                        detalle.setTrabajo(detalleDto.getTrabajo());
                        detalle.setDescripcion(detalleDto.getDescripcion());
                        detalle.setCantidad(detalleDto.getCantidad());
                        detalle.setPrecioUnitario(detalleDto.getPrecioUnitario());
                    } else {
                        throw new RuntimeException("Detalle con ID " + detalleId + " no encontrado en Presupuesto " + id);
                    }
                }
            }
        }
            return toDto(presupuestoRepository.save(presupuesto));
        });
    }

    @Transactional
    public boolean deletePresupuesto(Long id){
        if(presupuestoRepository.existsById(id)){
            presupuestoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Presupuesto toEntity(PresupuestoDto dto){
        Presupuesto entity = new Presupuesto();
        entity.setNumPresupuesto(dto.getNumPresupuesto());
        entity.setEstadoAceptacion(dto.getEstadoAceptacion());
        entity.setFechaAceptacion(dto.getFechaAceptacion());
        entity.setFechaEmision(dto.getFechaEmision());
        entity.setTotal_bruto(dto.getTotal_bruto());
        entity.setTotal_iva(dto.getTotal_iva());
        entity.setTotal_neto(dto.getTotal_neto());
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

    public PresupuestoDto toDto(Presupuesto entity){
        PresupuestoDto dto = new PresupuestoDto();
        dto.setNumPresupuesto(entity.getNumPresupuesto());
        dto.setEstadoAceptacion(entity.getEstadoAceptacion());
        dto.setFechaAceptacion(entity.getFechaAceptacion());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setTotal_bruto(entity.getTotal_bruto());
        dto.setTotal_iva(entity.getTotal_iva());
        dto.setTotal_neto(entity.getTotal_neto());

        if (entity.getCliente() != null) {
            dto.setCliente_id(entity.getCliente().getId());
        }
    
        if (entity.getProyecto() != null) {
            dto.setProyecto_id(entity.getProyecto().getId());
        }
        return dto;
    }

    private DetalleDocumento toDetalleEntity(DetalleDocumentoDto dto) {
        DetalleDocumento entity = new DetalleDocumento();
        if (dto.getId() != null) {
            entity.setId(dto.getId()); 
        }
        entity.setTrabajo(dto.getTrabajo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCantidad(dto.getCantidad());
        entity.setPrecioUnitario(dto.getPrecioUnitario());
        entity.setSubTotal(dto.getSubTotal());
        return entity;
    }


    private DetalleDocumentoDto toDetalleDto(DetalleDocumento entity) {
        DetalleDocumentoDto dto = new DetalleDocumentoDto();
        dto.setId(entity.getId());
        dto.setTrabajo(entity.getTrabajo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setCantidad(entity.getCantidad());
        dto.setPrecioUnitario(entity.getPrecioUnitario());
        dto.setSubTotal(entity.getSubTotal());
        return dto;
    }

    private List<DetalleDocumento> mapDetallesToEntity(List<DetalleDocumentoDto> dtos, Presupuesto presupuesto) {
            if (dtos == null) {
                return new ArrayList<>(); 
            }
    
        return dtos.stream()
                .map(dto -> {
                    DetalleDocumento entity = toDetalleEntity(dto);
                    entity.setDocumentoComercial(presupuesto); 
                    return entity;
                })
                .collect(Collectors.toList()); 
    }

    @Transactional
    public Optional<PresupuestoDto> addDetalle(Long PresupuestoId, DetalleDocumentoDto detalleDto) {
        return presupuestoRepository.findById(PresupuestoId).map(presupuesto -> {
        DetalleDocumento nuevoDetalle = toDetalleEntity(detalleDto);
        nuevoDetalle.setDocumentoComercial(presupuesto); 
        presupuesto.getDetalles().add(nuevoDetalle);
        Presupuesto updatedPresupuesto = presupuestoRepository.save(presupuesto);
        return toDto(updatedPresupuesto);
        });
    }
    
}
