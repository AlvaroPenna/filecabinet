package com.filecabinet.filecabinet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.filecabinet.filecabinet.dto.FacturaDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.DetalleDocumento;
import com.filecabinet.filecabinet.entidades.Factura;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.FacturaRepository;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public FacturaService(FacturaRepository facturaRespository, ClienteService clienteService, UsuarioService usuarioService){
        this.facturaRepository = facturaRespository;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<FacturaDto> getAllFacturas() {
        return facturaRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FacturaDto> getFacturaById(Long id) {
        return facturaRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public FacturaDto createFactura(FacturaDto facturaDto) {
        Factura factura = toEntity(facturaDto);

        Long usuario_id =facturaDto.getUsuario_id();
        Usuario usuario = usuarioService.usuarioById(usuario_id);
        factura.setUsuario(usuario);

        Long cliente_id = facturaDto.getCliente_id();
        Cliente cliente = clienteService.clienteById(cliente_id);
        factura.setCliente(cliente);

        List<DetalleDocumento> detallesEntidades = mapDetallesToEntity(facturaDto.getDetalles(), factura);
        factura.setDetalles(detallesEntidades);

        Factura savedFactura = facturaRepository.save(factura);
        return toDto(savedFactura);
    }

    @Transactional
    public Optional<FacturaDto> updateFactura(Long id, FacturaDto facturaDetails) {
        return facturaRepository.findById(id).map(factura -> {
            factura.setNumFactura(facturaDetails.getNumFactura());
            factura.setEstadoPago(facturaDetails.getEstadoPago());
            factura.setDescuento(facturaDetails.getDescuento());
            factura.setFechaEmision(facturaDetails.getFechaEmision());
            factura.setTotal_sin_iva(facturaDetails.getTotal_sin_iva());
            factura.setTotal_iva(facturaDetails.getTotal_iva());
            factura.setTotal_con_iva(facturaDetails.getTotal_con_iva());
            factura.setDescripcion(facturaDetails.getDescripcion());
            return toDto(facturaRepository.save(factura));
        });
    }

    @Transactional
    public boolean deleteFactura(Long id) {
        if (facturaRepository.existsById(id)) {
            facturaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Factura toEntity(FacturaDto dto){
        Factura entity = new Factura();
        entity.setNumFactura(dto.getNumFactura());
        entity.setEstadoPago(dto.getEstadoPago());
        entity.setDescuento(dto.getDescuento());
        entity.setFechaEmision(dto.getFechaEmision());
        entity.setTotal_sin_iva(dto.getTotal_sin_iva());
        entity.setTotal_iva(dto.getTotal_iva());
        entity.setTotal_con_iva(dto.getTotal_con_iva());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    public FacturaDto toDto(Factura entity){
        FacturaDto dto = new FacturaDto();
        dto.setNumFactura(entity.getNumFactura());
        dto.setEstadoPago(entity.getEstadoPago());
        dto.setDescuento(entity.getDescuento());
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

        private List<DetalleDocumento> mapDetallesToEntity(List<DetalleDocumentoDto> dtos, Factura factura) {
            if (dtos == null) {
                return new ArrayList<>(); 
            }
    
        return dtos.stream()
                .map(dto -> {
                    DetalleDocumento entity = toDetalleEntity(dto);
                    entity.setDocumentoComercial(factura); 
                    return entity;
                })
                .collect(Collectors.toList()); 
        }
    
    @Transactional
    public Optional<FacturaDto> addDetalle(Long facturaId, DetalleDocumentoDto detalleDto) {
        return facturaRepository.findById(facturaId).map(factura -> {
        DetalleDocumento nuevoDetalle = toDetalleEntity(detalleDto);
        nuevoDetalle.setDocumentoComercial(factura); 
        factura.getDetalles().add(nuevoDetalle);
        Factura updatedFactura = facturaRepository.save(factura);
        return toDto(updatedFactura);
        });
    }

    @Transactional
    public Optional<FacturaDto> updateDetalle(Long facturaId, Long detalleId, DetalleDocumentoDto detalleDto) {
    return facturaRepository.findById(facturaId).map(factura -> {
        
        Optional<DetalleDocumento> detalleOptional = factura.getDetalles().stream()
            .filter(d -> d.getId() != null && d.getId().equals(detalleId))
            .findFirst();

        if (detalleOptional.isPresent()) {
            DetalleDocumento detalle = detalleOptional.get();

            detalle.setTrabajo(detalleDto.getTrabajo());
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecio(detalleDto.getPrecio());
            Factura updatedFactura = facturaRepository.save(factura);
            return toDto(updatedFactura);
        } else {
            throw new RuntimeException("Detalle con ID " + detalleId + " no encontrado en Factura " + facturaId);
        }
        });
    }

    @Transactional
    public boolean deleteDetalle(Long facturaId, Long detalleId) {
        return facturaRepository.findById(facturaId).map(factura -> {
            Optional<DetalleDocumento> detalleOptional = factura.getDetalles().stream()
                .filter(d -> d.getId() != null && d.getId().equals(detalleId))
                .findFirst();
            if (detalleOptional.isPresent()) {
                factura.getDetalles().remove(detalleOptional.get());
                facturaRepository.save(factura);
                return true;
            }
            return false;
        }).orElse(false);
    }

}
