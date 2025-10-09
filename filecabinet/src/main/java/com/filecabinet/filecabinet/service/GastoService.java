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
            gasto.setNumGasto(gastoDetails.getNumGasto());
            gasto.setFechaEmision(gastoDetails.getFechaEmision());
            gasto.setTotal_sin_iva(gastoDetails.getTotal_sin_iva());
            gasto.setTotal_iva(gastoDetails.getTotal_iva());
            gasto.setTotal_con_iva(gastoDetails.getTotal_con_iva());
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
        entity.setNumGasto(dto.getNumGasto());;
        entity.setFechaEmision(dto.getFechaEmision());
        entity.setTotal_sin_iva(dto.getTotal_sin_iva());
        entity.setTotal_iva(dto.getTotal_iva());
        entity.setTotal_con_iva(dto.getTotal_con_iva());
        entity.setDescripcion(dto.getDescripcion());
        entity.setProveedor(dto.getProveedor());
        entity.setObra(dto.getObra());
        return entity;
    }

    private GastoDto toDto(Gasto entity) {
        GastoDto dto = new GastoDto();
        dto.setNumGasto(entity.getNumGasto());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setTotal_sin_iva(entity.getTotal_sin_iva());
        dto.setTotal_iva(entity.getTotal_iva());
        dto.setTotal_con_iva(entity.getTotal_con_iva());
        dto.setDescripcion(entity.getDescripcion());
        dto.setProveedor(entity.getProveedor());
        dto.setObra(entity.getObra());
        return dto;
    }
}