package com.filecabinet.filecabinet.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException; // <--- FALTABA ESTE IMPORT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filecabinet.filecabinet.config.CustomUserDetails;
import com.filecabinet.filecabinet.dto.DetalleDocumentoDto;
import com.filecabinet.filecabinet.dto.FacturaDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.DetalleDocumento;
import com.filecabinet.filecabinet.entidades.Factura;
import com.filecabinet.filecabinet.entidades.Proyecto;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.ClienteRepository;
import com.filecabinet.filecabinet.repository.FacturaRepository;
import com.filecabinet.filecabinet.repository.ProyectoRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;
import com.filecabinet.filecabinet.util.FacturaExcelExporter;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProyectoRepository proyectoRepository;

    public FacturaService(FacturaRepository facturaRespository, UsuarioRepository usuarioRepository, 
        ClienteRepository clienteRepository, ProyectoRepository proyectoRepository){
        this.facturaRepository = facturaRespository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.proyectoRepository = proyectoRepository;
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
    public FacturaDto createFactura(FacturaDto facturaDto, Long userId) {
        String numFactura = facturaDto.getNumFactura();
        if(facturaRepository.existsByNumFacturaAndUsuarioId(numFactura, userId)){
            throw new IllegalStateException("La factura con número " + numFactura + " ya ha sido registrada");
        }
        Factura factura = toEntity(facturaDto);

        if (userId != null) {
            Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
            factura.setUsuario(usuario);
        }
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
            factura.setTotal_bruto(facturaDetails.getTotal_bruto());
            factura.setTotal_iva(facturaDetails.getTotal_iva());
            factura.setTotal_neto(facturaDetails.getTotal_neto());
            List <DetalleDocumentoDto> detallesDto = facturaDetails.getDetalles();
            if(detallesDto != null){
                List<DetalleDocumento> detallesExistentes = factura.getDetalles();
                for(DetalleDocumentoDto detalleDto : detallesDto){
                    Long detalleid = detalleDto.getId();
                    if(detalleid != null){
                        Optional<DetalleDocumento> detalleOptional = detallesExistentes.stream()
                        .filter(d -> d.getId() != null && d.getId().equals(detalleid))
                        .findFirst();
                        
                        if(detalleOptional.isPresent()){
                            DetalleDocumento detalle = detalleOptional.get();
                            detalle.setTrabajo(detalleDto.getTrabajo());
                            detalle.setDescripcion(detalleDto.getDescripcion());
                            detalle.setCantidad(detalleDto.getCantidad());
                            detalle.setPrecioUnitario(detalleDto.getPrecioUnitario());
                        } else {
                            throw new RuntimeException("Detalle con ID " + detalleid + " no encontrado en Factura " + id);
                        }
                        // Nota: factura.setDetalles no es estrictamente necesario si modificas la lista existente referenciada, 
                        // pero no hace daño dejarlo.
                        factura.setDetalles(detallesExistentes);
                    }
                }
            }
            
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

    public FacturaDto toDto(Factura entity) {
        FacturaDto dto = new FacturaDto();
        dto.setId(entity.getId()); // Es útil devolver el ID de la factura
        dto.setNumFactura(entity.getNumFactura());
        dto.setEstadoPago(entity.getEstadoPago());
        dto.setDescuento(entity.getDescuento());
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

        // CORRECCIÓN: Mapear los detalles de vuelta al DTO
        if (entity.getDetalles() != null) {
            List<DetalleDocumentoDto> detallesDto = entity.getDetalles().stream()
                .map(this::toDetalleDto)
                .collect(Collectors.toList());
            dto.setDetalles(detallesDto);
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

    // CORRECCIÓN: Descomentar este método, es necesario para toDto
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

    private List<DetalleDocumento> mapDetallesToEntity(List<DetalleDocumentoDto> dtos, Factura factura) {
        if (dtos == null) {
            return new ArrayList<>(); 
        }
        return dtos.stream()
            .map(dto -> {
                DetalleDocumento entity = toDetalleEntity(dto);
                entity.setDocumentoComercial(factura); 
                return entity;
            }).collect(Collectors.toList()); 
    }

    @Transactional(readOnly = true)
    public void generarExcel(Long id, HttpServletResponse response, CustomUserDetails userDetails) throws IOException {
        // 1. Buscamos la factura
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // 2. VALIDACIÓN DE SEGURIDAD (Dueño O Admin):
        
        // A) Verificamos si es el dueño
        boolean esDuenio = factura.getUsuario() != null && 
                           factura.getUsuario().getId().equals(userDetails.getUserId());
        
        // B) Verificamos si tiene rol de ADMIN
        boolean esAdmin = userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si NO es dueño Y TAMPOCO es admin, lanzamos error
        if (!esDuenio && !esAdmin) {
            throw new AccessDeniedException("No tienes permiso para descargar esta factura.");
        }

        // 3. Configuración de respuesta HTTP
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Factura_" + factura.getNumFactura() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // 4. Generar Excel
        FacturaExcelExporter exporter = new FacturaExcelExporter(factura);
        exporter.export(response);
    }
}