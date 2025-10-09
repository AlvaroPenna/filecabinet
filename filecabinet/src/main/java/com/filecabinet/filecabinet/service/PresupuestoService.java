package com.filecabinet.filecabinet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.filecabinet.filecabinet.dto.PresupuestoDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.DetalleDocumento;
import com.filecabinet.filecabinet.entidades.Presupuesto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.PresupuestoRepository;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public PresupuestoService(PresupuestoRepository presupuestoRepository, ClienteService clienteService, UsuarioService usuarioService){
        this.presupuestoRepository = presupuestoRepository;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
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
    public PresupuestoDto createPresupuesto(PresupuestoDto presupuestoDto){
        Presupuesto presupuesto = toEntity(presupuestoDto);
        Long usuario_id =presupuestoDto.getUsuario_id();
        Usuario usuario = usuarioService.usuarioById(usuario_id);
        presupuesto.setUsuario(usuario);

        Long cliente_id = presupuestoDto.getCliente_id();
        Cliente cliente = clienteService.clienteById(cliente_id);
        presupuesto.setCliente(cliente);

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
            presupuesto.setTotal_sin_iva(presupuestoDetails.getTotal_sin_iva());
            presupuesto.setTotal_iva(presupuestoDetails.getTotal_iva());
            presupuesto.setTotal_con_iva(presupuestoDetails.getTotal_con_iva());
            presupuesto.setDescripcion(presupuestoDetails.getDescripcion());
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
        entity.setTotal_sin_iva(dto.getTotal_sin_iva());
        entity.setTotal_iva(dto.getTotal_iva());
        entity.setTotal_con_iva(dto.getTotal_con_iva());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    public PresupuestoDto toDto(Presupuesto entity){
        PresupuestoDto dto = new PresupuestoDto();
        dto.setNumPresupuesto(entity.getNumPresupuesto());
        dto.setEstadoAceptacion(entity.getEstadoAceptacion());
        dto.setFechaAceptacion(entity.getFechaAceptacion());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setTotal_sin_iva(entity.getTotal_sin_iva());
        dto.setTotal_iva(entity.getTotal_iva());
        dto.setTotal_con_iva(entity.getTotal_con_iva());
        dto.setDescripcion(entity.getDescripcion());

        if(entity.getDetalles() != null){
            List<DetalleDocumentoDto> detallesDto = entity.getDetalles().stream()
                    .map(this::toDetalleDto)
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDto);
        }else{
            dto.setDetalles(new ArrayList<>());
        }

        return dto;
    }

    private DetalleDocumento toDetalleEntity(DetalleDocumentoDto dto) {
        DetalleDocumento entity = new DetalleDocumento();
        if (dto.getId() != null) {
            entity.setId(dto.getId()); 
        }
        entity.setTrabajo(dto.getTrabajo());
        entity.setCantidad(dto.getCantidad());
        entity.setPrecio(dto.getPrecio());
        return entity;
    }

    private DetalleDocumentoDto toDetalleDto(DetalleDocumento entity) {
        DetalleDocumentoDto dto = new DetalleDocumentoDto();
        dto.setId(entity.getId());
        dto.setTrabajo(entity.getTrabajo());
        dto.setCantidad(entity.getCantidad());
        dto.setPrecio(entity.getPrecio());
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

    @Transactional
    public Optional<PresupuestoDto> updateDetalle(Long PresupuestoId, Long detalleId, DetalleDocumentoDto detalleDto) {
    return presupuestoRepository.findById(PresupuestoId).map(presupuesto -> {
        
        Optional<DetalleDocumento> detalleOptional = presupuesto.getDetalles().stream()
            .filter(d -> d.getId() != null && d.getId().equals(detalleId))
            .findFirst();

        if (detalleOptional.isPresent()) {
            DetalleDocumento detalle = detalleOptional.get();

            detalle.setTrabajo(detalleDto.getTrabajo());
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecio(detalleDto.getPrecio());
            Presupuesto updatedPresupuesto = presupuestoRepository.save(presupuesto);
            return toDto(updatedPresupuesto);
        } else {
            throw new RuntimeException("Detalle con ID " + detalleId + " no encontrado en Factura " + PresupuestoId);
        }
        });
    }

    @Transactional
    public boolean deleteDetalle(Long presupuestoId, Long detalleId) {
        return presupuestoRepository.findById(presupuestoId).map(presupuesto -> {
            Optional<DetalleDocumento> detalleOptional = presupuesto.getDetalles().stream()
                .filter(d -> d.getId() != null && d.getId().equals(detalleId))
                .findFirst();
            if (detalleOptional.isPresent()) {
                presupuesto.getDetalles().remove(detalleOptional.get());
                presupuestoRepository.save(presupuesto);
                return true;
            }
            return false;
        }).orElse(false);
    }
    
}
