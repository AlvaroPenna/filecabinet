package com.filecabinet.filecabinet.service;

import com.filecabinet.filecabinet.dto.GastoDto;
import com.filecabinet.filecabinet.entidades.Gasto;
import com.filecabinet.filecabinet.repository.GastoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GastoService {

    private final GastoRepository gastosRepository;

    public GastoService(GastoRepository gastosRepository) {
        this.gastosRepository = gastosRepository;
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
    public GastoDto createGasto(GastoDto gastoDto) {
        Gasto gasto = toEntity(gastoDto);
        Gasto savedGasto = gastosRepository.save(gasto);
        return toDto(savedGasto);
    }

    @Transactional
    public Optional<GastoDto> updateGasto(Long id, GastoDto gastoDetails) {
        return gastosRepository.findById(id).map(gasto -> {
            gasto.setNumero(gastoDetails.getNumero());
            gasto.setFechaEmision(gastoDetails.getFechaEmision());
            gasto.setPrecio_sin_iva(gastoDetails.getPrecio_sin_iva());
            gasto.setPrecio_iva(gastoDetails.getPrecio_iva());
            gasto.setPrecio_con_iva(gastoDetails.getPrecio_con_iva());
            gasto.setDescripcion(gastoDetails.getDescripcion());
            gasto.setProveedor(gastoDetails.getProveedor());
            gasto.setObra(gastoDetails.getObra());
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

    // Métodos de conversión
    private Gasto toEntity(GastoDto dto) {
        Gasto entity = new Gasto();
        entity.setNumero(dto.getNumero());
        entity.setFechaEmision(dto.getFechaEmision());
        entity.setPrecio_sin_iva(dto.getPrecio_sin_iva());
        entity.setPrecio_iva(dto.getPrecio_iva());
        entity.setPrecio_con_iva(dto.getPrecio_con_iva());
        entity.setDescripcion(dto.getDescripcion());
        entity.setProveedor(dto.getProveedor());
        entity.setObra(dto.getObra());
        return entity;
    }

    private GastoDto toDto(Gasto entity) {
        GastoDto dto = new GastoDto();
        dto.setNumero(entity.getNumero());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setPrecio_sin_iva(entity.getPrecio_sin_iva());
        dto.setPrecio_iva(entity.getPrecio_iva());
        dto.setPrecio_con_iva(entity.getPrecio_con_iva());
        dto.setDescripcion(entity.getDescripcion());
        dto.setProveedor(entity.getProveedor());
        dto.setObra(entity.getObra());
        return dto;
    }
}